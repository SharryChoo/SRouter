package com.sharry.srouter.plugin.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sharry.srouter.plugin.util.Logger
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import java.io.File

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

    /**
     * The com.sharry.srouter.plugin will scan all classes in the project
     * @return
     */
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
        Logger.i("Start scan register info in jar file.")
        val startTime = System.currentTimeMillis()
        val leftSlash = File.separator == "/"
        inputs?.forEach { input ->
            // 1. 从 jar 包中找寻目标文件
            input.jarInputs.forEach { jarInput ->
                var destName = jarInput.name
                // rename jar files
                val hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length - 4)
                }
                // input file
                val src = jarInput.file
                // output file
                val dest = outputProvider?.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR) ?: return@forEach
                // 1.1 扫描 jar 包, 找寻目标文件
                if (shouldProcessPreDexJar(src.absolutePath)) {
                    scanJar(src, dest)
                }
                FileUtils.copyFile(src, dest)
            }
            // 2. 从 Class 文件中找寻目标文件
            input.directoryInputs.forEach { directoryInput ->
                val dest = outputProvider?.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                var root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator)) {
                    root += File.separator
                }
                directoryInput.file.also { file ->
                    var path = file.absolutePath.replace(root, "")
                    if (!leftSlash) {
                        path = path.replace("\\\\", "/")
                    }
                    // 2.1 扫描 class 文件, 找寻目标文件
                    if (file.isFile()) {
                        scanClass(file, path)
                    }
                }
                // copy to dest
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        Logger.i("Scan finish, current cost time " + (System.currentTimeMillis() - startTime) + "ms")
    }

    abstract fun scanJar(jarFile: File, destFile: File)

    abstract fun scanClass(classFile: File, path: String)

    fun shouldProcessPreDexJar(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

}