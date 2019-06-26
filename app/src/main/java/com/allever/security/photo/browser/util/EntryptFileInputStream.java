package com.allever.security.photo.browser.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 这个InputStream用于解密私密图片
 * 重写了read  skip  available 三个方法
 */
public class EntryptFileInputStream extends FileInputStream {

    /**
     * 真实的长度
     */
    private int mTrueLength;

    /**
     * 当前的位置
     */
    private long mIndex;

    /**
     * 中间位置的Position
     */
    private long mMiddlePosition;

    /**
     * 最后位置的Position
     */
    private long mLastPosition;

    public EntryptFileInputStream(File file) throws IOException {
        super(file);
        int size = super.available();
        mIndex = 0;
        mTrueLength = size - EncryptUtil.SIZE;
        //跳掉头
        super.skip(1);
        mIndex++;
        mMiddlePosition = mIndex + mTrueLength / 2;
        mLastPosition = size - 1;
    }

    public EntryptFileInputStream(FileDescriptor fd) throws IOException {
        super(fd);
        int size = super.available();
        mIndex = 0;
        mTrueLength = size - EncryptUtil.SIZE;
        //跳掉头
        super.skip(1);
        mIndex++;
        mMiddlePosition = mIndex + mTrueLength / 2;
        mLastPosition = size - 1;
    }

    @Override
    public int read() throws IOException {
        if (mIndex == mMiddlePosition || mIndex == mLastPosition) {
            long count = super.skip(1);
            if (count > 0) {
                mIndex = mIndex + count;
            }
        }
        int num = super.read();
        if (num > 0) {
            mIndex = mIndex + num;
        }
        return num;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount)
            throws IOException {
        long newIndex = mIndex + byteCount;

        if (newIndex <= mMiddlePosition) {
            int i = super.read(buffer, byteOffset, byteCount);
            mIndex = mIndex + i;
            return i;
        } else if (newIndex > mMiddlePosition && newIndex < mLastPosition) {
            if (mIndex <= mMiddlePosition) {
                int count = (int) (mMiddlePosition - mIndex);
                int i1 = super.read(buffer, byteOffset, count);
                super.skip(1);
                int i2 = super.read(buffer, byteOffset + i1, byteCount - count);
                mIndex = mIndex + i1 + 1 + i2;
                return i1 + i2;
            } else {
                int i = super.read(buffer, byteOffset, byteCount);
                mIndex = mIndex + i;
                return i;
            }
        } else {//newIndex >= mLastPosition
            if (mIndex <= mMiddlePosition) {
                int count1 = (int) (mMiddlePosition - mIndex);
                int i1 = super.read(buffer, byteOffset, count1);
                mIndex = mIndex + i1;
                super.skip(1);
                mIndex = mIndex + 1;
                int i2 = super.read(buffer, byteOffset + i1, (int) (mLastPosition - mIndex));
                mIndex = mIndex + i2;
                super.skip(1);
                mIndex = mIndex + 1;
                return i1 + i2;
            } else if (mIndex > mMiddlePosition && mIndex <= mLastPosition) {
                int count = (int) (mLastPosition - mIndex);
                int i1 = super.read(buffer, byteOffset, count);
                super.skip(1);
                mIndex = mIndex + i1 + 1;
                return i1;
            } else {//mIndex > mLastPosition     -1
                return super.read(buffer, byteOffset, byteCount);
            }
        }
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        long newIndex = mIndex + byteCount;
        if (newIndex <= mMiddlePosition) {
            long i = super.skip(byteCount);
            mIndex = mIndex + i;
            return i;
        } else if (newIndex > mMiddlePosition && newIndex < mLastPosition) {
            if (mIndex <= mMiddlePosition) {
                long count = mMiddlePosition - mIndex;
                long i1 = super.skip(count);
                super.skip(1);
                long i2 = super.skip(byteCount - count);
                mIndex = mIndex + i1 + 1 + i2;

                return i1 + i2;
            } else {
                long i = super.skip(byteCount);
                mIndex = mIndex + i;
                return i;
            }
        } else {//newIndex >= mLastPosition
            if (mIndex <= mMiddlePosition) {
                long count1 = mMiddlePosition - mIndex;
                long i1 = super.skip(count1);
                mIndex = mIndex + i1;
                super.skip(1);
                mIndex = mIndex + 1;
                long i2 = super.skip(mLastPosition - mIndex);
                mIndex = mIndex + i2;
                super.skip(1);
                mIndex = mIndex + 1;
                return i1 + i2;
            } else if (mIndex > mMiddlePosition && mIndex <= mLastPosition) {
                long count = mLastPosition - mIndex;
                long i1 = super.skip(count);
                super.skip(1);
                mIndex = mIndex + i1 + 1;
                return i1;
            } else {//mIndex > mLastPosition   -1
                return super.skip(byteCount);
            }
        }
    }

    @Override
    public int available() throws IOException {
        if (mIndex <= mMiddlePosition) {
            return (super.available() - 2);
        } else if (mIndex <= mLastPosition) {
            return (super.available() - 1);
        } else {
            return super.available();
        }
    }
}
