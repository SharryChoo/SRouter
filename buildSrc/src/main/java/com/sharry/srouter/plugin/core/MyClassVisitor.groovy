package com.sharry.srouter.plugin.core

import com.sharry.srouter.plugin.base.ClassVisitorFactory
import com.sharry.srouter.plugin.util.ScanSetting
import org.objectweb.asm.*

class MyClassVisitor extends ClassVisitor {

    ScanSetting extension

    MyClassVisitor(int api, ClassVisitor cv, ScanSetting extension) {
        super(api, cv)
        this.extension = extension
    }

    void visit(int version, int access, String name, String signature,
               String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc,
                              String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        // generate code into this method
        if (name == ScanSetting.GENERATE_TO_METHOD_NAME) {
            mv = new RouteMethodVisitor(Opcodes.ASM5, mv)
        }
        return mv
    }

    class RouteMethodVisitor extends MethodVisitor {

        RouteMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv)
        }

        @Override
        void visitInsn(int opcode) {
            //generate code before return
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                extension.classList.each { name ->
                    name = name.replaceAll("/", ".")
                    mv.visitLdcInsn(name)//类名
                    // generate invoke register method into LogisticsCenter.loadRouterMap()
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC
                            , ScanSetting.GENERATE_TO_CLASS_NAME
                            , ScanSetting.REGISTER_METHOD_NAME
                            , "(Ljava/lang/String;)V"
                            , false)
                }
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }

    static class Factory implements ClassVisitorFactory {

        ScanSetting extension

        Factory(ScanSetting extension) {
            this.extension = extension
        }

        @Override
        ClassVisitor create(int api, ClassVisitor cv) {
            return MyClassVisitor(api, cv, extension)
        }
    }

}
