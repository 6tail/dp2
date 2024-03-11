package com.dp2.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * .xlsx格式文件读取，解决poi读取大文件引起内存疯涨溢出的问题，并且速度比poi快
 *
 * @author 6tail
 */
public class Excel2007Reader extends DefaultHandler implements Runnable {
  /**
   * 日期格式
   */
  private static final Set<String> DATE_FORMATS = new HashSet<String>() {
    private static final long serialVersionUID = 1L;

    {
      add("m/d/yy");
      add("m/d");
      add("mm/dd/yy");
      add("yy/m/d");
      add("yyyy/m/d");
      add("yyyy/mm/dd");
    }
  };
  /**
   * 内容类型：未设置
   */
  private static final int CELL_TYPE_NONE = 0;
  /**
   * 内容类型：字符串(s)
   */
  private static final int CELL_TYPE_STRING = 1;
  /**
   * 内容类型：其他未知类型
   */
  private static final int CELL_TYPE_UNKNOWN = -1;
  private static final String TAG_ROW = "row";
  private static final String TAG_VALUE = "v";
  private static final String TAG_CELL = "c";
  private static final String TAG_STRING = "s";
  private static final String TAG_T = "t";
  /**
   * 每次读取的行数
   */
  public static int queueSize = 5000;
  private boolean end;
  private boolean stop;
  /**
   * 待读取内容类型
   */
  private int nextCellType = CELL_TYPE_NONE;
  private boolean tElement;
  /**
   * 上一次的内容
   */
  private String lastContents;
  /**
   * 日期格式
   */
  private String dateFormat;
  private String prefPos = null;
  private String currentPos = null;
  private final File file;
  private StylesTable stylesTable;
  /**
   * 共享字符表
   */
  private SharedStringsTable sst;
  private final List<String> rowData = new ArrayList<String>();
  private final Queue<List<String>> rowQueue = new LinkedBlockingQueue<List<String>>(queueSize);

  public Excel2007Reader(File file) {
    this.file = file;
  }

  public void run() {
    InputStream sheet = null;
    try {
      OPCPackage pkg = OPCPackage.open(file);
      XSSFReader r = new XSSFReader(pkg);
      stylesTable = r.getStylesTable();
      sst = r.getSharedStringsTable();
      sheet = r.getSheetsData().next();
      XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
      parser.setContentHandler(this);
      parser.parse(new InputSource(sheet));
    } catch (Exception ignore) {
    } finally {
      IOUtil.closeQuietly(sheet);
      end = true;
    }
  }

  /**
   * 根据列标计算横坐标，A对应0,AA对应26
   *
   * @param label 列标，A、B、C、AB之类的
   * @return 横坐标
   */
  private int getPos(String label) {
    char[] letters = label.toUpperCase().toCharArray();
    int n = 0;
    int size = letters.length;
    for (int i = 0; i < size; i++) {
      int p = letters[size - i - 1];
      p -= 64;
      if (0 == i) {
        p -= 1;
      }
      n += (int) (p * Math.pow(26, i));
    }
    return n;
  }

  private int diffPos() {
    return getPos(currentPos.toUpperCase()) - getPos(prefPos.toUpperCase()) - 1;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) {
    while (rowQueue.size() >= queueSize) {
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      if (stop) {
        throw new RuntimeException("中止读取");
      }
    }
    if (TAG_ROW.equals(name)) {
      prefPos = "@";
    } else if (TAG_CELL.equals(name)) {
      dateFormat = null;
      String cellType = attributes.getValue("t");
      String cellStyle = attributes.getValue("s");
      String pos = attributes.getValue("r");
      currentPos = pos.replaceAll("\\d+", "");
      if (null == cellType) {
        nextCellType = CELL_TYPE_NONE;
      } else if (TAG_STRING.equals(cellType)) {
        nextCellType = CELL_TYPE_STRING;
      } else {
        nextCellType = CELL_TYPE_UNKNOWN;
      }
      if (null != cellStyle) {
        int cs = Integer.parseInt(cellStyle);
        XSSFCellStyle style = stylesTable.getStyleAt(cs);
        String format = style.getDataFormatString();
        if (DATE_FORMATS.contains(format)) {
          dateFormat = "yyyy-MM-dd";
        }
      }
    }
    //当元素为t时
    tElement = TAG_T.equals(name);
    // 置空
    lastContents = "";
  }

  @Override
  public void endElement(String uri, String localName, String name) {
    if (stop) {
      throw new RuntimeException("中止读取");
    }
    if (CELL_TYPE_STRING == nextCellType) {
      try {
        int idx = Integer.parseInt(lastContents);
        lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      } catch (Exception ignore) {
      }
    }
    if (tElement) {
      for (int i = 0, j = diffPos(); i < j; i++) {
        rowData.add("");
      }
      prefPos = currentPos;
      String value = lastContents.trim();
      rowData.add(value);
      tElement = false;
    } else if (TAG_VALUE.equals(name)) {
      //值
      for (int i = 0, j = diffPos(); i < j; i++) {
        rowData.add("");
      }
      prefPos = currentPos;
      String value = lastContents.trim();
      int length = value.length();
      if (length > 0) {
        //日期格式处理
        if (null != dateFormat) {
          Date date = null;
          try {
            date = HSSFDateUtil.getJavaDate(Double.parseDouble(value));
          } catch (Exception e) {
            if (length == 10) {
              try {
                date = HSSFDateUtil.parseYYYYMMDDDate(value);
              } catch (Exception ignore) {
              }
            }
          }
          if (null != date) {
            value = new SimpleDateFormat(dateFormat).format(date);
          }
        } else if (CELL_TYPE_NONE == nextCellType) {
          //尝试按数字处理
          try {
            value = DoubleFixUtil.fix(Double.parseDouble(value));
          } catch (Exception ignore) {
          }
        }
      }
      rowData.add(value);
    } else if (TAG_ROW.equals(name)) {
      List<String> row = new ArrayList<String>(rowData.size());
      row.addAll(rowData);
      rowQueue.offer(row);
      rowData.clear();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    lastContents += new String(ch, start, length);
  }

  public void load() {
    end = false;
    stop = false;
    nextCellType = CELL_TYPE_NONE;
    tElement = false;
    lastContents = null;
    dateFormat = null;
    prefPos = null;
    currentPos = null;
    rowData.clear();
    rowQueue.clear();
    new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy()).execute(this);
  }

  public List<String> nextLine() {
    if (stop) {
      return null;
    }
    List<String> row = rowQueue.poll();
    while (null == row) {
      if (stop || end) {
        break;
      }
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      row = rowQueue.poll();
    }
    return row;
  }

  public void stop() {
    stop = true;
  }
}
