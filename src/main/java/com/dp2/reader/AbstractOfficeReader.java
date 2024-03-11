package com.dp2.reader;

import com.dp2.util.Magics;
import com.dp2.util.ReaderUtil;

import java.io.File;
import java.util.Arrays;

/**
 * OFFICE97-2003抽象读取
 *
 * @author 6tail
 */
public abstract class AbstractOfficeReader extends AbstractReader {

  protected AbstractOfficeReader(File file) {
    super(file);
  }

  @Override
  public boolean support() {
    int magicLength = Magics.OFFICE_97_2003.length;
    byte[] magic = ReaderUtil.getMagic(file, magicLength);
    if (!Arrays.equals(Magics.OFFICE_97_2003, magic)) {
      return false;
    }
    try {
      load();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

}
