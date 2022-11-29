package test;

import com.dp2.ParserFactory;
import com.dp2.detector.SingleLineRepeatMarkerDetector;
import com.dp2.marker.Marker;
import com.dp2.marker.RepeatedMarker;
import com.dp2.node.INode;
import com.dp2.node.Node;
import com.dp2.parser.INodeReader;
import com.dp2.parser.INodeWriter;
import com.dp2.parser.IParser;
import com.dp2.util.Types;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用示例
 *
 * @author 6tail
 */
public class ParserTest {

    /**
     * 类型
     *
     * @see Types
     */
    private static final String TYPE = Types.XLSX;

    /**
     * 模板文件
     */
    private File templateFile = new File(getClass().getResource("/").getPath(), String.format("template.%s", TYPE));

    /**
     * 输出文件
     */
    private File saveFile = new File(getClass().getResource("/").getPath(), String.format("save.%s", TYPE));

    private void printNodes(List<INode> nodes) {
        for (INode child : nodes) {
            // 自动识别的以列下标作为标题子项标记名称
            System.out.print(child.getMarker().getName());
            System.out.print("=");
            System.out.print(child.getValue());
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * 自动识别读取
     *
     * @throws Exception Exception
     */
    @Test
    public void readAutoDetect() throws Exception {
        // 自动识别单行带标题的标记
        List<Marker> markers = SingleLineRepeatMarkerDetector.detect(templateFile);

        IParser parser = ParserFactory.getParser(templateFile);
        INodeReader reader = parser.read(markers);
        while (reader.hasNext()) {
            INode node = reader.next();
            // 自动识别的标题以head作为标记名称，以body作为数据标记名称
            if ("head".equals(node.getMarker().getName())) {
                List<INode> children = node.getChildren();
                printNodes(children);
            } else if ("body".equals(node.getMarker().getName())) {
                List<INode> children = node.getChildren();
                printNodes(children);
            }
        }
    }

    /**
     * 手动读取
     *
     * @throws Exception Exception
     */
    @Test
    public void readManual() throws Exception {
        List<Marker> markers = new ArrayList<Marker>();

        // 标题只有一行，使用Marker
        Marker markerHead = new Marker("标题", 0, 0);

        // 设置宽度为6列
        markerHead.setWidth(6);

        // 添加需要取的标题标记及位于父区域内的坐标
        markerHead.addChild(new Marker("序号", 0, 0));
        markerHead.addChild(new Marker("姓名", 0, 1));
        markerHead.addChild(new Marker("性别", 0, 2));
        markerHead.addChild(new Marker("年龄", 0, 3));
        markerHead.addChild(new Marker("民族", 0, 4));

        // 数据区域为重复性数据，使用RepeatedMarker
        Marker markerBody = new RepeatedMarker("数据", 1, 0);

        // 设置宽度为6列
        markerBody.setWidth(6);

        // 添加需要取的数据标记及位于父区域内的坐标
        markerBody.addChild(new Marker("序号", 0, 0));
        markerBody.addChild(new Marker("name", 0, 1));
        markerBody.addChild(new Marker("性别", 0, 2));
        markerBody.addChild(new Marker("年龄", 0, 3));
        markerBody.addChild(new Marker("民族", 0, 4));

        markers.add(markerHead);
        markers.add(markerBody);

        IParser parser = ParserFactory.getParser(templateFile);
        INodeReader reader = parser.read(markers);
        while (reader.hasNext()) {
            INode node = reader.next();
            if ("标题".equals(node.getMarker().getName())) {
                List<INode> children = node.getChildren();
                printNodes(children);
            } else if ("数据".equals(node.getMarker().getName())) {
                List<INode> children = node.getChildren();
                printNodes(children);
            }
        }
    }

    /**
     * 手动新建
     *
     * @throws Exception Exception
     */
    @Test
    public void writeManual() throws Exception {
        List<Marker> markers = new ArrayList<Marker>();

        // 标题只有一行，使用Marker
        Marker markerHead = new Marker("标题", 0, 0);

        // 设置宽度为2列
        markerHead.setWidth(2);

        // 添加需要取的标题标记及位于父区域内的坐标
        markerHead.addChild(new Marker("序号", 0, 0));
        markerHead.addChild(new Marker("姓名", 0, 1));

        // 数据区域为重复性数据，使用RepeatedMarker
        Marker markerBody = new RepeatedMarker("数据", 1, 0);

        // 设置宽度为2列
        markerBody.setWidth(2);

        // 添加需要取的数据标记及位于父区域内的坐标
        markerBody.addChild(new Marker("序号", 0, 0));
        markerBody.addChild(new Marker("name", 0, 1));

        markers.add(markerHead);
        markers.add(markerBody);

        // 新建
        IParser parser = ParserFactory.getParser(TYPE);
        INodeWriter writer = parser.write(markers);

        // 写标题
        Node head = new Node("标题");
        head.addChild(new Node("序号", "sn"));
        head.addChild(new Node("姓名", "name"));
        writer.add(head);

        // 写数据
        Node body = new Node("数据");
        body.addChild(new Node("序号", "1"));
        body.addChild(new Node("name", "张三"));
        writer.add(body);

        // 再写一行数据
        body = new Node("数据");
        body.addChild(new Node("序号", "2"));
        body.addChild(new Node("name", "李四"));
        writer.add(body);

        // 输出文件
        writer.save(saveFile);
    }

    /**
     * 在模板基础上修改
     *
     * @throws Exception Exception
     */
    @Test
    public void writeWithTemplate() throws Exception {
        List<Marker> markers = new ArrayList<Marker>();

        // 标题只有一行，使用Marker
        Marker markerHead = new Marker("标题", 0, 0);

        // 设置宽度为2列
        markerHead.setWidth(2);

        // 添加需要取的标题标记及位于父区域内的坐标
        markerHead.addChild(new Marker("序号", 0, 0));
        markerHead.addChild(new Marker("姓名", 0, 1));

        // 数据区域为重复性数据，使用RepeatedMarker
        Marker markerBody = new RepeatedMarker("数据", 1, 0);

        // 设置宽度为2列
        markerBody.setWidth(2);

        // 添加需要取的数据标记及位于父区域内的坐标
        markerBody.addChild(new Marker("序号", 0, 0));
        markerBody.addChild(new Marker("name", 0, 1));

        markers.add(markerHead);
        markers.add(markerBody);

        // 从模板文件读取
        IParser parser = ParserFactory.getParser(templateFile);
        INodeWriter writer = parser.write(markers);

        // 修改标题
        Node head = new Node("标题");
        head.addChild(new Node("序号", "sn"));
        head.addChild(new Node("姓名", "name"));
        writer.add(head);

        // 把第一行数据的姓名改掉
        Node body = new Node("数据");
        body.addChild(new Node("name", "zhangsan"));
        writer.add(body);

        // 改第二行
        body = new Node("数据");
        body.addChild(new Node("name", "lisi"));
        writer.add(body);

        // 第三行保持不变
        body = new Node("数据");
        writer.add(body);

        // 新增一行
        body = new Node("数据");
        body.addChild(new Node("序号", "4"));
        body.addChild(new Node("name", "麻子"));
        writer.add(body);

        // 输出文件
        writer.save(saveFile);
    }

    /**
     * 在模板基础上指定坐标修改
     *
     * @throws Exception Exception
     */
    @Test
    public void writeCellWithTemplate() throws Exception {
        List<Marker> markers = new ArrayList<Marker>();

        // 标记第1行第1列
        Marker marker = new Marker("张三", 1, 1);

        markers.add(marker);

        // 从模板文件读取
        IParser parser = ParserFactory.getParser(templateFile);
        INodeWriter writer = parser.write(markers);

        // 修改
        Node node = new Node("张三", "zhangsan");
        writer.add(node);

        // 输出文件
        writer.save(saveFile);
    }
}
