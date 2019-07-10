package com.allever.security.photo.browser.function.endecode

import android.net.Uri
import android.os.Environment
import android.os.Handler
import com.android.absbase.App
import android.util.Log
import com.allever.security.photo.browser.BuildConfig
import com.allever.security.photo.browser.bean.LocalThumbnailBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.util.FolderHelper
import com.allever.security.photo.browser.util.MD5
import java.io.*
import java.util.concurrent.*
import kotlin.experimental.xor

object PrivateHelper {

    val Tag = PrivateHelper::class.java.simpleName
    //加密文件存储路径
    val PATH_ENCODE_ORIGINAL =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "." + App.getPackageName() + "/Image/Cache/Decode"
    //相册目录
    val PATH_ALBUM =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "." + App.getPackageName() + "/Image/Cache/Album"
    //解密临时文件路径
    val PATH_DECODE_TEMP = App.getContext().externalCacheDir.absolutePath + "/.temp/"
    //创建 .nomedia 文件
    val PATH_DECODE_TEMP_NOMEDIA = App.getContext().externalCacheDir.absolutePath + "/.temp/.nomedia"

    /**
     * 对字节加解密
     */
    fun deBytes(bytes: ByteArray, key: Int): ByteArray {
        //异或操作
        bytes.mapIndexed { index, byte ->
            bytes[index] = byte.xor(key.toByte())
        }
        return bytes
    }

    val mHandler: Handler = Handler(App.getContext().mainLooper)
    val threadPool = ThreadPoolExecutor(
        3, 10, 30L,
        TimeUnit.SECONDS, LinkedBlockingDeque(), ExecutorThreadFactory(), ThreadPoolExecutor.DiscardPolicy()
    );

    class ExecutorThreadFactory : ThreadFactory {
        override fun newThread(r: Runnable?): Thread {
            return Thread(r);
        }
    }

    fun close() {
        if (!threadPool.isShutdown) {
            threadPool.shutdown()
        }
    }

    /**
     * 加密多个文件
     */
    fun encodeList(heads: MutableList<PrivateBean>, listener: EncodeListListener? = null) {
        val errorList = arrayListOf<PrivateBean>()
        val successList = arrayListOf<PrivateBean>()
        var fixSize = 0 //已经处理的数量
        val runnable = Runnable {
            run {
                //start
                mHandler?.post { listener?.onStart() }
                if (heads.isEmpty()) {
                    mHandler?.post { listener?.onFailed(successList, errorList) }
                }
                heads.map { value ->
                    encode(value, object : EncodeListener {
                        override fun onEncodeStart() {}
                        override fun onEncodeSuccess(head: PrivateBean) {
                            successList.add(head);
                            //最后一个
                            if (fixSize == heads.size - 1) {
                                if (errorList.size == 0) {
                                    //success
                                    mHandler?.post {
                                        listener?.onSuccess(successList, errorList)
                                    }
                                } else {
                                    mHandler?.post {
                                        listener?.onFailed(successList, errorList)
                                    }
                                }
                            }
                            fixSize++
                        }

                        override fun onEncodeFailed(msg: String, head: PrivateBean) {
                            errorList.add(head)
                            if (fixSize == heads.size - 1) {
                                if (errorList.size != 0) {
                                    //failed
                                    mHandler?.post {
                                        listener?.onFailed(successList, errorList)
                                    }
                                }
                            }
                            fixSize++
                        }
                    })
                }
            }
        }
        threadPool.execute(runnable)
    }

    /**
     * 加密
     *
     * 成功后删除源文件
     */
    fun encode(head: PrivateBean, listener: EncodeListener? = null) {
        val runnable = Runnable {
            run {
                mHandler?.post {
                    listener?.onEncodeStart()
                }
                try {
                    if (!File(head.originalPath).exists()) {
                        mHandler?.post {
                            listener?.onEncodeFailed("file is not found : path $head.originalPath", head)
                        }
                        return@Runnable
                    }
                    val originalMD5 = MD5.getMD5Str(head.originalPath)
                    val encodePath = File(PATH_ENCODE_ORIGINAL, originalMD5).path
                    val tempPath = File(PATH_DECODE_TEMP, originalMD5).path
                    head.encodePath = encodePath
                    head.tempPath = tempPath
                    createFile(encodePath)
                    val currentTimeMillis = System.currentTimeMillis()
                    val fileInputStream = FileInputStream(head.originalPath)
                    val fileOutStream = FileOutputStream(encodePath)
                    //写入数据头、加密数据体
                    writeEncodeData(head, fileInputStream, fileOutStream)
                    fileOutStream.close()
                    fileInputStream.close()
                    log(Thread.currentThread().name)
                    log("PrivateUtils encode time ：" + (System.currentTimeMillis() - currentTimeMillis))

                    /// 指定目录中写入新的链接文件
                    val alarmFilePath = File(head.albumPath, originalMD5)
                    alarmFilePath.createNewFile()

                    mHandler?.post {
                        listener?.onEncodeSuccess(head)
                    }
                    // 成功后删除源文件,并且通知系统删除相册资源
                    val result = removeFile(head.originalPath)
                    if (result) {
                        FolderHelper.broadcastVideoFile(App.getContext(), File(head.originalPath), null, null)
                    } else {
                    }
                } catch (e: Exception) {
                    mHandler?.post {
                        e.message?.let { listener?.onEncodeFailed(it, head) }
                    }
                }
            }
        }
        threadPool.execute(runnable)
    }

    /**
     * 解密
     */
    fun decode(path: String, listener: DecodeListener? = null) {
        val runnable = Runnable {
            run {
                mHandler?.post {
                    listener?.onDecodeStart()
                }
                val deImageBean = PrivateBean()
                try {
                    if (!File(path).exists()) {
                        mHandler?.post {
                            listener?.onDecodeFailed("file is not found : path $path")
                        }
                        return@Runnable
                    }
                    val currentTimeMillis = System.currentTimeMillis()
                    //解析头
                    if (deImageBean.resolveHead(path)) {
                        //对原始文件名加密
                        val filePath = deImageBean.tempPath
                        createFile(filePath)
                        val fileInputStream = FileInputStream(path)
                        val fileOutStream = FileOutputStream(filePath)
                        readEncodeData(fileInputStream, fileOutStream, deImageBean)
                        fileOutStream.close()
                        fileInputStream.close()
                        mHandler?.post {
                            listener?.onDecodeSuccess(deImageBean)
                            if (!File(PATH_DECODE_TEMP_NOMEDIA).exists()) {
                                createFile(PATH_DECODE_TEMP_NOMEDIA)
                            }
                        }
                    } else {
                        mHandler?.post {
                            listener?.onDecodeFailed("not private photo path:$path")
                        }
                    }
                    log("decode time ：" + (System.currentTimeMillis() - currentTimeMillis))
                } catch (e: Exception) {
                    mHandler?.post {
                        e.message?.let { listener?.onDecodeFailed(it) }
                    }
                }
            }
        }
        threadPool.execute(runnable)
    }

    /**
     * 解锁多个文件
     */
    fun unLockList(heads: List<PrivateBean>, listener: UnLockListListener? = null) {
        val errors = arrayListOf<PrivateBean>()
        var fixSize = 0 //已经处理的数量
        val runnable = Runnable {
            run {
                //start
                mHandler?.post { listener?.onStart() }
                if (heads.isEmpty()) {
                    mHandler?.post { listener?.onFailed(errors) }
                }
                heads.map { head ->
                    unLockAndRestore(head, object : UnLockAndRestoreListener {
                        override fun onStart() {}
                        override fun onSuccess() {
                            if (fixSize == heads.size - 1) {
                                if (errors.size == 0) {
                                    //success.
                                    mHandler?.post { listener?.onSuccess() }
                                } else {
                                    //failed
                                    mHandler?.post { listener?.onFailed(errors) }
                                }
                            }
                            fixSize++
                        }

                        override fun onFailed(msg: String) {
                            errors.add(head)
                            if (fixSize == heads.size - 1) {
                                mHandler?.post { listener?.onFailed(errors) }
                            }
                            fixSize++
                        }
                    })
                }
            }
        }
        threadPool.execute(runnable)
    }

    /**
     *
     *  解锁后A回到原路径
     *
     * 1 从临时缓存文件(已解密)获得源文件A
     * 2 A资源回到原路径
     * 3 成功后删除临时缓存文件A和加密文件A
     *
     * todo
     * 失败考虑一个特定目录
     *
     * @param head 数据头信息
     */
    fun unLockAndRestore(head: PrivateBean, listener: UnLockAndRestoreListener? = null) {
        val runnable = Runnable {
            run {
                mHandler?.post {
                    listener?.onStart()
                }
                try {
                    val currentTimeMillis = System.currentTimeMillis()
                    val tempFile = File(head.tempPath)
                    if (tempFile.exists()) {
                        val originalFile = File(head.originalPath)
                        tempFile.copyTo(originalFile, true)
                        if (originalFile.exists()) {
                            FolderHelper.broadcastVideoFile(App.getContext(), originalFile, null, null)
                            //删除临时缓存文件
                            tempFile.delete()
                            //删除加密文件
                            val encodeFile = File(head.encodePath)
                            if (encodeFile.exists()) {
                                encodeFile.delete()
                            }
                            mHandler?.post {
                                listener?.onSuccess()
                            }
                        } else {
                            mHandler?.post {
                                listener?.onFailed("copy is failed")
                            }
                        }
                    } else {
                        mHandler?.post {
                            listener?.onFailed("tempFile is not exist : path ${head.tempPath}")
                        }
                    }
                    log("delete ${System.currentTimeMillis() - currentTimeMillis}")
                } catch (e: Exception) {
                    mHandler?.post {
                        e.message?.let { listener?.onFailed(it) }
                    }
                }
            }
        }
        threadPool.execute(runnable)
    }

    /**
     *  边读边写操作
     *  @transform 加密操作
     */
    private fun writeEncodeData(head: PrivateBean, inputStream: InputStream, outputStream: OutputStream) {
        //数据头 加密
        val headData = head.buildHead()
        //写数据头
        outputStream.write(headData, 0, headData.size)
        val buffer = ByteArray(1024)
        var len = -1
        val currentTimeMillis = System.currentTimeMillis()
        val key = head.VALUE_KEY.toByte()
        while (inputStream.read(buffer).also { len = it } != -1) {
            for ((index, b) in buffer.withIndex()) {
                buffer[index] = b.xor(key)
            }
            //不使用Lambda 表达式, tranform 里面做了两次 NUMBER 强转,速度更慢,由于kotlin没有byte类型,故要强转为number类型再做toByte
//                if(transform != null){
//                    for ((index, b) in buffer.withIndex()) {
//                        buffer[index] = transform(b)
//                    }
//                }
            outputStream.write(buffer, 0, len)
        }
        log("writeEncodeData " + (System.currentTimeMillis() - currentTimeMillis))
    }

    /**
     * 解密操作
     */
    private fun readEncodeData(inputStream: InputStream, outputStream: OutputStream, head: PrivateBean) {
        val result = inputStream.skip(head.originalOffset.toLong())
        if (result < 0) {
            throw IOException("file skip error")
        }
        val buffer = ByteArray(1024)
        var len = -1
        val currentTimeMillis = System.currentTimeMillis()
        val key = head.key.toByte()
        while (inputStream.read(buffer).also { len = it } != -1) {
            for ((index, b) in buffer.withIndex()) {
                buffer[index] = b.xor(key)
            }
            outputStream.write(buffer, 0, len)
        }
        log("readEncodeData " + (System.currentTimeMillis() - currentTimeMillis))
    }

    private fun createFile(path: String) {
        val parentPath = File(path).parent
            ?: throw (RuntimeException("createFile parent is null"))
        val parent = File(parentPath)
        if (!parent.exists()) {
            parent.mkdirs()
        }
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
    }

    private fun removeFile(path: String): Boolean {
        try {
            val file = File(path)
            if (!file.exists()) {
                log("file is not found : path $path")
                return false
            }
            file.delete()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 删除临时缓存文件
     */
    fun removeTempFile() {
        val runnable = Runnable {
            run {
                val file = File(PATH_DECODE_TEMP)
                if (file.exists()) file.deleteRecursively()
            }
        }
        threadPool.execute(runnable)
    }

    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(Tag, msg)
        }
    }

    fun changeThumbnailBean2LocalThumbnailBean(bean: ThumbnailBean): LocalThumbnailBean {
        val localThumbnailBean = LocalThumbnailBean()
        localThumbnailBean.date = bean.date
        localThumbnailBean.uri = bean.uri?.toString()
        localThumbnailBean.path = bean.path
        localThumbnailBean.duration = bean.duration
        localThumbnailBean.type = bean.type
        localThumbnailBean.sourceType = bean.sourceType
        localThumbnailBean.tempPath = bean.tempPath
        localThumbnailBean.isChecked = bean.isChecked
//        localThumbnailBean.selectCount = bean.selectCount
        return localThumbnailBean
    }

    fun changeLocalThumbnailBean2ThumbnailBean(local: LocalThumbnailBean): ThumbnailBean {
        val bean = ThumbnailBean()
        bean.date = local.date
        bean.uri = Uri.parse(local.uri ?: "")
        bean.path = local.path
        bean.duration = local.duration
        bean.type = local.type
        bean.sourceType = local.sourceType
        bean.tempPath = local.tempPath
        bean.isChecked = local.isChecked
        return bean
    }

    fun changeThumbnailList2LocalThumbnaiList(arrayList: MutableList<ThumbnailBean>): MutableList<LocalThumbnailBean> {
        val list = arrayListOf<LocalThumbnailBean>()
        arrayList.mapTo(list, object : (ThumbnailBean) -> LocalThumbnailBean {
            override fun invoke(p1: ThumbnailBean): LocalThumbnailBean {
                return changeThumbnailBean2LocalThumbnailBean(p1)
            }
        })
        return list
    }

    fun changeLocalThumbnaiList2ThumbnailList(arrayList: ArrayList<LocalThumbnailBean>): ArrayList<ThumbnailBean> {
        val list = arrayListOf<ThumbnailBean>()
        arrayList.mapTo(list, object : (LocalThumbnailBean) -> ThumbnailBean {
            override fun invoke(p1: LocalThumbnailBean): ThumbnailBean {
                return changeLocalThumbnailBean2ThumbnailBean(p1)
            }
        })
        return list
    }

}


