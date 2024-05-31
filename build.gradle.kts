/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.4/userguide/building_java_projects.html in the Gradle documentation.
 */
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import cl.franciscosolis.sonatypecentralupload.SonatypeCentralUploadTask

plugins {
    `java-library`
    `maven-publish`
    `jacoco`
    alias(libs.plugins.errorprone)
    alias(libs.plugins.spotless)
    id("cl.franciscosolis.sonatype-central-upload") version "1.0.2"
}

group = "io.github.wreulicke.errorprone.logstash"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.errorprone.check.api)
    errorprone(libs.errorprone.core)
    compileOnly(libs.auto.service)
    annotationProcessor(libs.auto.service)

    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.errorprone.check.api)
    testImplementation(libs.errorprone.test.helpers)
    testRuntimeOnly(libs.slf4j.api)
    testRuntimeOnly(libs.logstash.logback.encoder)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}


val exportsArgs = listOf(
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports",
        "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
)
val addExportsFile = file("${layout.buildDirectory.get()}/tmp/javadoc/add-exports.txt")
val createJavadocOptionFile by tasks.registering {
    outputs.file(addExportsFile)
    doLast {
        addExportsFile.printWriter().use { writer ->
            exportsArgs.chunked(2).forEach {
                writer.println("${it[0]}=${it[1]}")
            }
        }
    }
}


tasks {
    withType<JavaCompile> {
        options.compilerArgs.addAll(exportsArgs)
        options.errorprone.check("MemoizeConstantVisitorStateLookups", CheckSeverity.ERROR)
        options.errorprone.check("ASTHelpersSuggestions", CheckSeverity.ERROR)
        options.errorprone.check("BugPatternNaming", CheckSeverity.ERROR)
    }

    withType<Javadoc>() {
        dependsOn(createJavadocOptionFile)
        options.optionFiles(addExportsFile)
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs = exportsArgs + listOf("--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
        finalizedBy(named("jacocoTestReport"))
    }
}

spotless {
    java {
        googleJavaFormat()
        importOrder()
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        indentWithSpaces()
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            pom {
                name.set(project.name)
                description.set("Custom Errorprone check for logstash-logback-encoder")
                url.set("https://github.com/wreulicke/errorprone-logstash-logback-encoder")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/wreulicke/errorprone-logstash-logback-encoder/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("wreulicke")
                        name.set("wreulicke")
                        email.set("wreulicke@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:wreulicke/errorprone-logstash-logback-encoder.git")
                    developerConnection.set("scm:git:git@github.com:wreulicke/errorprone-logstash-logback-encoder.git")
                    url.set("https://github.com/wreulicke/errorprone-logstash-logback-encoder")
                }
                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/wreulicke/errorprone-logstash-logback-encoder/issues")
                }
            }
        }
    }
}

tasks.named<SonatypeCentralUploadTask>("sonatypeCentralUpload") {
    // 公開するファイルを生成するタスクに依存する。
    dependsOn("jar", "sourcesJar", "javadocJar", "generatePomFileForMavenPublication")

    // Central Portalで生成したトークンを指定する。
    username = System.getenv("SONATYPE_CENTRAL_USERNAME")
    password = System.getenv("SONATYPE_CENTRAL_PASSWORD")

    // タスク名から成果物を取得する。
    archives = files(
        tasks.named("jar"),
        tasks.named("sourcesJar"),
        tasks.named("javadocJar"),
    )
    // POMファイルをタスクの成果物から取得する。
    pom = file(
        tasks.named("generatePomFileForMavenPublication").get().outputs.files.single()
    )

    // PGPの秘密鍵を指定する。
    signingKey = System.getenv("PGP_SIGNING_KEY")
    // PGPの秘密鍵のパスフレーズを指定する。
    signingKeyPassphrase = System.getenv("PGP_SIGNING_KEY_PASSPHRASE")
}

defaultTasks("spotlessApply", "build")