//package com.allever.security.photo.browser.test
//
//import android.os.Bundle
//import android.os.Environment
//import android.util.Log
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import com.allever.security.photo.browser.R
//import com.allever.security.photo.browser.util.BytesUtils
//
//import java.io.*
//import java.util.Random
//import kotlin.experimental.xor
//
//class TestActivityyy : AppCompatActivity(), View.OnClickListener {
//    private val mOriginOffset = 0
//    private val mOriginLength = 0L
//    private val mTimeMillis = 0L
//    private val mOriginPath = ""
//    private val mEncodePath = ""
//    private val mKey = Random().nextInt(8) + 1
//    //    private int mKey = KEY;
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_test)
//
//        findViewById<View>(R.id.btn_decode).setOnClickListener(this)
//        findViewById<View>(R.id.btn_encode).setOnClickListener(this)
//    }
//
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.btn_decode -> decode()
//            R.id.btn_encode -> encode()
//            else -> {
//            }
//        }
//    }
//
//    /***
//     * //    魔法值	        4
//     * //    原始数据偏移量	4
//     * //    原始数据长度	    8
//     * //    时间戳	        8
//     * //    原始路径       	n
//     * //    加密路径	        n
//     * //    原始路径的长度	4
//     * //    加密路径的长度	4
//     * //    秘钥	        1
//     */
//    private fun encode() {
//        //加密
//        val originPath = Environment.getExternalStorageDirectory().path + "/test.jpg"
//        val encodePath = Environment.getExternalStorageDirectory().absolutePath + "/test-private"
//        Log.d(TAG, "encode: originPath = $originPath")
//        Log.d(TAG, "encode: encodePath = $encodePath")
//
//
//        var bufferedInputStream: BufferedInputStream? = null
//        var byteArrayOutputStream: ByteArrayOutputStream? = null
//        var fileOutputStream: FileOutputStream? = null
//        try {
//            byteArrayOutputStream = ByteArrayOutputStream()
//
//            //魔法值
//            val magicBytes = MAGIC_VALUE.toByteArray()
//            Log.d(TAG, "encode: magicBytes.size = " + magicBytes.size)
//            //原始路径
//            val originPathBytes = originPath.toByteArray()
//            Log.d(TAG, "encode: originPathBytes.size = " + originPathBytes.size)
//            //加密路径
//            val encodePathBytes = encodePath.toByteArray()
//            Log.d(TAG, "encode: encodePathBytes.size = " + encodePathBytes.size)
//            //原始路径长度
//            val originPathLength = originPath.length
//            val originPathLengthBytes = BytesUtils.intToByteArray(originPathLength)
//            Log.d(TAG, "encode: originPathLengthBytes.size = " + originPathLengthBytes.size)
//            //加密路径长度
//            val encodePathLength = encodePath.length
//            val encodePathLengthBytes = BytesUtils.intToByteArray(encodePathLength)
//            Log.d(TAG, "encode: encodePathLengthBytes.size = " + encodePathLengthBytes.size)
//            //时间戳
//            val time = System.currentTimeMillis()
//            val timeBytes = BytesUtils.longToBytes(time)
//            Log.d(TAG, "encode: timeBytes.size = " + timeBytes.size)
//            //原始数据长度
//            val originFile = File(originPath)
//            val originFileLength = originFile.length()
//            val originFileLengthBytes = BytesUtils.longToBytes(originFileLength)
//            Log.d(TAG, "encode: originFileLengthBytes.size = " + originFileLengthBytes.size)
//            //key
//            val keyBytes = ByteArray(1)
//            keyBytes[0] = BytesUtils.intToByte(mKey)
//            Log.d(TAG, "encode: key = $mKey")
//            Log.d(TAG, "encode: keyBytes.size = " + keyBytes.size)
//            //原始数据偏移量 = 头部长度
//            val originOffset = magicBytes.size +
//                    originPathBytes.size +
//                    encodePathBytes.size +
//                    originPathLengthBytes.size +
//                    encodePathLengthBytes.size +
//                    timeBytes.size +
//                    originFileLengthBytes.size +
//                    keyBytes.size +
//                    4//本身
//            val originOffsetBytes = BytesUtils.intToByteArray(originOffset)
//            Log.d(TAG, "encode: originOffsetBytes.size = " + originOffsetBytes.size)
//            Log.d(TAG, "encode: head length.size = $originOffset")
//
//
//            /***
//             * * //    魔法值	        4
//             * * //    原始数据偏移量	    4
//             * * //    原始数据长度	    8
//             * * //    时间戳	        8
//             * * //    原始路径的长度	    4
//             * * //    原始路径       	n
//             * * //    加密路径的长度	    4
//             * * //    加密路径	        n
//             * * //    秘钥	            1
//             */
//            writeBytes(byteArrayOutputStream, magicBytes)
//            writeBytes(byteArrayOutputStream, originOffsetBytes)
//            writeBytes(byteArrayOutputStream, originFileLengthBytes)
//            writeBytes(byteArrayOutputStream, timeBytes)
//            writeBytes(byteArrayOutputStream, originPathLengthBytes)
//            writeBytes(byteArrayOutputStream, originPathBytes)
//            writeBytes(byteArrayOutputStream, encodePathLengthBytes)
//            writeBytes(byteArrayOutputStream, encodePathBytes)
//            writeBytes(byteArrayOutputStream, keyBytes)
//
//            val b = ByteArray(1024)
//            bufferedInputStream = BufferedInputStream(FileInputStream(originFile))
//            var len = 0
//            while ((len = bufferedInputStream.read(b)) > 0) {
//                for (i in 0 until len) {
//                    b[i] = (b[i] xor mKey.toByte()).toByte()
//                    //                    byteArrayOutputStream.write(b[i]);
//                }
//                byteArrayOutputStream.write(b, 0, len)
//            }
//            fileOutputStream = FileOutputStream(File(encodePath))
//            fileOutputStream.write(byteArrayOutputStream.toByteArray())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            try {
//                bufferedInputStream?.close()
//
//                byteArrayOutputStream?.close()
//
//                fileOutputStream?.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    private fun writeBytes(baos: ByteArrayOutputStream, bytes: ByteArray) {
//        baos.write(bytes, 0, bytes.size)
//    }
//
//    private fun decode() {
//        //解密
//        val privatePath = Environment.getExternalStorageDirectory().path + "/test-private"
//        val newPath = Environment.getExternalStorageDirectory().absolutePath + "/test-decode.jpg"
//        Log.d(TAG, "decode: privatePath = $privatePath")
//        Log.d(TAG, "decode: newPath = $newPath")
//
//        var bufferedInputStream: BufferedInputStream? = null
//        var byteArrayOutputStream: ByteArrayOutputStream? = null
//        var fileOutputStream: FileOutputStream? = null
//        try {
//            val b = ByteArray(1024)
//            bufferedInputStream = BufferedInputStream(FileInputStream(File(privatePath)))
//
//            val headBytesBuffer = ByteArray(1024)
//            val headLength = bufferedInputStream.read(headBytesBuffer)
//            val headBaos = ByteArrayOutputStream()
//            headBaos.write(headBytesBuffer, 0, headLength)
//            val headBytes = headBaos.toByteArray()
//            headBaos.close()
//            bufferedInputStream.close()
//
//
//            /***
//             * * //    魔法值	        4
//             * * //    原始数据偏移量	    4
//             * * //    原始数据长度	    8
//             * * //    时间戳	        8
//             * * //    原始路径的长度	    4
//             * * //    原始路径       	n
//             * * //    加密路径的长度	    4
//             * * //    加密路径	        n
//             * * //    秘钥	            1
//             */
//
//            var offset = 0
//
//            //魔法值
//            val magicBytes = ByteArray(4)
//            offset = cutBytes(headBytes, 0, magicBytes, 0, 4)
//            val magic = String(magicBytes)
//            Log.d(TAG, "decode: magic = $magic")
//            //原始数据偏移量
//            val originOffsetBytes = ByteArray(4)
//            offset = cutBytes(headBytes, offset, originOffsetBytes, 0, 4)
//            val originOffset = BytesUtils.byteArrayToInt(originOffsetBytes)
//            Log.d(TAG, "decode: originOffset = $originOffset")
//            //原始数据长度
//            val originFileLengthBytes = ByteArray(8)
//            offset = cutBytes(headBytes, offset, originFileLengthBytes, 0, 8)
//            val originFileLength = BytesUtils.bytesToLong(originFileLengthBytes)
//            Log.d(TAG, "decode: originFileLength = $originFileLength")
//            //时间戳
//            val timeBytes = ByteArray(8)
//            offset = cutBytes(headBytes, offset, timeBytes, 0, 8)
//            //原始路径的长度
//            val originPathLengthBytes = ByteArray(4)
//            offset = cutBytes(headBytes, offset, originPathLengthBytes, 0, 4)
//            val originPathLength = BytesUtils.byteArrayToInt(originPathLengthBytes)
//            Log.d(TAG, "decode: originPathLength = $originPathLength")
//            //原始路径
//            val originPathBytes = ByteArray(originPathLength)
//            offset = cutBytes(headBytes, offset, originPathBytes, 0, originPathLength)
//            val originPath = String(originPathBytes)
//            Log.d(TAG, "decode: originPath = $originPath")
//            //加密路径的长度
//            val encodePathLengthBytes = ByteArray(4)
//            offset = cutBytes(headBytes, offset, encodePathLengthBytes, 0, 4)
//            val encodePathLength = BytesUtils.byteArrayToInt(encodePathLengthBytes)
//            Log.d(TAG, "decode: encodePathLength = $encodePathLength")
//            //加密路径
//            val encodePathBytes = ByteArray(encodePathLength)
//            offset = cutBytes(headBytes, offset, encodePathBytes, 0, encodePathLength)
//            val encodePath = String(encodePathBytes)
//            Log.d(TAG, "decode: encodePath = $encodePath")
//            val keyBytes = ByteArray(1)
//            offset = cutBytes(headBytes, offset, keyBytes, 0, 1)
//            val key = BytesUtils.byteToInt(keyBytes[0])
//            Log.d(TAG, "decode: key = $key")
//
//            //如果要跳过1个字节数，传的是1
//            bufferedInputStream = BufferedInputStream(FileInputStream(File(privatePath)))
//            bufferedInputStream.skip(originOffset.toLong())
//            byteArrayOutputStream = ByteArrayOutputStream()
//            var len = 0
//            while ((len = bufferedInputStream.read(b)) > 0) {
//                for (i in 0 until len) {
//                    b[i] = (b[i] xor key.toByte()).toByte()
//                    //                    byteArrayOutputStream.write(b[i]);
//                }
//                byteArrayOutputStream.write(b, 0, len)
//            }
//            fileOutputStream = FileOutputStream(File(newPath))
//            fileOutputStream.write(byteArrayOutputStream.toByteArray())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            try {
//                bufferedInputStream?.close()
//
//                byteArrayOutputStream?.close()
//
//                fileOutputStream?.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    /***
//     * @param      src      the source array.
//     * @param      offset   starting position in the source array.
//     * @param      dest     the destination array.
//     * @param      destPos  starting position in the destination data.
//     * @param      length   the number of array elements to be copied.
//     * @return
//     */
//    private fun cutBytes(src: ByteArray, offset: Int, dest: ByteArray, destPos: Int, length: Int): Int {
//        System.arraycopy(src, offset, dest, destPos, length)
//        return offset + length
//    }
//
//    companion object {
//        private val TAG = "TestActivityyy"
//
//        private val KEY = 7
//
//        //头部
//        //    字段	字节数
//        //    魔法值	4
//        //    原始数据偏移量	4
//        //    原始数据长度	8
//        //    时间戳	8
//        //    原始路径	n
//        //    加密路径	n
//        //    原始路径的长度	4
//        //    加密路径的长度	4
//        //    秘钥	1
//
//        private val MAGIC_VALUE = "-PP-"
//    }
//
//}
