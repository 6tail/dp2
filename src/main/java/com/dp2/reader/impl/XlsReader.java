package com.dp2.reader.impl;

import com.dp2.reader.AbstractOfficeReader;
import com.dp2.util.DoubleFixUtil;
import com.dp2.util.Types;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * xls文件读取(大文件读取内存疯涨)
 *
 * @author 6tail
 */
@Deprecated
public class XlsReader extends AbstractOfficeReader {
  /**
   * xls Sheet
   */
  private HSSFSheet sheet = null;

  /**
   * 当前行，从0开始计
   */
  private int rowCount = 0;

  /**
   * 它认为的最后一行，从0开始计，如果为-1则不以它为准
   */
  private int lastRow = -1;

  /**
   * 是否已加载
   */
  private boolean loaded = false;

  public XlsReader(File file) {
    super(file);
  }

  public void load() throws IOException {
    if (!loaded) {
      HSSFWorkbook wbs = new HSSFWorkbook(new FileInputStream(file));
      sheet = wbs.getSheetAt(0);
      lastRow = sheet.getLastRowNum();
      loaded = true;
    }
    stop = false;
    rowCount = 0;
  }

  private List<String> getLine(int index) {
    if (lastRow > -1) {
      if (index > lastRow) {
        return null;
      }
    }
    HSSFRow row = sheet.getRow(index);
    if (null == row) {
      if (-1 == lastRow) {
        return null;
      } else {
        return new ArrayList<String>();
      }
    }
    int l = row.getLastCellNum();
    List<String> rs = new ArrayList<String>();
    for (int i = 0; i < l; i++) {
      String v = "";
      HSSFCell cell = row.getCell(i);
      if (null != cell) {
        switch (cell.getCellTypeEnum()) {
          case NUMERIC:
            double value = cell.getNumericCellValue();
            try {
              HSSFCellStyle style = cell.getCellStyle();
              String formatString = String.valueOf(style.getDataFormatString());
              if (formatString.endsWith("_ ")) {
                v = DoubleFixUtil.fix(value);
              } else {
                short format = cell.getCellStyle().getDataFormat();
                SimpleDateFormat sdf = null;
                if (176 == format || format == 14 || format == 31 || format == 57 || format == 58) {
                  sdf = new SimpleDateFormat("yyyy-MM-dd");
                } else if (format == 20 || format == 32) {
                  sdf = new SimpleDateFormat("HH:mm");
                }
                if (null != sdf) {
                  v = sdf.format(DateUtil.getJavaDate(value));
                }
                int len = v.length();
                if (10 != len && 5 != len) {
                  throw new IllegalArgumentException();
                }
              }
            } catch (Exception e) {
              v = DoubleFixUtil.fix(value);
            }
            break;
          case STRING:
            v = String.valueOf(cell.getStringCellValue()).trim();
            break;
          case BOOLEAN:
            v = cell.getBooleanCellValue() + "";
            break;
          case FORMULA:
            try {
              v = DoubleFixUtil.fix(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
              v = String.valueOf(cell.getRichStringCellValue());
            }
            break;
          default:
            v = "";
            break;
        }
      }
      rs.add(v);
    }
    return rs;
  }

  public List<String> nextLine() {
    if (stop) {
      return null;
    }
    return getLine(rowCount++);
  }

  public String type() {
    return Types.XLS;
  }
}
