package com.allever.security.photo.browser.function.endecode

import android.util.Log
import com.allever.security.photo.browser.util.BytesUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

class PrivateBean {
    companion object {
        val TAG = PrivateBean::class.java.name

        //魔法值4个字节（-PE-）
        const val VALUE_MAGIC = "-AL-"

    }

    //密钥 对图片和图片路径
    val VALUE_KEY = Random().nextInt(8) + 1


    //魔法值
    var magic: String = ""
    //文件原始数据偏移量（4个字节）
    var originalOffset: Int = 0
    //文件原始数据长度（8个字节）
    var originalSize: Long = 0L
    //加密时间戳（8个字节）
    var encodeTimeMills: Long = 0L
    var originalPath: String = ""
    var albumPath: String = ""
    //加密后图像路径
    var encodePath: String = ""
    var key: Int = 0

    //解码后临时缓存文件路径
    var tempPath = ""

    constructor(originalSize: Long, encodeTimeMills: Long, originalPath: String, album: String) {
        this.originalSize = originalSize
        this.encodeTimeMills = encodeTimeMills
        this.originalPath = originalPath
        this.albumPath = album
    }


    constructor() {}

    /**
     * 构建数据头
     *
     * 魔法值4个字节（-PE-）
     * 文件原始数据偏移量（4个字节）
     * 文件原始数据长度（8个字节）
     * 加密时间戳（8个字节）
     * 4个字节存储长度、n字节文件原始路径
     * 4个字节存储长度、n字节相册目录
     * 4个字节存储长度、n字节加密后路径
     * 4个字节存储长度、n字节解码后临时缓存文件路径
     * 1个字节秘钥
     */
    fun buildHead(): ByteArray {
        var buffer = ByteArray(0)
        //文件原始路径
        val originalPathBytes = PrivateHelper.deBytes(originalPath.toByteArray(), VALUE_KEY)
        //相册目录
        // 暂时去除目录名字
//        val albumPathBytes = PrivateHelper.deBytes(albumPath.toByteArray(), VALUE_KEY)
        val albumPathBytes = PrivateHelper.deBytes(ByteArray(0), VALUE_KEY)
        //加密路径长度
        val encodePathBytes = PrivateHelper.deBytes(encodePath.toByteArray(), VALUE_KEY)
        //缓存文件路径长度
        val tempPathBytes = PrivateHelper.deBytes(tempPath.toByteArray(), VALUE_KEY)
        try {
            //总偏移量
            var totalOffsetSize = 0
            //相对偏移
            var offset = 0

            val magic = VALUE_MAGIC.toByteArray()
            totalOffsetSize += magic.size


            //文件原始数据偏移量
            totalOffsetSize += 4

            //文件原始数据长度
            val originalSizeBtyes = BytesUtils.longToBytes(originalSize)
            totalOffsetSize += originalSizeBtyes.size

            //加密时间戳
            val encodeTimeMillsBtyes = BytesUtils.longToBytes(encodeTimeMills)
            totalOffsetSize += encodeTimeMillsBtyes.size
            //
            totalOffsetSize += originalPathBytes.size
            totalOffsetSize += albumPathBytes.size
            totalOffsetSize += encodePathBytes.size
            totalOffsetSize += tempPathBytes.size
            //分别对应上面存储路径的(4字节)
            totalOffsetSize += 4 + 4 + 4 + 4 + 1

            //数据头
            buffer = ByteArray(totalOffsetSize)
            //-----------------存数据-------------------
            //添加魔法值 -PE-
            offset = copyBytes(magic, buffer, offset)
            //文件原始数据偏移量
            val totalOffsetSizeBuffer = BytesUtils.intToByteArray(totalOffsetSize)
            offset = copyBytes(totalOffsetSizeBuffer, buffer, offset)
            //文件原始数据长度
            offset = copyBytes(originalSizeBtyes, buffer, offset)
            //加密时间戳
            offset = copyBytes(encodeTimeMillsBtyes, buffer, offset)
            //文件原始路径长度的四个字节
            val originalPathSizeBytes = BytesUtils.intToByteArray(originalPathBytes.size)
            offset = copyBytes(originalPathSizeBytes, buffer, offset)
            offset = copyBytes(originalPathBytes, buffer, offset)
            //相册目录长度的四个字节
            val albumPathSizeBytes = BytesUtils.intToByteArray(albumPathBytes.size)
            offset = copyBytes(albumPathSizeBytes, buffer, offset)
            offset = copyBytes(albumPathBytes, buffer, offset)
            //加密路径长度的四个字节
            val encodePathSizeBytes = BytesUtils.intToByteArray(encodePathBytes.size)
            offset = copyBytes(encodePathSizeBytes, buffer, offset)
            offset = copyBytes(encodePathBytes, buffer, offset)
            //缓存文件路径长度的四个字节
            val tempPathSizeBytes = BytesUtils.intToByteArray(tempPathBytes.size)
            offset = copyBytes(tempPathSizeBytes, buffer, offset)
            offset = copyBytes(tempPathBytes, buffer, offset)
            //密钥1个字节
            buffer[offset] = BytesUtils.intToByte(VALUE_KEY)
            originalOffset = buffer.size

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer

    }

    /**
     * 拷贝字节
     * @param srcBytes  源
     * @param disBytes  目的
     * @param offset   偏移量
     */
    private fun copyBytes(srcBytes: ByteArray, disBytes: ByteArray, offset: Int): Int {
        System.arraycopy(srcBytes, 0, disBytes, offset, srcBytes.size)
        return offset + srcBytes.size
    }

    /**
     * 截取字节
     * @param srcBytes  源
     * @param disBytes  目的
     * @param offset   偏移量
     * @param size 截取多长
     */
    private fun cutBytes(srcBytes: ByteArray, disBytes: ByteArray, offset: Int, size: Int): Int {
        System.arraycopy(srcBytes, offset, disBytes, 0, size)
        return offset + size
    }

    /**
     * 解析数据头
     */
    fun resolveHead(bytes: ByteArray): Boolean {
        try {
            if (bytes.isEmpty()) return false
            //相对偏移量
            var offset = 0

            var buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            //魔法值
            val magic = String(buffer)
            this.magic = magic
            if (magic != VALUE_MAGIC) {
                Log.e(TAG, "invalid photo")
                return false
            }
            buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            //文件原始数据偏移量
            val originalOffset = BytesUtils.byteArrayToInt(buffer)
            this.originalOffset = originalOffset

            buffer = ByteArray(8)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            //文件原始数据长度
            val originalSize = BytesUtils.bytesToLong(buffer)
            this.originalSize = originalSize

            buffer = ByteArray(8)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            //加密时间戳
            val timeMills = BytesUtils.bytesToLong(buffer)
            this.encodeTimeMills = timeMills

            // 4个字节存储长度
            buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            val oPathSize = BytesUtils.byteArrayToInt(buffer)
            //文件原始路径
            val originalPathBuffer = ByteArray(oPathSize)
            offset = cutBytes(bytes, originalPathBuffer, offset, originalPathBuffer.size)

            // 4个字节存储长度
            buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            val albumSize = BytesUtils.byteArrayToInt(buffer)
            //相册目录
            val albumPathBuffer = ByteArray(albumSize)
            offset = cutBytes(bytes, albumPathBuffer, offset, albumPathBuffer.size)

            // 4个字节存储长度
            buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            val encodeSize = BytesUtils.byteArrayToInt(buffer)
            //加密路径
            val encodePathBuffer = ByteArray(encodeSize)
            offset = cutBytes(bytes, encodePathBuffer, offset, encodePathBuffer.size)

            // 4个字节存储长度
            buffer = ByteArray(4)
            offset = cutBytes(bytes, buffer, offset, buffer.size)
            val tempPathSize = BytesUtils.byteArrayToInt(buffer)
            //缓存文件路径
            val tempPathBuffer = ByteArray(tempPathSize)
            offset = cutBytes(bytes, tempPathBuffer, offset, tempPathBuffer.size)

            //一个字节密钥
            this.key = BytesUtils.byteToInt(bytes[offset])

            //解码
            this.originalPath = String(PrivateHelper.deBytes(originalPathBuffer, key))
            this.albumPath = String(PrivateHelper.deBytes(albumPathBuffer, key))
            this.encodePath = String(PrivateHelper.deBytes(encodePathBuffer, key))
            this.tempPath = String(PrivateHelper.deBytes(tempPathBuffer, key))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 解析数据头
     * 1024 个字节
     */
    fun resolveHead(path: String): Boolean {
        if (!File(path).exists()) {
            Log.e(TAG, "file is not found : path $path")
            return false
        }
        val fis = FileInputStream(path)
        val buffer = ByteArray(1024)
        val len = fis.read(buffer)
        val outStream = ByteArrayOutputStream()
        outStream.write(buffer, 0, len)
        val data = outStream.toByteArray()
        outStream.close()
        fis.close()
        return resolveHead(data)
    }
}