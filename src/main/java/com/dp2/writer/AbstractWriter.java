package com.dp2.writer;

import java.io.File;

/**
 * 抽象写入
 *
 * @author 6tail
 *
 */
public abstract class AbstractWriter implements IWriter{
  /** 文件 */
  protected File file;

  protected AbstractWriter(File file){
    this.file = file;
  }
}
