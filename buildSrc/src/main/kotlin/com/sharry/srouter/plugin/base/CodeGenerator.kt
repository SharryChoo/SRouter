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

/**
 * generate register code into LogisticsCenter.class
 * @author billy.qi email: qiyilike@163.com
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
        FileUtils.writeByteArrayToFile(
                classFile,
                referHackWhenInit(FileInputStream(classFile), factory)
        )
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
        val optJar = File(jarFile.parent, jarFile.name.toString() + ".opt")
        if (optJar.exists()) {
            optJar.delete()
        }
        val file = JarFile(jarFile)
        val enumeration: Enumeration<*> = file.entries()
        val jarOutputStream = JarOutputStream(FileOutputStream(optJar))
        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement() as JarEntry
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            val inputStream: InputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            if (targetEntryName == entryName) {
                val bytes = referHackWhenInit(inputStream, factory)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
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