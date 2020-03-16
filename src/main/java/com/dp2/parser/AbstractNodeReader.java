package com.dp2.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.dp2.marker.Marker;
import com.dp2.marker.RepeatedMarker;
import com.dp2.node.AbstractNode;
import com.dp2.node.INode;
import com.dp2.reader.IReader;

/**
 * 抽象节点读取器
 *
 * @author 6tail
 *
 */
public abstract class AbstractNodeReader implements INodeReader{
  protected List<Marker> markers = new ArrayList<Marker>();
  protected Marker marker;
  protected IReader reader;
  private List<String> line = null;
  private int row = -1;
  private INode node = null;

  public AbstractNodeReader(IReader reader,List<Marker> markers){
    this.reader = reader;
    if(null!=markers){
      this.markers.addAll(markers);
    }
    try{
      reader.load();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public boolean hasNext(){
    if(null==markers){
      reader.stop();
      return false;
    }
    if(null==marker){
      if(markers.size()>0){
        marker = markers.remove(0);
      }else{
        reader.stop();
        return false;
      }
    }
    if(marker instanceof RepeatedMarker){
      while(null!=(line = reader.nextLine())){
        row++;
        RepeatedMarker m = (RepeatedMarker)marker;
        switch(m.getOrientation()){
          case VERTICAL:
            if(row<m.getRow()){
              continue;
            }
            switch(m.getSpacePosition()){
              case HEAD:
              case HEAD_AND_TAIL:
                for(int i=0,j=m.getSpace();i<j;i++){
                  line = reader.nextLine();
                  row++;
                }
                break;
              default:
            }
            int width = m.getWidth();
            int height = m.getHeight();
            int col = m.getCol();
            List<List<String>> l = new ArrayList<List<String>>(height);
            int lineWidth = line.size();
            List<String> range = new ArrayList<String>(width);
            for(int i=col,j=col+width;i<j;i++){
              range.add(i<lineWidth?line.get(i):"");
            }
            l.add(range);
            for(int x=1;x<height;x++){
              line = reader.nextLine();
              row++;
              if(null==line){
                line = new ArrayList<String>(0);
              }
              lineWidth = line.size();
              range = new ArrayList<String>(width);
              for(int i=col,j=col+width;i<j;i++){
                range.add(i<lineWidth?line.get(i):"");
              }
              l.add(range);
            }
            switch(m.getSpacePosition()){
              case TAIL:
              case HEAD_AND_TAIL:
                for(int i=0,j=m.getSpace();i<j;i++){
                  line = reader.nextLine();
                  row++;
                }
                break;
              default:
            }
            node = new AbstractNode(m,l){};
            return true;
          case HORIZONTAL:
            break;
          default:
        }
      }
    }else {
      while(row!=marker.getRow()) {
        line = reader.nextLine();
        if(null==line) {
          break;
        }
        row++;
      }
      if(row==marker.getRow()){
        int width = marker.getWidth();
        int height = marker.getHeight();
        int col = marker.getCol();
        List<List<String>> l = new ArrayList<List<String>>(height);
        int lineWidth = line.size();
        List<String> range = new ArrayList<String>(width);
        for(int i=col,j=col+width;i<j;i++){
          range.add(i<lineWidth?line.get(i):"");
        }
        l.add(range);
        for(int x=1;x<height;x++){
          line = reader.nextLine();
          row++;
          if(null==line){
            line = new ArrayList<String>(0);
          }
          lineWidth = line.size();
          range = new ArrayList<String>(width);
          for(int i=col,j=col+width;i<j;i++){
            range.add(i<lineWidth?line.get(i):"");
          }
          l.add(range);
        }
        node = new AbstractNode(marker,l){};
        return true;
      }
    }
    reader.stop();
    return false;
  }

  public INode next(){
    if(null==marker){
      return null;
    }
    if(!(marker instanceof RepeatedMarker)){
      marker = null;
    }
    return node;
  }

  public void remove(){
    next();
  }
}
