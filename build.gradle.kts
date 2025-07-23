import org.gradle.configurationcache.extensions.capitalized

plugins {
    java
}

allprojects {
    apply<JavaPlugin>()

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }

    group = "it.unibo.ise"
}

subprojects {
    sourceSets {
        main {
            resources {
                srcDir("src/main/asl")
            }
        }
    }

    dependencies {
        implementation("io.github.jason-lang:jason-interpreter:3.2.1")
        testImplementation("junit", "junit", "4.13.2")
    }

    tasks.register<JavaExec>("runRestaurantMas") {
        group = "run"
        classpath = sourceSets.getByName("main").runtimeClasspath
        mainClass.set("jason.infra.local.RunLocalMAS")
        standardInput = System.`in`
        javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
        args = listOf("restaurant.mas2j") // riferito a workingDir
    }
}
