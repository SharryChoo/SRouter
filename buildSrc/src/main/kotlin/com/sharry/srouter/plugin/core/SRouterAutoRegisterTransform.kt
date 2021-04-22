package com.sharry.srouter.plugin.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sharry.srouter.plugin.base.BaseFileScanTransform
import com.sharry.srouter.plugin.base.CodeGenerator
import com.sharry.srouter.plugin.util.Logger
import com.sharry.srouter.plugin.util.ScanSetting
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Opcodes
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * transform api
 *
 *
 * 1. Scan all classes to find which classes implement the specified interface
 * 2. Generate register code into class file: [ScanSetting.GENERATE_TO_CLASS_FILE_NAME]
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/21 11:48
 */
internal class SRouterAutoRegisterTransform(
        val project: Project,
        val registerList: ArrayList<ScanSetting>
) : BaseFileScanTransform() {

    private var mIsJarFile: Boolean? = null
    private var mGenerateToClass: File? = null

    /**
     * name of this transform
     * @return
     */
    override fun getName(): String {
        return ScanSetting.PLUGIN_NAME
    }

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

    override fun onScanJarEntry(file: File, jarFile: JarFile, jarEntry: JarEntry) {
        val classFilePath = jarEntry.name
        when {
            // 1. 如果是 com/sharry/srouter/generate 这个文件, 则找寻其内部生成的路由文件
            classFilePath.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME) -> {
                val inputStream: InputStream = jarFile.getInputStream(jarEntry)
                scanClassInputStream(inputStream)
                inputStream.close()
                Logger.print("find target entryName: $classFilePath")
            }
            // 2. 如果是 com/sharry/srouter/support/SRouterImpl.class 这个文件, 则记录一下说明是需要插入代码的目标文件
            ScanSetting.GENERATE_TO_CLASS_FILE_NAME == classFilePath -> {
                mGenerateToClass = file
                mIsJarFile = true
                Logger.print("find contains generate jar file: " + file.absolutePath)
            }
        }
    }

    override fun onScanClass(classFile: File, classFilePath: String) {
        when {
            // 1. 如果是 com/sharry/srouter/generate 这个文件, 则找寻其内部生成的路由文件
            classFilePath.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME) -> {
                val inputStream = FileInputStream(classFile)
                scanClassInputStream(inputStream)
                inputStream.close()
                Logger.print("find target entryName: $classFilePath")
            }
            // 2. 如果是 com/sharry/srouter/support/SRouterImpl.class 这个文件, 则记录一下说明是需要插入代码的目标文件
            ScanSetting.GENERATE_TO_CLASS_FILE_NAME == classFilePath -> {
                mGenerateToClass = classFile
                mIsJarFile = false
                Logger.print("find generate class file: " + classFile.absolutePath)
            }
        }
    }

    override fun onProcess() {
        val fileContainsClass = mGenerateToClass ?: return
        val isJarFile = mIsJarFile ?: return
        registerList.forEach { ext ->
            Logger.print("Insert register code to file " + fileContainsClass.absolutePath)
            if (ext.classList.isEmpty()) {
                Logger.print("No class implements found for interface:" + ext.interfaceName)
            } else {
                ext.classList.forEach { Logger.print(it) }
                if (isJarFile) {
                    CodeGenerator.insertCodeToJar(
                            fileContainsClass,
                            ScanSetting.GENERATE_TO_CLASS_FILE_NAME,
                            RouterImplClassVisitor.Factory(ext)
                    )
                } else {
                    CodeGenerator.insertCodeToClass(
                            fileContainsClass,
                            RouterImplClassVisitor.Factory(ext)
                    )
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Utils method.
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun scanClassInputStream(inputStream: InputStream) {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    private inner class ScanClassVisitor internal constructor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {

        override fun visit(version: Int,
                           access: Int,
                           name: String,
                           signature: String?,
                           superName: String?,
                           interfaces: Array<String>
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            // 将符合条件的 class 文件名保存到 ScanSetting 的 classList 中, 方便后续在代码中添加 register(xxx).
            registerList.forEach { ext ->
                interfaces.forEach { interfaceName ->
                    if (interfaceName == ext.interfaceName) {
                        // fix repeated inject init code when Multi-channel packaging
                        if (!ext.classList.contains(name)) {
                            ext.classList.add(name)
                        }
                    }
                }
            }
        }
    }

}