package com.allever.security.photo.browser.util

import java.io.File

object FileUtils {

    fun checkExist(filePath: String): Boolean {
        return File(filePath).exists()
    }
}