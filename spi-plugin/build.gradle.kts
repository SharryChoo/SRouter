// 编译时期脚本的插件, 输出一个 gradle 插件的 aar 包
plugins {
    `kotlin-dsl`
}
apply(from = "../install.gradle")

/**
 * 编译脚本
 */
buildscript {
    /**
     * 编译时期脚本代码的仓库
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
 * 工程代码依赖的仓库
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