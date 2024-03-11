package com.dp2.node;

import com.dp2.marker.Marker;

import java.util.List;

/**
 * 节点接口
 *
 * @author 6tail
 */
public interface INode {
  /**
   * 获取该节点对应的标记
   *
   * @return 标记
   */
  Marker getMarker();

  /**
   * 获取节点数据内容
   *
   * @return 数据内容
   */
  String getValue();

  /**
   * 获取子节点
   *
   * @return 子节点们
   */
  List<INode> getChildren();

  /**
   * 获取节点类型
   *
   * @return 节点类型
   */
  NodeType getType();

  /**
   * 获取高度
   *
   * @return 高度
   */
  Integer getHeight();
}
