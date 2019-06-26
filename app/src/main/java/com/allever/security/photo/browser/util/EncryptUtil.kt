package com.allever.security.photo.browser.util

import java.io.File
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 用于加解密图片并保存
 */
object EncryptUtil {
    val SIZE = 3

    val BACKUP_FILE_TYPE = ".pebackup"
    val DYNAMIC_START = "VIDEO_"


    /**
     * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
     *
     * @param path
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun decrypt(path: String): EntryptFileInputStream {
        var efis: EntryptFileInputStream? = null
        val f = File(path)
        if (f.exists()) {
            efis = EntryptFileInputStream(f)
            return efis
        }
        throw FileNotFoundException("not found path:$path")
    }

    /**
     * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
     *
     * @param f
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun decrypt(f: File): EntryptFileInputStream {
        var efis: EntryptFileInputStream? = null
        if (f.exists()) {
            efis = EntryptFileInputStream(f)
            return efis
        }
        throw FileNotFoundException("not found file:" + f.path)
    }

    /**
     * 解密出图片根据加密后文件的路径获取它的EntryptFileInputStream
     *
     * @param fd
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun decrypt(fd: FileDescriptor?): EntryptFileInputStream {
        var efis: EntryptFileInputStream? = null
        if (fd != null) {
            efis = EntryptFileInputStream(fd)
            return efis
        }
        throw FileNotFoundException("error FileDescriptor")
    }
}
