package com.sharry.srouter.plugin.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

/**
 * 用来处理文件扫描的 Base Transform
 *
 * @author Sharry
 * @since 17/3/21 11:48
 */
abstract class BaseFileScanTransform extends Transform {

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
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        Logger.i('Start scan register info in jar file.')
        long startTime = System.currentTimeMillis()
        boolean leftSlash = File.separator == '/'
        inputs.each { TransformInput input ->
            // 1. 从 jar 包中找寻目标文件
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name
                // rename jar files
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                // input file
                File src = jarInput.file
                // output file
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                // 1.1 扫描 jar 包, 找寻目标文件
                if (shouldProcessPreDexJar(src.absolutePath)) {
                    scanJar(src, dest)
                }
                FileUtils.copyFile(src, dest)
            }
            // 2. 从 Class 文件中找寻目标文件
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                String root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator))
                    root += File.separator
                directoryInput.file.eachFileRecurse { File file ->
                    def path = file.absolutePath.replace(root, '')
                    if (!leftSlash) {
                        path = path.replaceAll("\\\\", "/")
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
        Logger.i('Scan finish, current cost time ' + (System.currentTimeMillis() - startTime) + "ms")
    }

    abstract void scanJar(File jarFile, File destFile)

    abstract void scanClass(File classFile, String path)

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

}