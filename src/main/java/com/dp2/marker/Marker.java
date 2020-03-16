package com.dp2.marker;

import java.util.ArrayList;
import java.util.List;

/**
 * 标记
 *
 * @author 6tail
 *
 */
public class Marker{
  /** 名称 */
  private String name;
  /** 行序号，从0开始计 */
  private int row = 0;
  /** 列序号，从0开始计 */
  private int col = 0;
  /** 区域宽度 */
  private int width = 1;
  /** 区域高度 */
  private int height = 1;
  /** 子标记 */
  private List<Marker> children;

  public Marker(){}

  public Marker(int row,int col){
    this(row+","+col,row,col);
  }

  public Marker(String name,int row,int col){
    setName(name);
    setRow(row);
    setCol(col);
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public int getRow(){
    return row;
  }

  public void setRow(int row){
    this.row = row;
  }

  public int getCol(){
    return col;
  }

  public void setCol(int col){
    this.col = col;
  }

  public int getWidth(){
    return width;
  }

  public void setWidth(int width){
    this.width = width;
  }

  public int getHeight(){
    return height;
  }

  public void setHeight(int height){
    this.height = height;
  }

  public List<Marker> getChildren(){
    return children;
  }

  public void setChildren(List<Marker> children){
    this.children = children;
  }

  public void addChild(Marker child){
    if(null==children){
      children = new ArrayList<Marker>();
    }
    children.add(child);
  }

  @Override
  public String toString(){
    return name;
  }
}
