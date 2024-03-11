package com.dp2.reader.impl;

import com.dp2.reader.AbstractReader;
import com.dp2.util.IOUtil;
import com.dp2.util.ReaderUtil;
import com.dp2.util.Types;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV读取
 *
 * @author 6tail
 */
public class CsvReader extends AbstractReader implements Closeable {
  /**
   * 回车符
   */
  public static String CR = "\r";

  /**
   * 换行符
   */
  public static String LF = "\n";

  /**
   * 列间隔符
   */
  public static String SPACE = ",";

  /**
   * 双引号
   */
  public static final String QUOTE = "\"";

  private BufferedReader reader;

  /**
   * 缓存
   */
  private final StringBuffer buffer = new StringBuffer();

  /**
   * 标识数据内容是否包含在引号之间
   */
  private boolean quoted = false;

  public CsvReader(File file) {
    super(file);
  }

  /**
   * close
   */
  public void close() {
    IOUtil.closeQuietly(reader);
  }

  /**
   * 按间隔符拆分字符串
   *
   * @param s 字符串
   * @return 拆分后的列表
   */
  private List<String> split(String s) {
    List<String> l = new ArrayList<String>();
    String r = s;
    while (r.contains(SPACE)) {
      int space = r.indexOf(SPACE);
      l.add(r.substring(0, space));
      r = r.substring(space + SPACE.length());
    }
    l.add(r);
    return l;
  }

  private void endQuote(List<String> l, String s) {
    buffer.append(SPACE);
    buffer.append(s);
    l.add(buffer.toString());
    buffer.setLength(0);
    quoted = false;
  }

  /**
   * 按照CSV格式规范将拆散的本来是一列的数据合并
   *
   * @param segments 拆散的列
   * @return 合并后的列
   */
  private List<String> combine(List<String> segments) {
    List<String> l = new ArrayList<String>();
    for (String seg : segments) {
      String t = seg.replace(QUOTE + QUOTE, "");
      if (t.startsWith(QUOTE)) {
        if (!quoted) {
          quoted = true;
          buffer.append(seg);
          if (t.endsWith(QUOTE)) {
            if (!t.equals(QUOTE)) {
              l.add(buffer.toString());
              buffer.setLength(0);
              quoted = false;
            }
          }
        } else {
          if (t.equals(QUOTE)) {
            endQuote(l, seg);
          } else {
            l.add(buffer.toString());
            buffer.setLength(0);
            buffer.append(seg);
          }
        }
      } else if (t.endsWith(QUOTE)) {
        if (quoted) {
          endQuote(l, seg);
        } else {
          l.add(seg);
        }
      } else {
        if (quoted) {
          buffer.append(SPACE);
          buffer.append(seg);
        } else {
          l.add(seg);
        }
      }
    }
    return l;
  }

  protected String readLine() {
    try {
      return reader.readLine();
    } catch (IOException ignore) {
      close();
    }
    return null;
  }

  /**
   * 读取下一行
   *
   * @return 一行数据，如果没有下一行，返回null
   */
  public List<String> nextLine() {
    buffer.setLength(0);
    quoted = false;
    String line = readLine();
    if (null == line) {
      return null;
    }
    List<String> l = new ArrayList<String>();
    StringBuilder r = new StringBuilder(line);
    if (!r.toString().contains(QUOTE)) {
      l.addAll(split(r.toString()));
    } else {
      String t = r.toString().replace(QUOTE + QUOTE, "");
      int count = t.length() - t.replace(QUOTE, "").length();
      while (count % 2 == 1) {
        String nextLine = readLine();
        if (null == nextLine) {
          nextLine = "\"";
        }
        r.append(CR).append(LF).append(nextLine);
        String nt = nextLine.replace(QUOTE + QUOTE, "");
        int len = nt.length() - nt.replace(QUOTE, "").length();
        count += len;
      }
      List<String> segments = split(r.toString());
      l.addAll(combine(segments));
    }
    List<String> cols = new ArrayList<String>();
    for (String col : l) {
      if (col.equals(QUOTE)) {
        col = "";
      } else if (col.equals(QUOTE + QUOTE)) {
        col = "";
      } else if (col.startsWith(QUOTE) && col.endsWith(QUOTE)) {
        col = col.replace(QUOTE + QUOTE, QUOTE);
        col = col.substring(QUOTE.length());
        col = col.substring(0, col.length() - QUOTE.length());
      }
      cols.add(col);
    }
    return cols;
  }

  public void load() throws IOException {
    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ReaderUtil.getCharset(file)));
    stop = false;
    quoted = false;
    buffer.setLength(0);
  }

  @Override
  public boolean support() {
    return true;
  }

  public String type() {
    return Types.CSV;
  }
}
