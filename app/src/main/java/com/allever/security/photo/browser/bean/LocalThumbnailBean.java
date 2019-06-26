package com.allever.security.photo.browser.bean;

import com.allever.security.photo.browser.util.MediaTypeUtil;

import java.io.Serializable;

public class LocalThumbnailBean implements Serializable {
    private static final long serialVersionUID = -6743567631108323096L;
    private String mPath;
    private String tempPath;    //临时缓存的文件路径
    private long mDate;
    private boolean isChecked = false;
    private String mUri;
    private int mDegree = 0;
    private int mType = MediaTypeUtil.INSTANCE.getTYPE_OTHER_IMAGE();
    private long mDuration;

    private int mSelectCount = 0;
    private int sourceType = 0; //资源来源  0是默认来自系统相册、1是来自解密的资源

    public LocalThumbnailBean() {

    }

    public LocalThumbnailBean(String path, long date, boolean isChecked, String uri, int degree, int type) {
        mPath = path;
        mDate = date;
        isChecked = isChecked;
        mUri = uri;
        mDegree = degree;
        mType = type;
    }


    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }

    public int getDegree() {
        return mDegree;
    }

    public void setDegree(int mDegree) {
        this.mDegree = mDegree;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public void setSelectCount(int selectCount) {
        mSelectCount = selectCount;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    @Override
    public String toString() {
        return "ThumbnailBean{" +
                "mPath='" + mPath + '\'' +
                ", mDate=" + mDate +
                ", isChecked=" + isChecked +
                ", mUri=" + mUri +
                ", mDegree=" + mDegree +
                ", mType=" + mType +
                ", mDuration=" + mDuration +
                ", mSelectCount=" + mSelectCount +
                '}';
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
}
