package com.sharry.srouter.plugin.util

import groovy.io.FileType
import java.io.File
import java.io.FileNotFoundException


/**
 * 将 '\\' 替换成 '/'
 */
fun String.toLeftSlash(): String {
    val leftSlash = File.separator == "/"
    return if (leftSlash) {
        return this
    } else {
        replace("\\\\", "/")
    }
}

/**
 * 检查目录是否存在
 */
@Throws(FileNotFoundException::class, java.lang.IllegalArgumentException::class)
fun File.checkDir() {
    if (!exists()) {
        throw FileNotFoundException(absolutePath)
    } else if (!isDirectory) {
        throw java.lang.IllegalArgumentException("The provided File object is not a directory: $absolutePath")
    }
}

/**
 * 变量当前文件中所有的子文件
 */
@Throws(FileNotFoundException::class, IllegalArgumentException::class)
fun File.eachFileRecurse(fileType: FileType, closure: (file: File) -> Unit) {
    checkDir()
    val files = this.listFiles()
    if (files != null) {
        val var5 = files.size
        for (var6 in 0 until var5) {
            val file = files[var6]
            if (file.isDirectory) {
                if (fileType != FileType.FILES) {
                    closure(file)
                }
                file.eachFileRecurse(fileType, closure)
            } else if (fileType != FileType.DIRECTORIES) {
                closure(file)
            }
        }
    }
}
