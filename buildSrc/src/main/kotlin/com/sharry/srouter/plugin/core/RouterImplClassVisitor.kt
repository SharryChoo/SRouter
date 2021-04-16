package com.sharry.srouter.plugin.core

import com.sharry.srouter.plugin.base.ClassVisitorFactory
import com.sharry.srouter.plugin.util.ScanSetting
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import com.sharry.srouter.plugin.util.Logger


class RouterImplClassVisitor(api: Int, cv: ClassVisitor?, val extension: ScanSetting) : ClassVisitor(api, cv) {

    override fun visitMethod(access: Int,
                             name: String?,
                             desc: String?,
                             signature: String?,
                             exceptions: Array<String>?): MethodVisitor {
        var mv = super.visitMethod(access, name, desc, signature, exceptions)
        // generate code into this method
        if (ScanSetting.GENERATE_TO_METHOD_NAME == name) {
            mv = RouteMethodVisitor(Opcodes.ASM5, mv)
        }
        return mv
    }

    internal inner class RouteMethodVisitor(api: Int, mv: MethodVisitor?) : MethodVisitor(api, mv) {

        override fun visitInsn(opcode: Int) {
            //generate code before return
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                extension.classList.forEach { name ->
                    // 类名
                    val className = name.replace("/", ".")
                    mv.visitLdcInsn(className)
                    // generate invoke register method into SRouterImpl.loadRouterMap()
                    mv.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            ScanSetting.GENERATE_TO_CLASS_NAME,
                            ScanSetting.REGISTER_METHOD_NAME,
                            "(Ljava/lang/String;)V",
                            false
                    )
                    Logger.print("visitIns $className")
                }
            }
            super.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }

    }

    class Factory(private val extension: ScanSetting) : ClassVisitorFactory {

        override fun create(api: Int, cv: ClassVisitor): ClassVisitor {
            return RouterImplClassVisitor(api, cv, extension)
        }

    }

}