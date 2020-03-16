package com.dp2.marker;

import java.util.Comparator;

/**
 * 标记比较器
 *
 * @author 6tail
 *
 */
public class MarkerComparator implements Comparator<Marker>{
  public int compare(Marker a,Marker b){
    int row = a.getRow()-b.getRow();
    if(0!=row){
      return row;
    }
    return a.getCol()-b.getCol();
  }
}
