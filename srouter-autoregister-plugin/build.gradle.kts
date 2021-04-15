plugins {
    `kotlin-dsl`
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

apply(from = "../install.gradle")