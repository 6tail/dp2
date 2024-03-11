package com.dp2;

import com.dp2.exception.ParserNotSupportException;
import com.dp2.parser.AbstractParser;
import com.dp2.parser.IParser;
import com.dp2.reader.IReader;
import com.dp2.reader.ReaderFactory;
import com.dp2.util.TemplateUtil;
import com.dp2.util.Types;
import com.dp2.writer.IWriter;
import com.dp2.writer.WriterFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析工厂
 *
 * @author 6tail
 */
public class ParserFactory {
  /**
   * 空白的模板文件映射
   */
  public static final Map<String, File> EMPTY_TEMPLATE_FILES = new HashMap<String, File>(3);

  /**
   * 初始化
   */
  protected static void init() {
    EMPTY_TEMPLATE_FILES.put(Types.XLS, TemplateUtil.initXls());
    EMPTY_TEMPLATE_FILES.put(Types.XLSX, TemplateUtil.initXlsx());
    EMPTY_TEMPLATE_FILES.put(Types.CSV, TemplateUtil.initTxt());
  }

  static {
    init();
  }

  /**
   * 通过文件类型获取解析器，这种方式将解析一个空白的模板文件，主要用于指定类型的数据文件输出而非读取(因为是空白文件什么也读不到)
   *
   * @param type 文件类型，如xls、xlsx、csv等
   * @return 解析器接口
   * @throws ParserNotSupportException ParserNotSupportException
   */
  public static IParser getParser(String type) throws ParserNotSupportException {
    File file = EMPTY_TEMPLATE_FILES.get(type.toLowerCase());
    if (null == file) {
      throw new ParserNotSupportException();
    }
    return getParser(file);
  }

  /**
   * 获取解析器，这种方式既可用于读取文件，也可将该文件作为模板，修改指定内容后输出新文件
   *
   * @param file 待解析的文件
   * @return 解析器接口
   * @throws ParserNotSupportException ParserNotSupportException
   */
  public static IParser getParser(File file) throws ParserNotSupportException {
    IReader reader = ReaderFactory.getReader(file);
    IWriter writer = WriterFactory.getWriter(reader.type(), file);
    return new AbstractParser(reader, writer) {
    };
  }
}
