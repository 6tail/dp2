package com.dp2.writer.impl;

import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * docx文件写入
 *
 * @author 6tail
 */
public class DocxWriter extends AbstractWriter {
  /**
   * docx文档
   */
  private XWPFDocument document;
  private XWPFTable table;

  public DocxWriter(File file) {
    super(file);
  }

  public void load() throws IOException {
    document = new XWPFDocument(new FileInputStream(file));
    List<XWPFTable> tables = document.getTables();
    table = tables.isEmpty() ? document.createTable() : tables.get(0);
  }

  public void save(File file) throws IOException {
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
    String value = node.getValue();
    if (null == value) {
      return;
    }
    int rows = table.getRows().size();
    int rowDiff = row - rows + 1;
    XWPFTableRow r = null;
    if (rowDiff > 0) {
      for (int i = 0; i < rowDiff; i++) {
        r = table.createRow();
      }
    } else {
      r = table.getRow(row);
    }
    int cols = r.getTableCells().size();
    int colDiff = col - cols + 1;
    XWPFTableCell cell = null;
    if (colDiff > 0) {
      for (int i = 0; i < colDiff; i++) {
        cell = r.createCell();
      }
    } else {
      cell = r.getCell(col);
    }
    cell.removeParagraph(0);
    cell.setText(value);
  }
}
