plugins {
    id ("org.jetbrains.intellij") version "1.5.3"
    kotlin("jvm") version "1.6.20"
}

group = "com.ruben"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

intellij {
    version.set("2021.3.2")
    plugins.set(listOf("Kotlin"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
    )
}