import java.io.FileInputStream
import java.util.Properties


plugins {
    id ("org.jetbrains.intellij") version "1.6.0"
    kotlin("jvm") version "1.6.20"
}

group = "com.ruben"
version = "0.3.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

intellij {
    version.set("2021.3.2")
    plugins.set(listOf("Kotlin", "java"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        isScanForTestClasses = false
        // Only run tests from classes that end with "Test"
        include("**/*Test.class")
        systemProperty("idea.force.use.core.classloader", "true")
        val file = File("local.properties")
        val prop = Properties()
        prop.load(FileInputStream(file))
        systemProperty("idea.home.path", "${prop["home.path"]}")
    }
    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    patchPluginXml {
        sinceBuild.set("193.*")
    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """<br><b>Initial Release v0.2.3:</b></br>
        Inspect kotlin data classes for missing <b>SerializedName</b> annotations
        <br></br>
        <br><b>v0.3.0: </b></br>
        Added support for <b>Json (Moshi)</b> and <b>SerialName (Kotlinx-Serialization)</b> annotations.
        """
    )
}