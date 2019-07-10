package com.allever.security.photo.browser.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.allever.security.photo.browser.util.MediaTypeUtil;

public class ThumbnailBean implements Parcelable {
    /***
     * 源文件路径
     */
    private String originPath;
    /***
     * 解密临时文件路径(运行时存在)
     */
    private String tempPath;
    private long timeMillis;
    private Uri uri;
    /***
     * 媒体类型： 图片，视频，文本
     */
    private int mediaType = MediaTypeUtil.INSTANCE.getTYPE_OTHER_IMAGE();
    private long duration;

    private int mSelectCount = 0;
    private boolean autoPlay = false;

    public static final int SYSTEM = 0;
    public static final int DECODE = 1;
    /***
     * 资源来源： 系统，解密
     * 资源来源  0是默认来自系统相册、1是来自解密的资源
     */
    private int sourceType = SYSTEM;

    private boolean isChecked = false;

    public ThumbnailBean() {

    }

    public boolean isInvalid() {
        return TextUtils.isEmpty(originPath) || TextUtils.isEmpty(tempPath);
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public ThumbnailBean(String path, long date, boolean isChecked, Uri uri, int degree, int type) {
        this.originPath = path;
        this.timeMillis = date;
        this.uri = uri;
        this.mediaType = type;
        this.isChecked = isChecked;
    }

    public ThumbnailBean(Parcel source) {
        originPath = source.readString();
        tempPath = source.readString();
        timeMillis = source.readLong();
        uri = Uri.parse(source.readString());
        mediaType = source.readInt();
        sourceType = source.readInt();
        autoPlay = source.readInt() == 1;
        isChecked = source.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originPath);
        dest.writeString(tempPath);
        dest.writeLong(timeMillis);
        dest.writeString(uri.toString());
        dest.writeInt(mediaType);
        dest.writeInt(sourceType);
        dest.writeInt(autoPlay ? 1 : 0);
        dest.writeInt(isChecked ? 1 : 0);
    }

    public static final Creator<ThumbnailBean> CREATOR = new Creator<ThumbnailBean>() {

        @Override
        public ThumbnailBean[] newArray(int size) {
            return new ThumbnailBean[size];
        }

        @Override
        public ThumbnailBean createFromParcel(Parcel source) {
            return new ThumbnailBean(source);
        }
    };

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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri mUri) {
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

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
