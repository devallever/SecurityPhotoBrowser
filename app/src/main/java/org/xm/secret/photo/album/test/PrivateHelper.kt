//package org.xm.secret.photo.album.test
//
//import android.util.Log
//import org.xm.secret.photo.album.util.FileUtils
//import java.io.*
//import java.lang.Exception
//import kotlin.experimental.xor
//
//object PrivateHelper {
//
//    private val TAG = PrivateHelper::class.java.simpleName
//
//    /***
//     * 加密
//     */
//    fun encode(originPath: String, enDecodeListener: EnDecodeListener?) {
//        enDecodeListener?.onStart()
//
//        val originFile = File(originPath)
//        if (!originFile.exists()) {
//            //原文件不存在
//            Log.d(TAG, "origin file not exist!")
//            enDecodeListener?.onFail()
//            return
//        }
//
//        val headerBean = HeaderBean(originPath)
//        val headBytes = headerBean.buildHeader()
//        val baos = ByteArrayOutputStream()
//        val bufferInputStream = BufferedInputStream(FileInputStream(File(originPath)))
//        val fileOutputStream = FileOutputStream(File(headerBean.encodePath))
//        try {
//            //写入数据头
//            baos.write(headBytes, 0, headBytes.size)
//            //写入数据体
//            var buffer = ByteArray(1024)
//            var len = -1
//            while (bufferInputStream.read(buffer).also { len = it } != -1) {
//                buffer = encodeByteArray(buffer, headerBean.key)
//                baos.write(buffer, 0, len)
//            }
//
//            fileOutputStream.write(baos.toByteArray())
//            enDecodeListener?.onSuccess(headerBean.encodePath)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            enDecodeListener?.onFail()
//        } finally {
//            baos.close()
//            bufferInputStream.close()
//            fileOutputStream.close()
//        }
//    }
//
//    /***
//     * 解密
//     */
//    fun decode(encodePath: String, enDecodeListener: EnDecodeListener?) {
//        enDecodeListener?.onStart()
//
//        if (!FileUtils.checkExist(encodePath)) {
//            Log.d(TAG, "encodeFile not exist!")
//            enDecodeListener?.onFail()
//            return
//        }
//
//        val headerBean = HeaderBean()
//        val resolveResult = headerBean.resolveHeader(encodePath)
//        if (!resolveResult) {
//            Log.d(TAG, "resolve header fail!")
//            return
//        }
//
//        val bufferedInputStream = BufferedInputStream(FileInputStream(File(encodePath)))
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        val fileOutputStream = FileOutputStream(File(headerBean.originPath))
//        try {
//            //如果要跳过1个字节数，传的是1
//            //跳过数据头，读取源文件数据
//            bufferedInputStream.skip(headerBean.originOffset.toLong())
//            val key = headerBean.key.toByte()
//            var len = -1
//            var buffer = ByteArray(1024)
//            while (bufferedInputStream.read(buffer).also { len = it } != -1) {
//                buffer = encodeByteArray(buffer, headerBean.key)
//                byteArrayOutputStream.write(buffer, 0, len)
//            }
//            fileOutputStream.write(byteArrayOutputStream.toByteArray())
//            enDecodeListener?.onSuccess(headerBean.originPath)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            enDecodeListener?.onFail()
//        } finally {
//            bufferedInputStream.close()
//            byteArrayOutputStream.close()
//            fileOutputStream.close()
//        }
//    }
//
//    /***
//     * 加密字节数组
//     */
//    private fun encodeByteArray(byteArray: ByteArray, key: Int): ByteArray {
//        //异或操作
//        for ((index, b) in byteArray.withIndex()) {
//            byteArray[index] = encodeByte(b, key)
//        }
//        return byteArray
//    }
//
//    /***
//     * 加密字节
//     */
//    private fun encodeByte(byte: Byte, key: Int): Byte {
//        return byte.xor(key.toByte())
//    }
//
//
//
//}