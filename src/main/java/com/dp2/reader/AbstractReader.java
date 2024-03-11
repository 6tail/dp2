package com.dp2.reader;

import java.io.File;

/**
 * 抽象读取
 *
 * @author 6tail
 */
public abstract class AbstractReader implements IReader {
  /**
   * 文件
   */
  protected File file;

  /**
   * 是否停止读取
   */
  protected boolean stop;

  protected AbstractReader(File file) {
    this.file = file;
  }

  public boolean support() {
    return false;
  }

  public void stop() {
    stop = true;
  }
}
