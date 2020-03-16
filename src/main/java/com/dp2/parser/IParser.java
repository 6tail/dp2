package com.dp2.parser;

import java.util.List;
import com.dp2.marker.Marker;

/**
 * 解析器接口
 *
 * @author 6tail
 *
 */
public interface IParser{
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
