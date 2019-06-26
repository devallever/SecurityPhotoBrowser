package com.allever.security.photo.browser.test

//package com.allever.security.photo.browser.function.endecode
//
//import android.os.Environment
//import android.util.Log
//import com.allever.lib.common.app.App
//import com.allever.security.photo.browser.util.BytesUtils
//import java.io.*
//import java.lang.Exception
//import java.util.*
//
///***
// *      * //    魔法值	        4
// *      * //    原始数据偏移量	    4
// *      * //    原始数据长度	    8
// *      * //    时间戳	        8
// *      * //    原始路径的长度	    4
// *      * //    原始路径       	n
// *      * //    加密路径的长度	    4
// *      * //    加密路径	        n
// *      * //    秘钥	            1
// */
//class HeaderBean {
//    companion object {
//        val TAG = HeaderBean::class.java.name
//
//        //魔法值4个字节（-AL-）
//        val MAGIC_VALUE = "-AL-"
//
//    }
//
//    val ENCODE_PATH_DIR = Environment.getExternalStorageDirectory().absolutePath + File.separator + "" + App.context.getPackageName() + "/Image/Cache/Decode"
//
//    private val KEY_VALUE = Random().nextInt(8) + 1
//
//    //魔法值-4字节
//    var magicValue = ""
//        private set
//    //原始数据偏移量-4字节
//    var originOffset = 0
//        private set
//    //原始数据长度-8字节
//    var originFileLength = 0L
//        private set
//    //时间戳-8字节
//    var timeMillis = 0L
//    //原始路径长度-4字节
//    var originPathLength = 0
//        private set
//    //原始路径-n字节
//    var originPath = ""
//    //加密路径长度-4字节
//    var encodePathLength = 0
//        private set
//    //加密路径-n字节
//    var encodePath = ""
//    //秘钥-1字节
//    var key = 0
//
//    constructor() {}
//
//    constructor(originPath: String) {
//        this.originPath = originPath
//        initData()
//    }
//
//    private fun initData() {
//        val originFile = File(originPath)
//        magicValue = MAGIC_VALUE
//        originFileLength = originFile.length()
//        timeMillis = System.currentTimeMillis()
//        originPathLength = originPath.length
//        originPath = originPath
//        val encodeDirFile = File(ENCODE_PATH_DIR)
//        if (!encodeDirFile.exists()) {
//            encodeDirFile.mkdirs()
//        }
//        encodePath = "$ENCODE_PATH_DIR${File.separator}${originFile.name}"
//        val encodeFile = File(encodePath)
//        if (!encodeFile.exists()) {
//            encodeFile.createNewFile()
//        }
//        encodePathLength = encodePath.length
//        key = KEY_VALUE
//        //还差原始文件数据偏移量
//    }
//
//    /***
//     * 构建数据头
//     */
//    fun buildHeader(): ByteArray {
//        //魔法值
//        val magicBytes = magicValue.toByteArray()
//        Log.d(TAG, "buildHeader: magicBytes.size = " + magicBytes.size)
//        //原始路径
//        val originPathBytes = originPath.toByteArray()
//        Log.d(TAG, "buildHeader: originPathBytes.size = " + originPathBytes.size)
//        //加密路径
//        val encodePathBytes = encodePath.toByteArray()
//        Log.d(TAG, "buildHeader: encodePathBytes.size = " + encodePathBytes.size)
//        //原始路径长度
//        val originPathLength = originPathLength
//        val originPathLengthBytes = BytesUtils.intToByteArray(originPathLength)
//        Log.d(TAG, "buildHeader: originPathLengthBytes.size = " + originPathLengthBytes.size)
//        //加密路径长度
//        val encodePathLength = encodePathLength
//        val encodePathLengthBytes = BytesUtils.intToByteArray(encodePathLength)
//        Log.d(TAG, "buildHeader: encodePathLengthBytes.size = " + encodePathLengthBytes.size)
//        //时间戳
//        val time = timeMillis
//        val timeBytes = BytesUtils.longToBytes(time)
//        Log.d(TAG, "buildHeader: timeBytes.size = " + timeBytes.size)
//        //原始数据长度
//        val originFileLength = originFileLength
//        val originFileLengthBytes = BytesUtils.longToBytes(originFileLength)
//        Log.d(TAG, "buildHeader: originFileLengthBytes.size = " + originFileLengthBytes.size)
//        //key
//        val keyBytes = ByteArray(1)
//        keyBytes[0] = BytesUtils.intToByte(key)
//        Log.d(TAG, "buildHeader: keyBytes.size = " + keyBytes.size)
//
//        val originOffset = magicBytes.size +
//                originPathBytes.size +
//                encodePathBytes.size +
//                originPathLengthBytes.size +
//                encodePathLengthBytes.size +
//                timeBytes.size +
//                originFileLengthBytes.size +
//                keyBytes.size +
//                4//本身
//        this.originOffset = originOffset
//        val originOffsetBytes = BytesUtils.intToByteArray(originOffset)
//        Log.d(TAG, "encode: originOffsetBytes.size = " + originOffsetBytes.size)
//
//
//        val byteArrayOutputStream = ByteArrayOutputStream()
//
//        writeBytes(byteArrayOutputStream, magicBytes)
//        writeBytes(byteArrayOutputStream, originOffsetBytes)
//        writeBytes(byteArrayOutputStream, originFileLengthBytes)
//        writeBytes(byteArrayOutputStream, timeBytes)
//        writeBytes(byteArrayOutputStream, originPathLengthBytes)
//        writeBytes(byteArrayOutputStream, originPathBytes)
//        writeBytes(byteArrayOutputStream, encodePathLengthBytes)
//        writeBytes(byteArrayOutputStream, encodePathBytes)
//        writeBytes(byteArrayOutputStream, keyBytes)
//
//        val headBytes = byteArrayOutputStream.toByteArray()
//        byteArrayOutputStream.close()
//        return headBytes
//
//
//    }
//
//    /***
//     * @param path 加密文件的路径
//     */
//    fun resolveHeader(path: String): Boolean {
//        var bufferedInputStream: BufferedInputStream? = null
//        var headBaos: ByteArrayOutputStream? = null
//        try {
//            val b = ByteArray(1024)
//            bufferedInputStream = BufferedInputStream(FileInputStream(File(path)))
//
//            val headBytesBuffer = ByteArray(1024)
//            val headLength = bufferedInputStream.read(headBytesBuffer)
//            headBaos = ByteArrayOutputStream()
//            headBaos.write(headBytesBuffer, 0, headLength)
//            val headBytes = headBaos.toByteArray()
//            return resolveHeader(headBytes)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        } finally {
//            headBaos?.close()
//            bufferedInputStream?.close()
//        }
//    }
//
//    /***
//     * 解析数据头
//     */
//    fun resolveHeader(headBytes: ByteArray): Boolean {
//        //此时所有字段都已知道
//        try {
//            var offset = 0
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
//            val timeMillis = BytesUtils.bytesToLong(timeBytes)
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
//            this.magicValue = magic
//            this.originOffset = originOffset
//            this.originFileLength = originFileLength
//            this.timeMillis = timeMillis
//            this.originPathLength = originPathLength
//            this.originPath = originPath
//            this.encodePathLength = encodePathLength
//            this.encodePath = encodePath
//            this.key = key
//
//            return true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//    }
//
//    private fun writeBytes(baos: ByteArrayOutputStream, bytes: ByteArray) {
//        baos.write(bytes, 0, bytes.size)
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
//}