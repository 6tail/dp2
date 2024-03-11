package com.dp2.writer.impl;

import com.dp2.ParserFactory;
import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.util.Types;
import com.dp2.writer.AbstractWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.io.*;

/**
 * xlsx文件写入
 *
 * @author 6tail
 */
public class XlsxWriter extends AbstractWriter {
  /**
   * xlsx工作簿
   */
  private Workbook workbook;
  /**
   * xlsx Sheet
   */
  private Sheet sheet;

  public XlsxWriter(File file) {
    super(file);
  }

  public void load() throws IOException {
    workbook = new XSSFWorkbook(new FileInputStream(file));
    if (file == ParserFactory.EMPTY_TEMPLATE_FILES.get(Types.XLSX)) {
      workbook = new SXSSFWorkbook((XSSFWorkbook) workbook, 500);
    }
    sheet = workbook.getSheetAt(0);
  }

  public void write(int row, int col, INode node) {
    String value = node.getValue();
    if (null == value) {
      return;
    }
    Row line = sheet.getRow(row);
    if (null == line) {
      line = sheet.createRow(row);
    }
    Integer height = node.getHeight();
    if (null != height) {
      line.setHeight((short) (height * 20));
    }
    Cell cell = line.getCell(col);
    if (null == cell) {
      cell = line.createCell(col);
    }
    switch (node.getType()) {
      case number:
        cell.setCellType(CellType.NUMERIC);
        try {
          cell.setCellValue(Double.parseDouble(value));
        } catch (Exception e) {
          cell.setCellValue(value);
        }
        break;
      case local_image:
        ByteArrayOutputStream out = null;
        try {
          out = new ByteArrayOutputStream();
          ImageIO.write(ImageIO.read(new File(value)), "png", out);
          byte[] data = out.toByteArray();
          Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
          XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) col, row, (short) (col + 1), row + 1);
          drawingPatriarch.createPicture(anchor, workbook.addPicture(data, XSSFWorkbook.PICTURE_TYPE_PNG));
        } catch (Exception e) {
          cell.setCellValue(value);
        } finally {
          IOUtil.closeQuietly(out);
        }
        break;
      default:
        cell.setCellValue(value);
        break;
    }
  }

  public void save(File file) throws IOException {
    FileOutputStream os = null;
    try {
      os = new FileOutputStream(file);
      workbook.write(os);
      os.flush();
    } finally {
      IOUtil.closeQuietly(os);
    }
  }
}
