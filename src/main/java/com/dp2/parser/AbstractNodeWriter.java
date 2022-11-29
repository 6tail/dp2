package com.dp2.parser;

import com.dp2.marker.Marker;
import com.dp2.marker.RepeatedMarker;
import com.dp2.node.INode;
import com.dp2.writer.IWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 抽象节点写入器
 *
 * @author 6tail
 *
 */
public class AbstractNodeWriter implements INodeWriter{
  protected IWriter writer;
  protected Map<String,Marker> markers = new LinkedHashMap<String, Marker>();
  protected Map<String,List<INode>> nodes = new LinkedHashMap<String,List<INode>>();
  public AbstractNodeWriter(IWriter writer,List<Marker> markers){
    this.writer = writer;
    if(null!=markers){
      for(Marker m:markers){
        this.markers.put(m.getName(),m);
      }
    }
    try{
      writer.load();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  protected void writeNode(int offsetRow,int offsetCol,Marker marker,List<INode> l){
    if(null==marker){
      return;
    }
    int size = l.size();
    if(size<1){
      return;
    }
    if(marker instanceof RepeatedMarker){
      int row = marker.getRow();
      int col = marker.getCol();
      int height = marker.getHeight();
      for(int i=0;i<size;i++){
        INode node = l.get(i);
        int newRow = offsetRow+row+i*height;
        int newCol = offsetCol+col;
        List<INode> children = node.getChildren();
        if(null==children||children.size()<1){
          writer.write(newRow,newCol,node);
        }else{
          if(null==marker.getChildren()||marker.getChildren().size()<1){
            continue;
          }
          Map<String,Marker> childMarkers = new LinkedHashMap<String,Marker>(16);
          for(Marker m:marker.getChildren()){
            childMarkers.put(m.getName(),m);
          }
          Map<String,List<INode>> childNodes = new LinkedHashMap<String,List<INode>>(16);
          for(INode child:children){
            String markerName = child.getMarker().getName();
            List<INode> cl = childNodes.get(markerName);
            if(null==cl){
              cl = new ArrayList<INode>();
              childNodes.put(markerName,cl);
            }
            cl.add(child);
          }
          for(Entry<String,List<INode>> entry:childNodes.entrySet()){
            writeNode(newRow,newCol,childMarkers.get(entry.getKey()),entry.getValue());
          }
        }
      }
    }else{
      INode node = l.get(0);
      int row = offsetRow+marker.getRow();
      int col = offsetCol+marker.getCol();
      List<INode> children = node.getChildren();
      if(null==children||children.size()<1){
        writer.write(row,col,node);
      }else{
        if(null==marker.getChildren()||marker.getChildren().size()<1){
          return;
        }
        Map<String,Marker> childMarkers = new LinkedHashMap<String,Marker>(16);
        for(Marker m:marker.getChildren()){
          childMarkers.put(m.getName(),m);
        }
        Map<String,List<INode>> childNodes = new LinkedHashMap<String,List<INode>>(16);
        for(INode child:children){
          String markerName = child.getMarker().getName();
          List<INode> cl = childNodes.get(markerName);
          if(null==cl){
            cl = new ArrayList<INode>();
            childNodes.put(markerName,cl);
          }
          cl.add(child);
        }
        for(Entry<String,List<INode>> entry:childNodes.entrySet()){
          writeNode(row,col,childMarkers.get(entry.getKey()),entry.getValue());
        }
      }
    }
  }

  public void save(File file) throws IOException{
    for(Entry<String,List<INode>> entry:nodes.entrySet()){
      String markerName = entry.getKey();
      List<INode> l = entry.getValue();
      writeNode(0,0,markers.get(markerName),l);
    }
    writer.save(file);
  }

  public void add(INode node){
    String markerName = node.getMarker().getName();
    List<INode> l = nodes.get(markerName);
    if(null==l){
      l = new ArrayList<INode>();
      nodes.put(markerName,l);
    }
    l.add(node);
  }

}
