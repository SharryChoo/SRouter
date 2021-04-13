package com.sharry.srouter.plugin.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sharry.srouter.plugin.base.BaseFileScanTransform
import com.sharry.srouter.plugin.base.CodeGenerator
import com.sharry.srouter.plugin.util.ScanSetting
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Opcodes
import org.gradle.api.Project
import com.sharry.srouter.plugin.util.Logger

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * transform api
 * <p>
 *     1. Scan all classes to find which classes implement the specified interface
 *     2. Generate register code into class file: {@link ScanSetting#GENERATE_TO_CLASS_FILE_NAME}
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/21 11:48
 */
class RegisterTransform extends BaseFileScanTransform {

    final Project project
    final ArrayList<ScanSetting> registerList
    File fileContainsGenerateToClass;

    RegisterTransform(Project project, ArrayList<ScanSetting> registerList) {
        this.project = project
        this.registerList = registerList
    }

    /**
     * name of this transform
     * @return
     */
    @Override
    String getName() {
        return ScanSetting.PLUGIN_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * The com.sharry.srouter.plugin will scan all classes in the project
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental
    ) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        if (fileContainsGenerateToClass) {
            registerList.each { ext ->
                Logger.i('Insert register code to file ' + fileContainsGenerateToClass.absolutePath)

                if (ext.classList.isEmpty()) {
                    Logger.e("No class implements found for interface:" + ext.interfaceName)
                } else {
                    ext.classList.each {
                        Logger.i(it)
                    }
                    if (ext != null && !ext.classList.isEmpty()) {
                        CodeGenerator.insertInitCodeTo(
                                ext,
                                fileContainsGenerateToClass,
                                ScanSetting.GENERATE_TO_CLASS_FILE_NAME,
                                MyClassVisitor.Factory(ext)
                        )
                    }
                }
            }
        }

        Logger.i("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
    }

    @Override
    void scanJar(File jarFile, File destFile) {
        if (jarFile) {
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                // 如果是 com/sharry/srouter/generate 这个文件, 则找寻其内部生成的路由文件
                if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                }
                // 如果是 SRouterImpl 文件的 jar 包, 则先暂存一下, 后续会使用
                else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    fileContainsGenerateToClass = destFile
                }
            }
            file.close()
        }
    }

    @Override
    void scanClass(File classFile, String path) {
        if (shouldProcessClass(path)) {
            scanClass(new FileInputStream(file))
        }
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null && entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            // 将符合条件的 class 文件名保存到 ScanSetting 的 classList 中, 方便后续在代码中添加 register(xxx).
            registerList.each { ext ->
                if (ext.interfaceName && interfaces != null) {
                    interfaces.each { itName ->
                        if (itName == ext.interfaceName) {
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

}