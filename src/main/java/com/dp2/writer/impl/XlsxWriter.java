package com.dp2.writer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dp2.ParserFactory;
import com.dp2.util.Types;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.dp2.node.INode;
import com.dp2.util.IOUtil;
import com.dp2.writer.AbstractWriter;

/**
 * xlsx文件写入
 *
 * @author 6tail
 *
 */
public class XlsxWriter extends AbstractWriter{
  /** xlsx工作簿 */
  private SXSSFWorkbook workbook;
  /** 轻量的xlsx工作簿 */
  private XSSFWorkbook lightWorkbook;
  /** xlsx Sheet */
  private Sheet sheet;
  /** 是否轻量 */
  private boolean light;

  public XlsxWriter(File file){
    super(file);
  }

  public void load() throws IOException{
    lightWorkbook = new XSSFWorkbook(new FileInputStream(file));
    light = file != ParserFactory.EMPTY_TEMPLATE_FILES.get(Types.XLSX);
    if(light){
      sheet = lightWorkbook.getSheetAt(0);
    }else{
      workbook = new SXSSFWorkbook(lightWorkbook,500);
      sheet = workbook.getSheetAt(0);
    }
  }

  public void write(int row,int col,INode node){
    String value = node.getValue();
    if(null==value){
      return;
    }
    Row line = sheet.getRow(row);
    if(null==line){
      line = sheet.createRow(row);
    }
    Cell cell = line.getCell(col);
    if(null==cell){
      cell = line.createCell(col);
    }
    switch(node.getType()){
      case number:
        cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
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

  public void save(File file) throws IOException{
    FileOutputStream os = null;
    try{
      os = new FileOutputStream(file);
      if(light){
        lightWorkbook.write(os);
      }else {
        workbook.write(os);
      }
      os.flush();
    }finally{
      IOUtil.closeQuietly(os);
    }
  }
}
