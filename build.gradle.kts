import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.7.21"
    id("org.openapi.generator") version "4.3.1"
}

group = "de.beiertu"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("io.swagger:swagger-annotations:1.6.8")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Creates a gradle task that generates java code from an Open API spec
fun generateOpenApiSpec(
    taskName: String,
    spec: String,
    pkg: String
) = tasks.register<GenerateTask>(taskName) {
    group = "Source Generation"
    description = "Generates kotlin classes from an Open API specification"

    verbose.set(false)
    generatorName.set("java")
    inputSpec.set(spec)
    outputDir.set("$buildDir/generated")
    packageName.set(pkg)
    modelPackage.set(pkg)
    generateModelTests.set(false)
    library.set("jersey2")
    configOptions.set(
        mapOf(
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "dateLibrary" to "java8",
            "bigDecimalAsString" to "true",
            "hideGenerationTimestamp" to "true",
            "useBeanValidation" to "false",
            "performBeanValidation" to "false",
            "openApiNullable" to "false"
        )
    )
    systemProperties.set(
        mapOf("models" to "", "modelDocs" to "false")
    )
}

generateOpenApiSpec(
    taskName = "generatePetStoreApi",
    spec = "$rootDir/specs/pet-store-api-v1.yaml",
    pkg = "de.beiertu.pet.store.api.v1"
)

tasks.compileKotlin.configure {
    dependsOn(
        tasks.getByName("generatePetStoreApi")
    )
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}
