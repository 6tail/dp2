package com.dp2.util;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文件读取工具
 *
 * @author 6tail
 */
public class ReaderUtil {
  public static final String CHARSET_UTF8 = "utf-8";
  public static final String CHARSET_GBK = "gbk";
  /**
   * BOM头
   */
  public static Map<String, byte[]> BOM = new HashMap<String, byte[]>();

  static {
    BOM.put(CHARSET_UTF8, new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
  }

  /**
   * 读取魔数
   *
   * @param file   文件
   * @param length 字节数
   * @return 字节数组，如果读取失败，返回空的数组
   */
  public static byte[] getMagic(File file, int length) {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      byte[] buffer = new byte[length];
      int l;
      try {
        l = is.read(buffer);
      } catch (IOException e) {
        return new byte[]{};
      }
      if (-1 == l) {
        return new byte[]{};
      } else {
        byte[] data = new byte[l];
        System.arraycopy(buffer, 0, data, 0, l);
        return data;
      }
    } catch (Exception e) {
      return new byte[]{};
    } finally {
      IOUtil.closeQuietly(is);
    }
  }

  /**
   * 获取文本文件的编码
   *
   * @param file 文件
   * @return 编码
   * @throws IOException IOException
   */
  public static String getCharset(File file) throws IOException {
    Map<String, Integer> length = new HashMap<String, Integer>(16);
    for (Entry<String, byte[]> entry : BOM.entrySet()) {
      String charset = entry.getKey();
      byte[] magic = entry.getValue();
      if (Arrays.equals(magic, getMagic(file, magic.length))) {
        return charset;
      }
      length.put(charset, readAsText(file, charset).length());
    }
    if (!length.containsKey(CHARSET_UTF8)) {
      length.put(CHARSET_UTF8, readAsText(file, CHARSET_UTF8).length());
    }
    if (!length.containsKey(CHARSET_GBK)) {
      length.put(CHARSET_GBK, readAsText(file, CHARSET_GBK).length());
    }
    return length.get(CHARSET_UTF8) < length.get(CHARSET_GBK) ? CHARSET_UTF8 : CHARSET_GBK;
  }

  protected static String readAsText(File file, String charset) throws IOException {
    StringBuilder s = new StringBuilder();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
      String line;
      while (null != (line = br.readLine())) {
        s.append(line);
        s.append("\r\n");
      }
    } finally {
      IOUtil.closeQuietly(br);
    }
    return s.toString();
  }
}
