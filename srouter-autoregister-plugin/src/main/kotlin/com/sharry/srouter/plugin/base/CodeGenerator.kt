package com.sharry.srouter.plugin.base

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.sharry.srouter.plugin.util.Logger

/**
 * Generate code into target file.
 *
 * @author zhuxiaoyu <a href="zhuxiaoyu.sharry@bytedance.com">Contact me.</a>
 * @version 1.0
 * @since 4/16/21
 */
object CodeGenerator {


    /**
     * generate code into jar file
     *
     * @param jarFile the jar file which contains targetEntryName
     */
    fun insertCodeToClass(classFile: File, factory: ClassVisitorFactory) {
        if (!classFile.name.endsWith(".class")) {
            return
        }
        val inputStream = FileInputStream(classFile)
        FileUtils.writeByteArrayToFile(
                classFile,
                referHackWhenInit(inputStream, factory)
        )
        inputStream.close()
    }

    /**
     * generate code into jar file
     *
     * @param jarFile the jar file which contains targetEntryName
     */
    fun insertCodeToJar(jarFile: File, targetEntryName: String, factory: ClassVisitorFactory) {
        if (!jarFile.name.endsWith(".jar")) {
            return
        }
        // 1. 创建新的 JarFile 文件
        val optJar = File(jarFile.parent, jarFile.name.toString() + ".opt")
        if (optJar.exists()) {
            optJar.delete()
        }
        // 2. 将原先的 JarFile 写入 optJar 中
        JarFile(jarFile).let { originJarFile ->
            val enumeration: Enumeration<*> = originJarFile.entries()
            val optJarFileOutputStream = JarOutputStream(FileOutputStream(optJar))
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryInputStream: InputStream = originJarFile.getInputStream(jarEntry)
                optJarFileOutputStream.apply {
                    // 2.1 为 optJar 文件添加一个 entry
                    val entryName = jarEntry.name
                    putNextEntry(ZipEntry(entryName))
                    // 2.2 写入 entry 的数据
                    write(
                            if (targetEntryName == entryName) {
                                Logger.print("write to $targetEntryName")
                                referHackWhenInit(entryInputStream, factory)
                            } else {
                                IOUtils.toByteArray(entryInputStream)
                            }
                    )
                    // 2.3 标记写 entry 终止
                    closeEntry()
                }
                entryInputStream.close()
            }
            optJarFileOutputStream.close()
            originJarFile.close()
        }
        // 3. 用 opt 的 Jar file 替换原始的 Jar file
        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
    }

    //refer hack class when object init
    private fun referHackWhenInit(inputStream: InputStream, factory: ClassVisitorFactory): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = factory.create(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}