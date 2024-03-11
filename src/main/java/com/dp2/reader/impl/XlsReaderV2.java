package com.dp2.reader.impl;

import com.dp2.reader.AbstractOfficeReader;
import com.dp2.util.Excel2003Reader;
import com.dp2.util.Types;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * xls文件读取
 *
 * @author 6tail
 */
public class XlsReaderV2 extends AbstractOfficeReader {
  private Excel2003Reader realReader;

  public XlsReaderV2(File file) {
    super(file);
  }

  public void load() throws IOException {
    realReader = new Excel2003Reader(file);
    realReader.load();
    stop = false;
  }

  public List<String> nextLine() {
    if (stop) {
      return null;
    }
    return realReader.nextLine();
  }

  public String type() {
    return Types.XLS;
  }

  @Override
  public void stop() {
    super.stop();
    realReader.stop();
  }
}
