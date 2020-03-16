package com.dp2.node;

import java.util.ArrayList;
import java.util.List;
import com.dp2.marker.Marker;
import com.dp2.marker.RepeatedMarker;

/**
 * 抽象节点
 *
 * @author 6tail
 *
 */
public abstract class AbstractNode implements INode{
  protected Marker marker;
  protected String value;
  protected List<INode> children;
  protected NodeType type = NodeType.text;

  public AbstractNode(){}

  public NodeType getType(){
    return type;
  }

  public void setType(NodeType type) {
    this.type = type;
  }

  public AbstractNode(Marker marker,List<List<String>> lines){
    this.marker = marker;
    StringBuilder s = new StringBuilder();
    for(int i = 0,j = marker.getHeight();i<j;i++){
      List<String> line = lines.get(i);
      for(int x = 0,y = marker.getWidth();x<y;x++){
        s.append(line.get(x));
      }
    }
    this.value = s.toString();
    List<Marker> markers = marker.getChildren();
    if(null!=markers){
      int lineHeight = lines.size();
      for(Marker m:markers){
        int col = m.getCol();
        int row = m.getRow();
        int width = m.getWidth();
        int height = m.getHeight();
        if(m instanceof RepeatedMarker){
          RepeatedMarker rm = (RepeatedMarker)m;
          switch(rm.getOrientation()){
            case VERTICAL:
              int rowIndex = row;
              while(rowIndex<lineHeight){
                switch(rm.getSpacePosition()){
                  case HEAD:
                  case HEAD_AND_TAIL:
                    rowIndex += rm.getSpace();
                    break;
                  default:
                }
                List<String> line = rowIndex<lineHeight?lines.get(rowIndex):new ArrayList<String>(0);
                List<List<String>> l = new ArrayList<List<String>>(height);
                int lineWidth = line.size();
                List<String> range = new ArrayList<String>(width);
                for(int i=col,j=col+width;i<j;i++){
                  range.add(i<lineWidth?line.get(i):"");
                }
                l.add(range);
                addChild(new AbstractNode(rm,l){});
                switch(rm.getSpacePosition()){
                  case TAIL:
                  case HEAD_AND_TAIL:
                    rowIndex += rm.getSpace();
                    break;
                  default:
                }
                rowIndex++;
              }
              break;
            case HORIZONTAL:
              break;
            default:
          }
        }else{
          List<List<String>> l = new ArrayList<List<String>>(height);
          for(int x = row,y = row+height;x<y;x++){
            List<String> line = x<lineHeight?lines.get(x):new ArrayList<String>(0);
            int lineWidth = line.size();
            List<String> range = new ArrayList<String>(width);
            for(int i = col,j = col+width;i<j;i++){
              range.add(i<lineWidth?line.get(i):"");
            }
            l.add(range);
          }
          addChild(new AbstractNode(m,l){});
        }
      }
    }
  }

  public Marker getMarker(){
    return marker;
  }

  public void setMarker(Marker marker){
    this.marker = marker;
  }

  public String getValue(){
    return value;
  }

  public void setValue(String value){
    this.value = value;
  }

  public List<INode> getChildren(){
    return children;
  }

  public void setChildren(List<INode> children){
    this.children = children;
  }

  public void addChild(INode child){
    if(null==children){
      children = new ArrayList<INode>();
    }
    children.add(child);
  }
}
