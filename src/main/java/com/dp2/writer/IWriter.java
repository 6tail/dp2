package com.dp2.writer;

import com.dp2.node.INode;

import java.io.File;
import java.io.IOException;

/**
 * 写入接口
 *
 * @author 6tail
 */
public interface IWriter {
  /**
   * 重新加载
   *
   * @throws IOException IOException
   */
  void load() throws IOException;

  /**
   * 写入数据
   *
   * @param row  行
   * @param col  列
   * @param node 节点
   */
  void write(int row, int col, INode node);

  /**
   * 保存到文件
   *
   * @param file 文件
   * @throws IOException IO异常
   */
  void save(File file) throws IOException;

}
