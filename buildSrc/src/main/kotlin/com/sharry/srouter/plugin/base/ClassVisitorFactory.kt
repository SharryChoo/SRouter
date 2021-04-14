package com.sharry.srouter.plugin.base

import org.objectweb.asm.ClassVisitor

internal interface ClassVisitorFactory {

    fun create(api: Int, cv: ClassVisitor): ClassVisitor

}