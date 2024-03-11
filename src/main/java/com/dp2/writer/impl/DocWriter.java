package com.dp2.writer.impl;

import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * doc文件写入，poi操作doc问题太多，功能还未实现，不要使用！不要使用！不要使用！
 *
 * @author 6tail
 */
public class DocWriter extends AbstractWriter {
  /**
   * doc文档
   */
  private HWPFDocument document;
  protected List<List<String>> lines = new ArrayList<List<String>>();

  public DocWriter(File file) {
    super(file);
  }

  public void load() throws IOException {
    document = new HWPFDocument(new FileInputStream(file));
  }

  public void save(File file) throws IOException {
    int rows = lines.size();
    int cols = 0;
    for (List<String> line : lines) {
      int size = line.size();
      if (size > cols) {
        cols = size;
      }
    }
    Range range = document.getRange();
    TableIterator it = new TableIterator(range);
    Table table;
    if (it.hasNext()) {
      table = it.next();
    } else {
      table = range.insertTableBefore((short) cols, rows);
    }
    for (int row = 0; row < rows && row < table.numRows(); row++) {
      List<String> line = lines.get(row);
      for (int col = 0; col < cols && col < line.size(); col++) {
        String o = line.get(col);
        o = null == o ? "" : o;
        TableCell cell = table.getRow(row).getCell(col);
        cell.insertBefore(o);
      }
    }
    FileOutputStream os = null;
    try {
      os = new FileOutputStream(file);
      document.write(os);
      os.flush();
    } finally {
      IOUtil.closeQuietly(os);
    }
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
}
