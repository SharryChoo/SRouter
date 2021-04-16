plugins {
    `kotlin-dsl`
}
apply(from = "../install.gradle")

/**
 * 编译脚本
 */
buildscript {
    /**
     * 编译脚本代码仓库
     */
    repositories {
        maven { setUrl("https://jitpack.io") }
    }

    /**
     * 编译脚本代码依赖
     */
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }

}

/**
 * 工程的代码仓库
 */
repositories {
    jcenter()
    google()
}

/**
 * 工程代码依赖
 */
dependencies {
    implementation("com.android.tools.build:gradle:3.2.1")
}

sourceSets {
    // 链接到子 module 的编译目录中
    add(getAt("../srouter-autoregister-plugin/src"))
}