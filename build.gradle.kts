import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import java.io.FileInputStream
import java.util.Properties


plugins {
    id("org.jetbrains.intellij.platform") version "2.1.0"
    kotlin("jvm") version "1.9.25"
}

group = "com.ruben"
version = "0.4.4"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion")
        )
        bundledPlugin("org.jetbrains.kotlin")

        instrumentationTools()
        pluginVerifier()
        zipSigner()

        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellijPlatform {
    pluginConfiguration {
        changeNotes.set(
            """
            <br><b>v0.4.4: </b></br>
            The plugin is now compatible with K2 compiler.
            <br></br>
            <br><b>v0.4.3: </b></br>
            Now the plugin has a setting using which the users can specify which packages need inspection. 
            This is specially useful in large projects which have multiple modules.
            If no packages are provided then the plugin will continue to inspect all the data classes.
            <br></br>
            <br><b>v0.3.2: </b></br>
            Fix inner class not detecting required annotation
            <br></br>
            <br><b>v0.3.1: </b></br>
            Added support for <b>Json (Moshi)</b> and <b>SerialName (Kotlinx-Serialization)</b> annotations
            <br></br>
            <br><b>Initial Release v0.2.3: </b></br>
            Inspect kotlin data classes for missing <b>SerializedName</b> annotations
            """.trimIndent()
        )

        ideaVersion {
            sinceBuild = providers.gradleProperty("sinceBuild")
            untilBuild = provider { null }
        }
    }

    signing {
        val file = File("local.properties")
        val prop = Properties()
        prop.load(FileInputStream(file))

        privateKeyFile = file("${prop["privateKeyPath"]}")
        password = "${prop["signingPass"]}"
        certificateChainFile = file("${prop["certificateChainPath"]}")
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    test {
        isScanForTestClasses = false
        // Only run tests from classes that end with "Test"
        include("**/*Test.class")
        systemProperty("idea.force.use.core.classloader", "true")
    }
    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks.named<RunIdeTask>("runIde") {
    jvmArgumentProviders += CommandLineArgumentProvider {
        listOf("-Didea.kotlin.plugin.use.k2=true")
    }
}