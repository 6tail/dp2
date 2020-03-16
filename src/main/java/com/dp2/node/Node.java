package com.dp2.node;

import com.dp2.marker.Marker;

/**
 * 节点
 *
 * @author 6tail
 *
 */
public class Node extends AbstractNode{

  public Node(String markerName){
    setMarker(new Marker(markerName,0,0));
  }

  public Node(String markerName,String value){
    this(markerName);
    setValue(value);
  }

}
