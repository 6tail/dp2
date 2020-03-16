package com.dp2.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.dp2.marker.Marker;
import com.dp2.marker.MarkerComparator;
import com.dp2.reader.IReader;
import com.dp2.writer.IWriter;

/**
 * 抽象解析器
 *
 * @author 6tail
 *
 */
public abstract class AbstractParser implements IParser{
  protected List<Marker> markers = new ArrayList<Marker>();
  protected IReader reader;
  protected IWriter writer;

  public AbstractParser(IReader reader,IWriter writer){
    this.reader = reader;
    this.writer = writer;
  }

  protected void sort(List<Marker> l){
    Collections.sort(l,new MarkerComparator());
    for(Marker m:l){
      if(null!=m.getChildren()){
        sort(m.getChildren());
      }
    }
  }

  protected List<Marker> clean(List<Marker> l){
    Set<String> exist = new HashSet<String>();
    List<Marker> all = new ArrayList<Marker>();
    for(Marker m:l){
      String pos = m.getRow()+","+m.getCol();
      //忽略坐标重复的标记
      if(exist.contains(pos)){
        continue;
      }
      exist.add(pos);
      if(null!=m.getChildren()){
        m.setChildren(clean(m.getChildren()));
      }
      all.add(m);
    }
    return all;
  }

  public INodeReader read(List<Marker> l){
    markers = clean(l);
    sort(markers);
    return new AbstractNodeReader(reader,markers){};
  }

  public INodeWriter write(List<Marker> l){
    markers = clean(l);
    sort(markers);
    return new AbstractNodeWriter(writer,markers){};
  }
}
