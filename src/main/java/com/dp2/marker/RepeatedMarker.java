package com.dp2.marker;

/**
 * 重复节点标记
 *
 * @author 6tail
 */
public class RepeatedMarker extends Marker {
  /**
   * 间隔数
   */
  private int space = 0;

  /**
   * 排列方向
   */
  private Orientation orientation = Orientation.VERTICAL;

  /**
   * 间隔位置
   */
  private SpacePosition spacePosition = SpacePosition.TAIL;

  public RepeatedMarker() {
  }

  public RepeatedMarker(int row, int col) {
    super(row, col);
  }

  public RepeatedMarker(String name, int row, int col) {
    super(name, row, col);
  }

  public RepeatedMarker(String name, int row, int col, int width, int height) {
    super(name, row, col, width, height);
  }

  public int getSpace() {
    return space;
  }

  public void setSpace(int space) {
    this.space = space;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  public SpacePosition getSpacePosition() {
    return spacePosition;
  }

  public void setSpacePosition(SpacePosition spacePosition) {
    this.spacePosition = spacePosition;
  }
}