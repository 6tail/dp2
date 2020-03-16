package com.dp2.parser;

import java.io.File;
import java.io.IOException;
import com.dp2.node.INode;

/**
 * 节点写入器接口
 *
 * @author 6tail
 *
 */
public interface INodeWriter{
  /**
   * 保存到新文件
   * @param file 文件
   * @throws IOException IOException
   */
  void save(File file) throws IOException;

  /**
   * 添加节点数据
   * @param node 节点
   */
  void add(INode node);
}
