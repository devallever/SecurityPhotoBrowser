package com.allever.security.photo.browser.function.endecode

import android.util.Log
import java.io.*
import java.lang.Exception
import kotlin.experimental.xor

object PrivateHelper {

    private val TAG = PrivateHelper::class.java.simpleName

    /***
     * 加密
     */
    fun encode(originPath: String, enDecodeListener: EnDecodeListener?) {
        enDecodeListener?.onStart()

        val originFile = File(originPath)
        if (!originFile.exists()) {
            //原文件不存在
            Log.d(TAG, "origin file not exist!")
            enDecodeListener?.onFail()
            return
        }

        val privateHeader = PrivateHeader(originPath)
        val headBytes = privateHeader.buildHeader()
        val baos = ByteArrayOutputStream()
        val bufferInputStream = BufferedInputStream(FileInputStream(File(originPath)))
        val fileOutputStream = FileOutputStream(File(privateHeader.encodePath))
        try {
            //写入数据头
            baos.write(headBytes, 0, headBytes.size)
            //写入数据体
            val buffer = ByteArray(1024)
            var len = -1
            val key = privateHeader.key.toByte()
            while (bufferInputStream.read(buffer).also { len = it } != -1) {
                for ((index, b) in buffer.withIndex()) {
                    buffer[index] = b.xor(key)
                }
                baos.write(buffer, 0, len)
            }

            fileOutputStream.write(baos.toByteArray())
            enDecodeListener?.onSuccess(privateHeader.encodePath)
        } catch (e: Exception) {
            e.printStackTrace()
            enDecodeListener?.onFail()
        } finally {
            baos.close()
            bufferInputStream.close()
            fileOutputStream?.close()
        }
    }

    /***
     * 解密
     */
    fun decode(encodePath: String, enDecodeListener: EnDecodeListener?) {
        enDecodeListener?.onStart()

        val encodeFile = File(encodePath)
        if (!encodeFile.exists()) {
            Log.d(TAG, "encodeFile not exist!")
            enDecodeListener?.onFail()
            return
        }

        val privateHeader = PrivateHeader()
        privateHeader.resolveHeader(encodePath)
        val bufferedInputStream = BufferedInputStream(FileInputStream(File(encodePath)))
        val byteArrayOutputStream = ByteArrayOutputStream()
        val fileOutputStream = FileOutputStream(File(privateHeader.originPath))
        try {
            //如果要跳过1个字节数，传的是1
            bufferedInputStream.skip(privateHeader.originOffset.toLong())
            val key = privateHeader.key.toByte()
            var len = -1
            val buffer = ByteArray(1024)
            while (bufferedInputStream.read(buffer).also { len = it } != -1) {
                for ((index, b) in buffer.withIndex()) {
                    buffer[index] = b.xor(key)
                }
                byteArrayOutputStream.write(buffer, 0, len)
            }
            fileOutputStream.write(byteArrayOutputStream.toByteArray())
            enDecodeListener?.onSuccess(privateHeader.originPath)
        } catch (e: Exception) {
            e.printStackTrace()
            enDecodeListener?.onFail()
        } finally {
            bufferedInputStream.close()
            byteArrayOutputStream.close()
            fileOutputStream.close()
        }
    }


}