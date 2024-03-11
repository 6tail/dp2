package com.dp2.parser;

import com.dp2.marker.Marker;
import com.dp2.reader.IReader;
import com.dp2.writer.IWriter;

import java.util.List;

/**
 * 解析器接口
 *
 * @author 6tail
 */
public interface IParser {

  /**
   * 获取读取接口
   *
   * @return 读取接口
   */
  IReader getReader();

  /**
   * 获取写入接口
   *
   * @return 写入接口
   */
  IWriter getWriter();

  /**
   * 获取节点读取器接口
   *
   * @param markers 标记们
   * @return 节点读取器接口
   */
  INodeReader read(List<Marker> markers);

  /**
   * 获取节点写入器接口
   *
   * @param markers 标记们
   * @return 节点写入器接口
   */
  INodeWriter write(List<Marker> markers);
}
