package com.dp2.node;

import com.dp2.marker.Marker;

/**
 * 节点
 *
 * @author 6tail
 */
public class Node extends AbstractNode {

  public Node(String markerName) {
    this(markerName, null);
  }

  public Node(Marker marker) {
    this(marker, null);
  }

  public Node(String markerName, String value) {
    this(new Marker(markerName, 0, 0), value);
  }

  public Node(Marker marker, String value) {
    setMarker(marker);
    setValue(value);
  }

}
