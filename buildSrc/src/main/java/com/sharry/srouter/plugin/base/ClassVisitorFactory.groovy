package com.sharry.srouter.plugin.base

import org.objectweb.asm.ClassVisitor

interface ClassVisitorFactory {

    ClassVisitor create(int api, ClassVisitor cv)

}