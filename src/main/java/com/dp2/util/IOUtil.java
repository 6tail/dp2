package com.dp2.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO处理工具
 *
 * @author 6tail
 *
 */
public class IOUtil {
  public static void closeQuietly(Closeable closeable){
    if(null==closeable){
      return;
    }
    try{
      closeable.close();
    }catch(IOException ignore){}
  }
}
