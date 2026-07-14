import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  alias(libs.plugins.jetbrains.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ktlint)
  id("application")
}

ktlint {
  reporters {
    reporter(ReporterType.PLAIN)
  }
  additionalEditorconfig.set(
    mapOf(
      "indent_size" to "2",
    ),
  )
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

application {
  mainClass.set("com.thindie.server.SttServer")
}

kotlin {
  compilerOptions {
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }
}

dependencies {
  implementation(libs.ktor.server.core.jvm)
  implementation(libs.ktor.server.netty.jvm)

  implementation(libs.ktor.server.content.negotiation.jvm)
  implementation(libs.ktor.serialization.kotlinx.json.jvm)

  implementation(libs.ktor.server.call.logging.jvm)
  implementation(libs.logback.classic)

  implementation(libs.ktor.server.websockets.jvm)
}
