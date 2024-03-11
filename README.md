# dp2  [![License](https://img.shields.io/badge/license-MIT-4EB1BA.svg?style=flat-square)](https://github.com/6tail/dp2/blob/master/LICENSE)

一个通用的表格数据读写工具，支持xls、xlsx、doc、docx、csv格式。

> 支持在较低内存占用下完成超大xls和xlsx文件的解析。

> 支持直接生成新文件，也支持在模板文件中修改单元格数据并生成新的文件。

> 支持java1.5及以上版本。java1.5需额外引入javax包，否则解析xlsx会报错。

> xls和xlsx文件仅解析第一个Sheet。

> doc和docx文件仅解析第一个表格。

## 使用

通过maven引入或者[点此下载](https://github.com/6tail/dp2/releases)相应的jar。


```xml
<dependency>
  <groupId>cn.6tail</groupId>
  <artifactId>dp2</artifactId>
  <version>1.0.3</version>
</dependency>
```

## 示例

### 待解析excel内容

<table>
  <tr>
  <th></th>
  <th>A</th>
  <th>B</th>
  <th>C</th>
  <th>D</th>
  <th>E</th>
  </tr>
  <tr>
  <td>1</td>
  <td>序号</td>
  <td>姓名</td>
  <td>性别</td>
  <td>年龄</td>
  <td>民族</td>
  </tr>
  <tr>
  <td>2</td>
  <td>1</td>
  <td>张三</td>
  <td>男</td>
  <td>20</td>
  <td>汉族</td>
  </tr>
  <tr>
  <td>3</td>
  <td>2</td>
  <td>李四</td>
  <td>女</td>
  <td>18</td>
  <td>汉族</td>
  </tr>
  <tr>
  <td>4</td>
  <td>3</td>
  <td>王二</td>
  <td>男</td>
  <td>30</td>
  <td>满族</td>
  </tr>
</table>

### 自动读取示例

    // 待解析文件
    File file = new File("template.xls");
     
    // 自动获取标记
    List<Marker> markers = SingleLineRepeatMarkerDetector.detect(file);
     
    // 通过工厂获取解析器接口
    IParser parser = ParserFactory.getParser(file);
     
    // 获取节点读取器
    INodeReader reader = parser.read(markers);
     
    // 遍历所有找到的节点
    while (reader.hasNext()) {
      INode node = reader.next();
       
      // 自动识别的标题以head作为标记名称，以body作为数据标记名称
      if ("head".equals(node.getMarker().getName())) {
        // 表头不处理
      } else if ("body".equals(node.getMarker().getName())) {
        for (INode child : node.getChildren()) {
          // 自动识别的以列下标（从0开始）作为子项标记名称
          // System.out.print(child.getMarker().getName() + "=");
          System.out.print(child.getValue());
          System.out.print("\t");
        }
        System.out.println("_____________________________________");
      }
    }

### 手动读取示例

    // 待解析文件
    File file = new File("template.xls");
     
    // 定义待解析的标记列表
    List<Marker> markers = new ArrayList<Marker>();
     
    // 标题只有一行，直接使用Marker，位于行0列0（如果不解析标题，则可以不定义）
    Marker markerHead = new Marker("标题", 0, 0);
     
    // 设置宽度为5列（高度默认为一行不用设置了）
    markerHead.setWidth(5);
     
    // 添加需要取的标题标记及位于父区域内的坐标
    markerHead.addChild(new Marker("序号", 0, 0));
    markerHead.addChild(new Marker("姓名", 0, 1));
    markerHead.addChild(new Marker("性别", 0, 2));
    markerHead.addChild(new Marker("年龄", 0, 3));
    markerHead.addChild(new Marker("民族", 0, 4));
     
    // 数据区域为重复性数据，使用RepeatedMarker，位于行1列0
    Marker markerBody = new RepeatedMarker("数据", 1, 0);
     
    // 设置宽度为5列（高度默认为一行不用设置了）
    markerBody.setWidth(5);
     
    // 添加需要取的数据标记及位于父区域内的坐标
    markerBody.addChild(new Marker("序号", 0, 0));
    markerBody.addChild(new Marker("姓名", 0, 1));
    markerBody.addChild(new Marker("性别", 0, 2));
    markerBody.addChild(new Marker("年龄", 0, 3));
    markerBody.addChild(new Marker("民族", 0, 4));
     
    // 添加要解析的标记
    markers.add(markerHead);
    markers.add(markerBody);
     
    // 通过工厂获取解析器接口
    IParser parser = ParserFactory.getParser(file);
     
    // 获取节点读取器
    INodeReader reader = parser.read(markers);
     
    // 遍历所有找到的节点
    while (reader.hasNext()) {
      INode node = reader.next();
       
      if ("标题".equals(node.getMarker().getName())) {
        // 表头不处理
      } else if ("数据".equals(node.getMarker().getName())) {
        for (INode child : node.getChildren()) {
          // 子项标记名称为前面定义的标记名称，如序号、姓名等
          // System.out.print(child.getMarker().getName() + "=");
          System.out.print(child.getValue());
          System.out.print("\t");
        }
        System.out.println("_____________________________________");
      }
    }

### 输出结果

    1  张三  男  20  汉族
    _____________________________________
    2  李四  女  18  汉族
    _____________________________________
    3  王二  男  30  满族
    _____________________________________


### 生成文件示例

    // 定义标记列表
    List<Marker> markers = new ArrayList<Marker>();
     
    // 标题只有一行，使用Marker，位于第0行第0列
    Marker markerHead = new Marker("标题", 0, 0);
     
    // 设置宽度为2列
    markerHead.setWidth(2);
     
    // 添加需要取的标题标记及位于父区域内的坐标
    markerHead.addChild(new Marker("sn", 0, 0));
    markerHead.addChild(new Marker("name", 0, 1));
     
    // 数据区域为重复性数据，使用RepeatedMarker，位于第1行第0列
    Marker markerBody = new RepeatedMarker("数据", 1, 0);
     
    // 设置宽度为2列
    markerBody.setWidth(2);
     
    // 添加需要取的数据标记及位于父区域内的坐标
    markerBody.addChild(new Marker("序号", 0, 0));
    markerBody.addChild(new Marker("姓名", 0, 1));
     
    // 添加标记
    markers.add(markerHead);
    markers.add(markerBody);
     
    // 通过工厂获取xls解析接口
    IParser parser = ParserFactory.getParser("xls");
     
    // 获取节点写入接口
    INodeWriter writer = parser.write(markers);
     
    // 写标题
    Node head = new Node("标题");
    head.addChild(new Node("sn", "序号"));
    head.addChild(new Node("name", "姓名"));
    writer.add(head);
     
    // 写数据
    Node body = new Node("数据");
    body.addChild(new Node("序号", "1"));
    body.addChild(new Node("姓名", "张三"));
    writer.add(body);
     
    // 再写一行数据
    body = new Node("数据");
    body.addChild(new Node("序号", "2"));
    body.addChild(new Node("姓名", "李四"));
    writer.add(body);
     
    // 待生成文件
    File file = new File("template.xls");
     
    // 输出
    writer.save(file);

## 解析机制

在一个表格中，指定横坐标、纵坐标、宽度（多少列），高度（多少行），你就能圈出一个区域。给这个区域贴上标签，你就能很容易的从表格中找到你感兴趣的内容，这个标签，也就是标记（Marker）。

标记可大可小，大到整个Sheet（当然这样没有实际意义），小到一个单元格。

标记支持层级，一个大的标记下，可以包含多个小的子标记（子标记的坐标以父标记为基准），这样就可以从大到小逐步锁定目标。

遍历读取的时候，仅遍历顶级的标记，子标记内容通过getChildren方法获取。

### 单一标记

单一标记(Marker)为不重复出现的标记。

以下图为例，通过"姓名"标记可直接读取到"六特尔"，通过"email"标记可直接读取到"6tail@6tail.cn"，也可修改对应的值。

![单一标记示例](https://github.com/6tail/dp2/raw/master/samples/dp2-0.png)

### 重复标记

重复标记(RepeatedMarker)为重复出现的标记。

以下图为例，每一行数据为一个大的标记（取名为"名单"，按行重复出现，因不确定有多少行数据，因此重复标记的坐标以第一次出现的坐标为准），大标记中包含两个子标记（"姓名"和"性别"）。

![重复标记示例](https://github.com/6tail/dp2/raw/master/samples/dp2-1.png)

### 组合标记

无论多复杂的表格，都可以拆分为单一标记与重复标记的组合，标记表格的方式也可以五花八门，你选择的标记方式将决定解析的效率。

![复杂标记示例](https://github.com/6tail/dp2/raw/master/samples/dp2-2.png)

以上图这个表格为例，如果我们需要读取主题和名单，可定义两个顶级标记，"主题"标记位于行1列1，"名单"标记位于行4列1，宽2高3。

其中"名单"中包含多个重复标记"人员"，位于行0列0，宽2高1。

"人员"标记又包含"姓名"标记（位于行0列0，宽1高1）和"性别"标记（位于行0列1，宽1高1）。

## 注意

坐标均以父标记为基准，从0开始计。
