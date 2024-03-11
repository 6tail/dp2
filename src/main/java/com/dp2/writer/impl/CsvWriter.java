package com.dp2.writer.impl;

import com.dp2.node.INode;
import com.dp2.reader.impl.CsvReader;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV导出
 *
 * @author 6tail
 */
public class CsvWriter extends AbstractWriter {
  /**
   * 默认的文件编码
   */
  public static final String DEFAULT_ENCODE = "GBK";

  /**
   * 文件编码
   */
  public static String ENCODE = DEFAULT_ENCODE;

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

  protected List<List<String>> lines = new ArrayList<List<String>>();

  public CsvWriter(File file) {
    super(file);
  }

  public void write(int row, int col, INode node) {
    int rows = lines.size();
    int rowDiff = row - rows + 1;
    for (int i = 0; i < rowDiff; i++) {
      lines.add(new ArrayList<String>());
    }
    List<String> line = lines.get(row);
    int cols = line.size();
    int colDiff = col - cols + 1;
    for (int i = 0; i < colDiff; i++) {
      line.add("");
    }
    if (null != node.getValue()) {
      line.set(col, node.getValue());
    }
  }

  public void save(File file) throws IOException {
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODE));
      for (List<String> line : lines) {
        StringBuilder s = new StringBuilder();
        for (int i = 0, j = line.size(); i < j; i++) {
          String o = line.get(i);
          String ro = o;
          if (null == o) {
            ro = "";
          } else {
            boolean needQuote = false;
            if (o.contains(QUOTE)) {
              ro = o.replace(QUOTE, QUOTE + QUOTE);
              needQuote = true;
            }
            if (o.contains(CR) || o.contains(LF) || o.contains(SPACE)) {
              needQuote = true;
            }
            if (needQuote) {
              ro = QUOTE + ro + QUOTE;
            }
          }
          s.append(ro);
          if (i < j - 1) {
            s.append(SPACE);
          }
        }
        s.append(CR);
        s.append(LF);
        writer.write(s.toString());
      }
      writer.flush();
    } finally {
      IOUtil.closeQuietly(writer);
    }
  }

  public void load() throws IOException {
    CsvReader reader = null;
    try {
      reader = new CsvReader(file);
      reader.load();
      lines.clear();
      List<String> line;
      while (null != (line = reader.nextLine())) {
        lines.add(line);
      }
    } finally {
      IOUtil.closeQuietly(reader);
    }
  }
}
