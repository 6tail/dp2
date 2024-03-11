package com.dp2.reader.impl;

import com.dp2.reader.AbstractOfficeReader;
import com.dp2.util.Types;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * doc文件读取
 * <p>由于poi原生解析不支持单元格colspan，所以使用变通办法：将doc转为html后解析html表格。</p>
 *
 * @author 6tail
 */
public class DocReader extends AbstractOfficeReader {
  /**
   * doc文档
   */
  protected HWPFDocument doc;

  protected Elements trs;

  /**
   * 总行数
   */
  protected int rowCount;

  /**
   * 列数
   */
  protected int colCount;

  /**
   * 已读取的行数
   */
  protected int rowRead;

  protected Map<Integer, Integer> rowSpans = new HashMap<Integer, Integer>();

  public DocReader(File file) {
    super(file);
  }

  public void load() throws IOException {
    doc = new HWPFDocument(new FileInputStream(file));
    stop = false;
    rowCount = 0;
    colCount = 0;
    rowRead = 0;
    rowSpans.clear();
    try {
      WordToHtmlConverter converter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
      converter.processDocument(doc);
      Document htmlDoc = converter.getDocument();
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      DOMSource domSource = new DOMSource(htmlDoc);
      StreamResult streamResult = new StreamResult(outStream);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer serializer = tf.newTransformer();
      serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      serializer.setOutputProperty(OutputKeys.INDENT, "no");
      serializer.setOutputProperty(OutputKeys.METHOD, "html");
      serializer.transform(domSource, streamResult);
      outStream.close();
      String content = outStream.toString("utf-8");
      org.jsoup.nodes.Document document = Jsoup.parse(content);
      Elements tables = document.getElementsByTag("table");
      if (!tables.isEmpty()) {
        trs = tables.get(0).getElementsByTag("tr");
        rowCount = trs.size();
      }
    } catch (IOException e) {
      throw e;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> nextLine() {
    if (stop) {
      return null;
    }
    if (rowRead >= rowCount) {
      return null;
    }
    Element tr = trs.get(rowRead);
    rowRead++;
    List<String> line = new ArrayList<String>();
    Elements tds = tr.children();
    if (rowRead == 1) {
      colCount = tds.size();
    }
    int colIndex = 0;
    for (int i = 0; i < colCount; i++) {
      Integer restRow = rowSpans.get(i);
      if (null == restRow || restRow < 1) {
        Element n = tds.get(colIndex++);
        line.add(n.text().trim());
        int rowSpan = 0;
        int colSpan;
        String attrRowSpan = n.attr("rowspan");
        if (attrRowSpan.length() > 0) {
          rowSpan = Integer.parseInt(attrRowSpan) - 1;
          rowSpans.put(i, rowSpan);
        }
        String attrColSpan = n.attr("colspan");
        if (attrColSpan.length() > 0) {
          colSpan = Integer.parseInt(attrColSpan) - 1;
          if (rowRead == 1) {
            colCount += colSpan;
          }
          for (int x = 0; x < colSpan; x++) {
            rowSpans.put(i + x + 1, rowSpan + 1);
          }
        }
      } else {
        restRow--;
        rowSpans.put(i, restRow);
        line.add("");
      }
    }
    return line;
  }

  public String type() {
    return Types.DOC;
  }
}
