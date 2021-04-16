package com.sharry.srouter.plugin.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sharry.srouter.plugin.util.Logger
import com.sharry.srouter.plugin.util.eachFileRecurse
import com.sharry.srouter.plugin.util.toLeftSlash
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 用来处理文件扫描的 Base Transform
 *
 * @author Sharry
 * @since 17/3/21 11:48
 */
abstract class BaseFileScanTransform : Transform() {

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(context: Context?,
                           inputs: MutableCollection<TransformInput>?,
                           referencedInputs: MutableCollection<TransformInput>?,
                           outputProvider: TransformOutputProvider?,
                           isIncremental: Boolean) {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        Logger.print("|---------------------Transform start---------------------|")
        // 1. 扫描文件
        Logger.print("|---------- Start scan file ----------|")
        var startTime = System.currentTimeMillis()
        inputs?.forEach { input ->
            // 1.1 从 jar 包中找寻目标文件
            input.jarInputs.forEach { jarInput ->
                var destName = jarInput.name
                // rename jar files
                val hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length - 4)
                }
                outputProvider?.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)?.let { destJarFile ->
                    // 拷贝到目标文件 destJarFile
                    FileUtils.copyFile(jarInput.file, destJarFile)
                    // 扫描 destJar File 中的 Entry
                    if (canScanJarFile(destJarFile.absolutePath)) {
                        val jarFile = JarFile(destJarFile)
                        val enumeration: Enumeration<*> = jarFile.entries()
                        while (enumeration.hasMoreElements()) {
                            val jarEntry = enumeration.nextElement() as JarEntry
                            onScanJarEntry(destJarFile, jarFile, jarEntry)
                        }
                        jarFile.close()
                    }
                }
            }
            // 1.2 从 Class 文件中找寻目标文件
            input.directoryInputs.forEach { directoryInput ->
                outputProvider?.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)?.let { destFile ->
                    // 拷贝到目标文件 destFile
                    FileUtils.copyDirectory(directoryInput.file, destFile)
                    var root = destFile.absolutePath
                    if (!root.endsWith(File.separator)) {
                        root += File.separator
                    }
                    // 遍历子文件
                    destFile.eachFileRecurse(FileType.ANY) { file ->
                        val filePath = file.absolutePath.replace(root, "").toLeftSlash()
                        if (file.isFile && canScanClassFile(filePath)) {
                            onScanClass(file, filePath)
                        }
                    }
                }
            }
        }
        Logger.print("|---------- Scan finish, current cost time " + (System.currentTimeMillis() - startTime) + "ms ----------|")
        // 2. 处理扫描的文件
        startTime = System.currentTimeMillis()
        Logger.print("|---------- Start process ----------|")
        onProcess()
        Logger.print("|---------- Process finish, current cost time " + (System.currentTimeMillis() - startTime) + "ms ----------|")
    }

    /**
     * 扫描 Jar 包中的 JarEntry
     */
    abstract fun onScanJarEntry(file: File, jarFile: JarFile, jarEntry: JarEntry)

    /**
     * 扫描 .class 文件
     */
    abstract fun onScanClass(classFile: File, classFilePath: String)

    abstract fun onProcess()

    private fun canScanJarFile(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    private fun canScanClassFile(filePath: String): Boolean {
        return filePath.endsWith(".class") && !filePath.startsWith("R\$") && "R.class" != filePath && "BuildConfig.class" != filePath
    }

}