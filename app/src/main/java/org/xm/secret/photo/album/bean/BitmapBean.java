package org.xm.secret.photo.album.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import org.xm.secret.photo.album.util.MediaTypeUtil;

/**
 * 数据体
 */
public class BitmapBean implements Parcelable {
    public int mId;
    public Uri mUri;
    public int mDegree = 0;
    public long mDate;

    public int mWidth = 0;

    public int mHeight = 0;
    /**
     * mType 是图片 GIF PNG JPG 或者视频
     */
    public int mType = MediaTypeUtil.INSTANCE.getTYPE_OTHER_IMAGE();

    public boolean mIsAble = true;

    /**
     * 这个路径必须存在
     */
    public String mPath;
    /**
     * 解密文件的缓存路径
     */
    public String tempPath;

    /**
     * 视频时长 单位ms
     **/
    public long mDuration;

    /**
     * 是否有音轨
     **/
    public boolean mHasAudio;
    private int sourceType = 0; //资源来源  0是默认来自系统相册、1是来自解密的资源

    public BitmapBean() {

    }

    public BitmapBean(Parcel source) {
        mId = source.readInt();
        String str = source.readString();
        mUri = (str != null) ? Uri.parse(str) : null;
        mDegree = source.readInt();
        mDate = source.readLong();
        mWidth = source.readInt();
        mHeight = source.readInt();
        mType = source.readInt();
        mIsAble = source.readInt() == 1;
        mPath = source.readString();
        mDuration = source.readLong();
        mHasAudio = source.readInt() == 1;
        sourceType = source.readInt();
        tempPath = source.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString((mUri != null) ? mUri.toString() : null);
        dest.writeInt(mDegree);
        dest.writeLong(mDate);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeInt(mType);
        dest.writeInt(mIsAble ? 1 : 0);
        dest.writeString(mPath);
        dest.writeLong(mDuration);
        dest.writeInt(mHasAudio ? 1 : 0);
        dest.writeInt(sourceType);
        dest.writeString(tempPath);
    }

    public static final Creator<BitmapBean> CREATOR = new Creator<BitmapBean>() {

        @Override
        public BitmapBean[] newArray(int size) {
            return new BitmapBean[size];
        }

        @Override
        public BitmapBean createFromParcel(Parcel source) {
            return new BitmapBean(source);
        }
    };

//	@Override
//	public String toString() {
//		return "BitmapBean{" +
//				"mDate=" + mDate +
//				", mId=" + mId +
//				", mUri=" + mUri +
//				", mDegree=" + mDegree +
//				", mWidth=" + mWidth +
//				", mHeight=" + mHeight +
//				", mType=" + mType +
//				", mIsAble=" + mIsAble +
//				", mPath=" + mPath +
//				", mDuration='" + mDuration +
//				", mHasAudio='" + mHasAudio + '\'' +
//				'}';
//	}

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof BitmapBean)) {
            return false;
        }
        BitmapBean bean = (BitmapBean) o;
        if (mUri == null) {
            return bean.mUri == null;
        }
        return mUri.equals(bean.mUri);
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
}
