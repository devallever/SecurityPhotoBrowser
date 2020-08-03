package org.xm.secret.photo.album.bean;

import org.xm.secret.photo.album.util.MediaTypeUtil;

import java.io.Serializable;

public class LocalThumbnailBean implements Serializable {
    private static final long serialVersionUID = -6743567631108323096L;
    private String originPath;
    private String tempPath;    //临时缓存的文件路径
    private long timeMillis;
    private boolean isChecked = false;
    private String uri;
    private int mediaType = MediaTypeUtil.INSTANCE.getTYPE_OTHER_IMAGE();
    private long duration;

    private int mSelectCount = 0;
    private int sourceType = 0; //资源来源  0是默认来自系统相册、1是来自解密的资源

    public LocalThumbnailBean() {

    }

    public LocalThumbnailBean(String path, long date, boolean isChecked, String uri, int degree, int type) {
        originPath = path;
        timeMillis = date;
        isChecked = isChecked;
        this.uri = uri;
        mediaType = type;
    }


    public String getPath() {
        return originPath;
    }

    public void setPath(String mPath) {
        this.originPath = mPath;
    }

    public long getDate() {
        return timeMillis;
    }

    public void setDate(long mDate) {
        this.timeMillis = mDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String mUri) {
        this.uri = mUri;
    }

    public int getType() {
        return mediaType;
    }

    public void setType(int mType) {
        this.mediaType = mType;
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public void setSelectCount(int selectCount) {
        mSelectCount = selectCount;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "ThumbnailBean{" +
                "originPath='" + originPath + '\'' +
                ", timeMillis=" + timeMillis +
                ", isChecked=" + isChecked +
                ", uri=" + uri +
                ", mediaType=" + mediaType +
                ", duration=" + duration +
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
