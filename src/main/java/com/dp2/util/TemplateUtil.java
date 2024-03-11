package com.dp2.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 模板文件辅助类
 *
 * @author 6tail
 */
public class TemplateUtil {
  public static File initTxt() {
    try {
      File file = File.createTempFile("dp2_", ".txt");
      file.deleteOnExit();
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static File initXls() {
    FileOutputStream os = null;
    try {
      File file = File.createTempFile("dp2_", ".xls");
      file.deleteOnExit();
      HSSFWorkbook workbook = new HSSFWorkbook();
      workbook.createSheet();
      os = new FileOutputStream(file);
      workbook.write(os);
      os.flush();
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeQuietly(os);
    }
  }

  public static File initXlsx() {
    FileOutputStream os = null;
    try {
      File file = File.createTempFile("dp2_", ".xlsx");
      file.deleteOnExit();
      XSSFWorkbook workbook = new XSSFWorkbook();
      workbook.createSheet();
      os = new FileOutputStream(file);
      workbook.write(os);
      os.flush();
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.closeQuietly(os);
    }
  }
}
