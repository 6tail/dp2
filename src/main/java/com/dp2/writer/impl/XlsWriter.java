package com.dp2.writer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;

/**
 * xls文件写入
 *
 * @author 6tail
 *
 */
public class XlsWriter extends AbstractWriter{
  /** xls工作簿 */
  private HSSFWorkbook workbook = null;
  /** xls Sheet */
  private HSSFSheet sheet = null;

  public XlsWriter(File file){
    super(file);
  }

  public void load() throws IOException{
    workbook = new HSSFWorkbook(new FileInputStream(file));
    sheet = workbook.getSheetAt(0);
  }

  public void save(File file) throws IOException{
    FileOutputStream os = null;
    try{
      os = new FileOutputStream(file);
      workbook.write(os);
      os.flush();
    }finally{
      IOUtil.closeQuietly(os);
    }
  }

  public void write(int row,int col,INode node){
    String value = node.getValue();
    if(null==value){
      return;
    }
    HSSFRow line = sheet.getRow(row);
    if(null==line){
      line = sheet.createRow(row);
    }
    HSSFCell cell = line.getCell(col);
    if(null==cell){
      cell = line.createCell(col);
    }
    switch(node.getType()){
      case number:
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        try{
          cell.setCellValue(Double.parseDouble(value));
        }catch(Exception e){
          cell.setCellValue(value);
        }
        break;
      default:
        cell.setCellValue(value);
        break;
    }
  }
}
