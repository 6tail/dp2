package com.dp2.reader;

import java.io.IOException;
import java.util.List;

/**
 * 读取接口
 *
 * @author 6tail
 *
 */
public interface IReader{

  /**
   * 是否支持
   *
   * @return true/false 支持/不支持
   */
  boolean support();

  /**
   * 重新加载
   *
   * @throws IOException IOException
   */
  void load() throws IOException;

  /**
   * 读取下一行数据，如果没有了或者停止读取，返回null
   *
   * @return 行数据
   */
  List<String> nextLine();

  /**
   * 停止读取
   */
  void stop();

  /**
   * 文件类型
   *
   * @return 文件类型
   */
  String type();

}
