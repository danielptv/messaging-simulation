import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    idea
    checkstyle

    alias(libs.plugins.springBoot)
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.dependencyAnalysis)
}

defaultTasks = mutableListOf("compileTestJava")
group = "com.danielptv"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaVersion.get()))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone") { mavenContent { releasesOnly() } }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation(platform(libs.springBom))
    implementation(platform(libs.springBootBom))
    implementation(platform(libs.assertjBom))
    implementation(platform(libs.junitBom))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-tomcat") {
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-websocket")
    }
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(libs.jfiglet)
    implementation(libs.springKafka)
    runtimeOnly(libs.kafkaClients)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.spotbugsAnnotations)
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.hamcrest", module = "hamcrest")
        exclude(group = "org.skyscreamer", module = "jsonassert")
        exclude(group = "org.xmlunit", module = "xmlunit-core")
    }
    testImplementation(libs.embeddedKafka)
    testRuntimeOnly(libs.junitPlatformSuiteEngine)

    constraints {
        implementation(libs.annotations)
        implementation(libs.tomcatCore)
        implementation(libs.tomcatEl)
    }
}

tasks.compileJava {
    options.isDeprecation = true
    with(options.compilerArgs) {
        add("-Xlint:unchecked")
        add("-parameters")
        add("--enable-preview")
        add("--add-opens")
        add("--add-exports")
    }
}

tasks.compileTestJava {
    options.isDeprecation = true
    with(options.compilerArgs) {
        add("-Xlint:unchecked")
        add("--enable-preview")
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    val path = "messaging-sim"
    imageName.set("$path/${project.name}")
    val tag = System.getProperty("tag") ?: project.version.toString()
    tags.set(mutableListOf("$path/${project.name}:$tag"))

    @Suppress("StringLiteralDuplication")
    environment.set(
        mapOf(
            "BP_JVM_VERSION" to "19.0.2",
            "BPL_JVM_THREAD_COUNT" to "20",
            "BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
            "BPE_APPEND_JAVA_TOOL_OPTIONS" to "--enable-preview",
        )
    )
}

tasks.named<BootRun>("bootRun") {
    jvmArgs("--enable-preview")
    val port = System.getProperty("port")
    if (port != null) {
        systemProperty("server.port", port)
    }

    val type = System.getProperty("type")
    if (type != null) {
        systemProperties["CONSUMER_TYPE"] = type
    }

    if (System.getProperty("tls") == "false") {
        @Suppress("StringLiteralDuplication")
        systemProperty("server.ssl.enabled", "false")
        @Suppress("StringLiteralDuplication")
        systemProperty("server.http2.enabled", "false")
    }
    systemProperty("spring.config.location", "classpath:/application.yml")
    systemProperty("spring.output.ansi.enabled", "ALWAYS")
    systemProperty("server.tomcat.basedir", "./build/tomcat")
    systemProperty("LOG_PATH", "./build/log")
    systemProperty("APPLICATION_LOGLEVEL", "TRACE")
}

tasks.test {
    useJUnitPlatform {
        includeTags = setOf("integration", "unit")
    }

    systemProperty("junit.platform.output.capture.stdout", true)
    systemProperty("junit.platform.output.capture.stderr", true)
    systemProperty("spring.config.location", "classpath:/application.yml")
    systemProperty("server.tomcat.basedir", "./build/tomcat")

    systemProperty("LOG_PATH", "./build/log")
    systemProperty("APPLICATION_LOGLEVEL", "TRACE")
    jvmArgs("--enable-preview")
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("extras/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/extras",
    )
    isIgnoreFailures = false
}

spotbugs {
    toolVersion.set(libs.versions.spotbugs.get())
}
tasks.spotbugsMain {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugs.html"))
    }
}

idea {
    module {
        isDownloadJavadoc = true
        sourceDirs.add(file("generated/"))
        generatedSourceDirs.add(file("generated/"))
    }
}
