package com.dp2.writer.impl;

import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;

import javax.imageio.ImageIO;
import java.io.*;

/**
 * xls文件写入
 *
 * @author 6tail
 */
public class XlsWriter extends AbstractWriter {
  /**
   * xls工作簿
   */
  private HSSFWorkbook workbook = null;
  /**
   * xls Sheet
   */
  private HSSFSheet sheet = null;

  public XlsWriter(File file) {
    super(file);
  }

  public void load() throws IOException {
    workbook = new HSSFWorkbook(new FileInputStream(file));
    sheet = workbook.getSheetAt(0);
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

  public void write(int row, int col, INode node) {
    String value = node.getValue();
    if (null == value) {
      return;
    }
    HSSFRow line = sheet.getRow(row);
    if (null == line) {
      line = sheet.createRow(row);
    }
    Integer height = node.getHeight();
    if (null != height) {
      line.setHeight((short) (height * 20));
    }
    HSSFCell cell = line.getCell(col);
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
          HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
          HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) col, row, (short) (col + 1), row + 1);
          patriarch.createPicture(anchor, workbook.addPicture(data, HSSFWorkbook.PICTURE_TYPE_PNG));
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
}
