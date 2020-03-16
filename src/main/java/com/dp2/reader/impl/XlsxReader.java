package com.dp2.reader.impl;

import java.io.File;
import java.util.List;

import com.dp2.util.Types;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import com.dp2.reader.AbstractReader;
import com.dp2.util.Excel2007Reader;

/**
 * xls文件读取
 *
 * @author 6tail
 *
 */
public class XlsxReader extends AbstractReader{
  private Excel2007Reader realReader;

  public XlsxReader(File file){
    super(file);
  }

  public void load(){
    realReader = new Excel2007Reader(file);
    realReader.load();
    stop = false;
  }

  public List<String> nextLine(){
    if(stop){
      return null;
    }
    return realReader.nextLine();
  }

  @Override
  public boolean support(){
    try{
      OPCPackage pkg = OPCPackage.open(file);
      XSSFReader r = new XSSFReader(pkg);
      r.getSheetsData();
    }catch(Exception e){
      return false;
    }
    return true;
  }

  public String type(){
    return Types.XLSX;
  }

  @Override
  public void stop(){
    super.stop();
    realReader.stop();
  }
}
