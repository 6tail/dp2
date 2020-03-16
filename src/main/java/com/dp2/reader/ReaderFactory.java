package com.dp2.reader;

import java.io.File;
import com.dp2.exception.ParserNotSupportException;
import com.dp2.reader.impl.*;

/**
 * 读取器工厂
 *
 * @author 6tail
 *
 */
public class ReaderFactory{
  public static IReader getReader(File file) throws ParserNotSupportException{
    IReader reader = new XlsReaderV2(file);
    if(reader.support()){
      return reader;
    }
    reader = new XlsxReader(file);
    if(reader.support()){
      return reader;
    }
    reader = new DocReader(file);
    if(reader.support()){
      return reader;
    }
    reader = new DocxReader(file);
    if(reader.support()){
      return reader;
    }
    reader = new CsvReader(file);
    if(reader.support()){
      return reader;
    }
    throw new ParserNotSupportException();
  }
}
