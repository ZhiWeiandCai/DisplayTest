package com.liftcore.android.displaytest.model;

/**
 * 表示显示的一个实体（视频，图片或者文本）
 * Created by Harry on 2017/7/28.
 */

public class DisplayEntry {

    private int sortFlag;
    private String path;
    private int xLoc;
    private int yLoc;
    private int width;
    private int height;

    public int getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(int sortFlag) {
        this.sortFlag = sortFlag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getxLoc() {
        return xLoc;
    }

    public void setxLoc(int xLoc) {
        this.xLoc = xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public void setyLoc(int yLoc) {
        this.yLoc = yLoc;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
