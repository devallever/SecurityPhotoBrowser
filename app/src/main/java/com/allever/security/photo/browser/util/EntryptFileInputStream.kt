package com.allever.security.photo.browser.util

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException

/**
 * 这个InputStream用于解密私密图片
 * 重写了read  skip  available 三个方法
 */
class EntryptFileInputStream : FileInputStream {

    /**
     * 真实的长度
     */
    private var mTrueLength: Int = 0

    /**
     * 当前的位置
     */
    private var mIndex: Long = 0

    /**
     * 中间位置的Position
     */
    private var mMiddlePosition: Long = 0

    /**
     * 最后位置的Position
     */
    private var mLastPosition: Long = 0

    @Throws(IOException::class)
    constructor(file: File) : super(file) {
        val size = super.available()
        mIndex = 0
        mTrueLength = size - EncryptUtil.SIZE
        //跳掉头
        super.skip(1)
        mIndex++
        mMiddlePosition = mIndex + mTrueLength / 2
        mLastPosition = (size - 1).toLong()
    }

    @Throws(IOException::class)
    constructor(fd: FileDescriptor) : super(fd) {
        val size = super.available()
        mIndex = 0
        mTrueLength = size - EncryptUtil.SIZE
        //跳掉头
        super.skip(1)
        mIndex++
        mMiddlePosition = mIndex + mTrueLength / 2
        mLastPosition = (size - 1).toLong()
    }

    @Throws(IOException::class)
    override fun read(): Int {
        if (mIndex == mMiddlePosition || mIndex == mLastPosition) {
            val count = super.skip(1)
            if (count > 0) {
                mIndex = mIndex + count
            }
        }
        val num = super.read()
        if (num > 0) {
            mIndex = mIndex + num
        }
        return num
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray): Int {
        return read(buffer, 0, buffer.size)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, byteOffset: Int, byteCount: Int): Int {
        val newIndex = mIndex + byteCount

        if (newIndex <= mMiddlePosition) {
            val i = super.read(buffer, byteOffset, byteCount)
            mIndex = mIndex + i
            return i
        } else if (newIndex > mMiddlePosition && newIndex < mLastPosition) {
            if (mIndex <= mMiddlePosition) {
                val count = (mMiddlePosition - mIndex).toInt()
                val i1 = super.read(buffer, byteOffset, count)
                super.skip(1)
                val i2 = super.read(buffer, byteOffset + i1, byteCount - count)
                mIndex = mIndex + i1.toLong() + 1 + i2.toLong()
                return i1 + i2
            } else {
                val i = super.read(buffer, byteOffset, byteCount)
                mIndex = mIndex + i
                return i
            }
        } else {//newIndex >= mLastPosition
            if (mIndex <= mMiddlePosition) {
                val count1 = (mMiddlePosition - mIndex).toInt()
                val i1 = super.read(buffer, byteOffset, count1)
                mIndex = mIndex + i1
                super.skip(1)
                mIndex = mIndex + 1
                val i2 = super.read(buffer, byteOffset + i1, (mLastPosition - mIndex).toInt())
                mIndex = mIndex + i2
                super.skip(1)
                mIndex = mIndex + 1
                return i1 + i2
            } else if (mIndex > mMiddlePosition && mIndex <= mLastPosition) {
                val count = (mLastPosition - mIndex).toInt()
                val i1 = super.read(buffer, byteOffset, count)
                super.skip(1)
                mIndex = mIndex + i1.toLong() + 1
                return i1
            } else {//mIndex > mLastPosition     -1
                return super.read(buffer, byteOffset, byteCount)
            }
        }
    }

    override fun mark(readlimit: Int) {
        super.mark(readlimit)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        super.reset()
    }

    @Throws(IOException::class)
    override fun skip(byteCount: Long): Long {
        val newIndex = mIndex + byteCount
        if (newIndex <= mMiddlePosition) {
            val i = super.skip(byteCount)
            mIndex = mIndex + i
            return i
        } else if (newIndex > mMiddlePosition && newIndex < mLastPosition) {
            if (mIndex <= mMiddlePosition) {
                val count = mMiddlePosition - mIndex
                val i1 = super.skip(count)
                super.skip(1)
                val i2 = super.skip(byteCount - count)
                mIndex = mIndex + i1 + 1 + i2

                return i1 + i2
            } else {
                val i = super.skip(byteCount)
                mIndex = mIndex + i
                return i
            }
        } else {//newIndex >= mLastPosition
            if (mIndex <= mMiddlePosition) {
                val count1 = mMiddlePosition - mIndex
                val i1 = super.skip(count1)
                mIndex = mIndex + i1
                super.skip(1)
                mIndex = mIndex + 1
                val i2 = super.skip(mLastPosition - mIndex)
                mIndex = mIndex + i2
                super.skip(1)
                mIndex = mIndex + 1
                return i1 + i2
            } else if (mIndex > mMiddlePosition && mIndex <= mLastPosition) {
                val count = mLastPosition - mIndex
                val i1 = super.skip(count)
                super.skip(1)
                mIndex = mIndex + i1 + 1
                return i1
            } else {//mIndex > mLastPosition   -1
                return super.skip(byteCount)
            }
        }
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return if (mIndex <= mMiddlePosition) {
            super.available() - 2
        } else if (mIndex <= mLastPosition) {
            super.available() - 1
        } else {
            super.available()
        }
    }
}
