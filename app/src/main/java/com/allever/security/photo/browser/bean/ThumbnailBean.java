package com.allever.security.photo.browser.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.allever.security.photo.browser.util.MediaTypeUtil;

public class ThumbnailBean implements Parcelable {
    private String mPath;
    private String tempPath;    //临时缓存的文件路径
    private long mDate;
    private boolean isChecked = false;
    private Uri mUri;
    private int mDegree = 0;
    private int mType = MediaTypeUtil.TYPE_OTHER_IMAGE;
    private long mDuration;

    private int mSelectCount = 0;

    public static final int SYSTEM = 0;
    public static final int DECODE = 1;
    private int sourceType = SYSTEM; //资源来源  0是默认来自系统相册、1是来自解密的资源

    public ThumbnailBean() {

    }

    public boolean isInvalid() {
        return TextUtils.isEmpty(mPath) || TextUtils.isEmpty(tempPath);
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public ThumbnailBean(String path, long date, boolean isChecked, Uri uri, int degree, int type) {
        mPath = path;
        mDate = date;
        this.isChecked = isChecked;
        mUri = uri;
        mDegree = degree;
        mType = type;
    }

    public ThumbnailBean(Parcel source) {
        mPath = source.readString();
        tempPath = source.readString();
        mDate = source.readLong();
        isChecked = source.readInt() == 1 ? true : false;
        mUri = Uri.parse(source.readString());
        mDegree = source.readInt();
        mType = source.readInt();
        sourceType = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(tempPath);
        dest.writeLong(mDate);
        dest.writeInt(isChecked ? 1 : 0);
        dest.writeString(mUri.toString());
        dest.writeInt(mDegree);
        dest.writeInt(mType);
        dest.writeInt(sourceType);
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

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
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

//	@Override
//	public String toString() {
//		return "ThumbnailBean{" +
//				"mPath='" + mPath + '\'' +
//				", mDate=" + mDate +
//				", isChecked=" + isChecked +
//				", mUri=" + mUri +
//				", mDegree=" + mDegree +
//				", mType=" + mType +
//				", mDuration=" + mDuration +
//				", mSelectCount=" + mSelectCount +
//				'}';
//	}

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
}
