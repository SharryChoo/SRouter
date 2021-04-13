package com.sharry.srouter.plugin.base

import org.apache.commons.io.IOUtils
import org.objectweb.asm.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.sharry.srouter.plugin.util.Logger

/**
 * generate register code into LogisticsCenter.class
 * @author billy.qi email: qiyilike@163.com
 */
class CodeGenerator {

    static void insertInitCodeTo(
            File fileContainsInitClass,
            String targetClassName,
            ClassVisitorFactory factory
    ) {
        File file = fileContainsInitClass
        if (file.getName().endsWith('.jar')) {
            insertInitCodeIntoJarFile(file, targetClassName, factory)
        }
    }

    /**
     * generate code into jar file
     * @param jarFile the jar file which contains LogisticsCenter.class
     * @return
     */
    private static File insertInitCodeIntoJarFile(File jarFile, String targetClassName, ClassVisitorFactory factory) {
        if (jarFile) {
            def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
            if (optJar.exists()) {
                optJar.delete()
            }
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)
                if (targetClassName == entryName) {
                    Logger.i('Insert init code to class >> ' + entryName)
                    def bytes = referHackWhenInit(inputStream, factory)
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
        return jarFile
    }

    //refer hack class when object init
    private static byte[] referHackWhenInit(InputStream inputStream, ClassVisitorFactory factory) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = factory.create(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}