package com.sharry.srouter.plugin.util

/**
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 4/14/21
 */
object Logger {

    fun i(info: String) {
        println("SRouter::AutoRegister >>> $info")
    }

    fun e(error: String) {
        println("SRouter::AutoRegister >>> $error")
    }

    fun w(warning: String) {
        println("SRouter::AutoRegister >>> $warning")
    }

}