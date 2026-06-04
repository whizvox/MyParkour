plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jooq.jooq-codegen-gradle") version "3.21.4"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("org.jooq:jooq:3.21.4")
    jooqCodegen("org.xerial:sqlite-jdbc:3.53.1.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

sourceSets {
    main {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.sqlite.JDBC"
            url = "jdbc:sqlite:$projectDir/library.db"
        }
        generator {
            database {
                name = "org.jooq.meta.sqlite.SQLiteDatabase"
                includes = ".*"
                excludes = ""
            }
            generate {}
            target {
                packageName = "me.whizvox.myparkour.db"
                directory = "build/generated-sources/jooq"
            }
        }
    }
}
