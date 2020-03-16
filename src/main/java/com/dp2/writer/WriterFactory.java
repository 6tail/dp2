package com.dp2.writer;

import java.io.File;
import com.dp2.util.Types;
import com.dp2.writer.impl.CsvWriter;
import com.dp2.writer.impl.DocxWriter;
import com.dp2.writer.impl.XlsWriter;
import com.dp2.writer.impl.XlsxWriter;

/**
 * 写入工厂
 *
 * @author 6tail
 *
 */
public class WriterFactory{
  public static IWriter getWriter(String type,File file){
    IWriter writer = null;
    if(Types.XLS.equals(type)){
      writer = new XlsWriter(file);
    }else if(Types.XLSX.equals(type)){
      writer = new XlsxWriter(file);
    }else if(Types.DOCX.equals(type)){
      writer = new DocxWriter(file);
    }else if(Types.CSV.equals(type)){
      writer = new CsvWriter(file);
    }
    return writer;
  }
}
