/*
 *     MyPivot - Accounting reconciliation system of Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import com.google.common.base.MoreObjects

plugins {
  java
  id("org.springframework.boot") version "2.6.6"
  id("io.freefair.lombok") version "8.4"
  // https://github.com/ben-manes/gradle-versions-plugin
  // plugin that provides a task to determine which dependencies have updates
  id("com.github.ben-manes.versions") version "0.44.0"
  //code generation for soap webservices classes (via jaxb)
  id("com.intershop.gradle.jaxb") version "5.2.1"
  //version file
  id("com.palantir.git-version") version "0.15.0"
  //semantic versioning
  id("com.glovoapp.semantic-versioning") version "1.1.10"
}

val xmlAdapterJarName = "${project.name}-XmlAdapter.jar"
task("xmlAdapterJar", type = Jar::class) {
  archiveFileName.set(xmlAdapterJarName)
  from(sourceSets.main.get().output) {
    include("**/TrimStringXmlAdapter.class")
  }
  includeEmptyDirs = false
  destinationDirectory.set(file("libs"))
}

//lombok configuration
//lombok.version.set("1.18.22")

//check if we are using a "local developer" profile
val isLocalDevProfile = System.getenv().containsKey("spring.profiles.active")
    && System.getenv("spring.profiles.active").toLowerCase().startsWith("local") ||
    System.getenv().containsKey("SPRING_PROFILES_ACTIVE")
    && System.getenv("SPRING_PROFILES_ACTIVE").toLowerCase().startsWith("local")

project.logger.lifecycle("isLocalDevProfile: $isLocalDevProfile")

springBoot {
  mainClass.set(project.findProperty("main")?.toString() ?: "it.regioneveneto.mygov.payment.mypivot4.WebApplication")
  val cl = mainClass.get()
  project.logger.lifecycle("mainClass: $cl")
}

tasks {
  processResources {
    dependsOn( "jaxbSchemaGenMypivot")
  }
  bootJar {
    val appendix = archiveAppendix.orNull
    val version = archiveVersion.orNull
    // remove version from archive JAR filename in order to keep it constant and ease CI/CD pipeline
    val jarFilename = archiveBaseName.get() +
            (if (appendix.isNullOrBlank()) "" else "-$appendix") +
//       (if (version.isNullOrBlank()) "" else "-$version") +
            "."+archiveExtension.get()
    println("jar filename: $jarFilename")
    println("jar version: $version")
    archiveFileName.set(jarFilename)
    launchScript()
  }
  bootRun {
    systemProperties(System.getenv().filterKeys { setOf("spring.profiles.active").contains(it) })
  }
  dependencyUpdates {
    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    revision = "release"
  }
}

apply(plugin = "io.spring.dependency-management")

group = "it.regioneveneto.mypivot4"

repositories {
  mavenCentral()
//  flatDir {
//    dirs("libs")
//  }
}

sourceSets {
  main {
    resources {
      srcDir(project.buildDir.name+"/generated/jaxb/resources")
      if (project.hasProperty("SERVER")) {
        exclude(
            "application*.properties",
            "data.sql",
            "saml-keystore.jks",
            "saml_metadata*.xml",
            "saml_myid_*",
            "ssl-springboot-keystore.jks",
            "spring-devtools.properties"
        )
      }
    }
  }
}

dependencies {
  //spring boot version: see plugin section above
  //lombok: see plugin section above
  val commonsLang3Version = "3.12.0"
  val commonsTextVersion = "1.9"
  val gsonVersion="2.8.9"
  val httpClientVersion = "4.5.13"
  val imgScalrVersion = "4.2"
  val jacksonDataformatVersion = "2.13.1"
  val jdbiVersion = "3.27.0"
  val jjwtVersion = "0.11.2"
  val jodaVersion = "2.10.13"
  val postgresJdbcVersion = "42.3.1"
  val rhinoScriptVersion="1.1.1"
  val springdocVersion = "1.6.4"
  val wsdl4jVersion = "1.6.3"
  val xmlSchemaVersion = "2.3.0"
  val xmlbeansVersion="5.0.3"

  //TODO: change to the commented versions (or remove if not neeeded) when switching to JAXB3
  val activationVersion = "1.1.1"   // "2.0.1"
  val jaxbVersion = "2.3.3"         // "3.0.1"
  val jaxbCoreVersion = "2.3.0.1"
  val jaxbApiVersion = "2.3.1"


  //spring boot
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-devtools")
  implementation("org.springframework.boot:spring-boot-starter-web-services")
  implementation("org.springframework.boot:spring-boot-starter-artemis")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework:spring-aspects")
  implementation("org.springframework.retry:spring-retry")

  //Embdedded Redis server (only development)
  implementation("it.ozimov:embedded-redis:0.7.3") {
    exclude(group="org.slf4j", module="slf4j-simple")
  }

  //jdbi3
  implementation("org.jdbi:jdbi3-spring5:$jdbiVersion")
  implementation("org.jdbi:jdbi3-sqlobject:$jdbiVersion")
  implementation("org.jdbi:jdbi3-stringtemplate4:$jdbiVersion")

  //apache httpClient
  implementation("org.apache.httpcomponents:httpclient:$httpClientVersion")

  //joda-time
  implementation("joda-time:joda-time:$jodaVersion")

  //apache commons-lang3
  implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
  implementation("org.apache.commons:commons-text:$commonsTextVersion")

  //apache xmlbeans
  implementation("org.apache.xmlbeans:xmlbeans:$xmlbeansVersion")
  implementation("de.christophkraemer:rhino-script-engine:$rhinoScriptVersion")

  //gson
  implementation("com.google.code.gson:gson:$gsonVersion")

  //jwt
  implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

  //postgres jdbc
  implementation("org.postgresql:postgresql:$postgresJdbcVersion")

  //jackson csv reader
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:$jacksonDataformatVersion")

  //ImgScalr
  implementation("org.imgscalr:imgscalr-lib:$imgScalrVersion")

  //webservice soap
  implementation("wsdl4j:wsdl4j:$wsdl4jVersion")
  implementation("org.apache.ws.xmlschema:xmlschema-core:$xmlSchemaVersion")

  //TODO: fallback to JAXB 2 implementation since Spring currently doesn't support JAXB 3 (jakarta.ee namespace)
  // when Spring will be ready (Spring v6), the following modifications should be made:
  // - upgrade dependencies to the commented ones below (further adjustment may be needed)
  // - upgrade version variables
  // - some changes on .jxb (change namespace and version tag)
  // - some changes on import of various java files (due to change of namespace javax.* -> jakarta.*)

  //jaxb
  runtimeOnly("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion")
  implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
  jaxb("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion")
  jaxb("com.sun.xml.bind:jaxb-xjc:$jaxbVersion")
  jaxb("com.sun.xml.bind:jaxb-jxc:$jaxbVersion")
  jaxb("com.sun.xml.bind:jaxb-core:$jaxbCoreVersion")
  jaxb("javax.xml.bind:jaxb-api:$jaxbApiVersion")
  jaxb("javax.activation:activation:$activationVersion")
  // see remarks on source file TrimStringXmlAdapter.java
  jaxbext(files("libs/$xmlAdapterJarName"))
  jaxbext("org.slf4j:slf4j-simple:1.7.9") // see https://github.com/IntershopCommunicationsAG/jaxb-gradle-plugin/issues/37

  //Springdoc OpenAPI UI (Swagger)
  implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
  implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")

  //jdbc ssh wrapper(needed just during development)
  if(isLocalDevProfile) {
    implementation("io.github.emotionbug:jdbc-sshj:1.0.13")
    implementation("com.hierynomus:sshj:0.32.0")
    implementation("org.apache.sshd:sshd-core:2.8.0") {
      exclude(group = "org.slf4j", module = "slf4j-api")
    }
  }

  /** =================== **/

  //testCompile("junit", "junit", "4.12")
}

  val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

  springBoot {
    buildInfo {
      properties {
        val details = versionDetails()
        additional = mapOf<String, Any>(
            "gitHash" to details.gitHash,
            "gitHashFull" to details.gitHashFull,
            "branchName" to MoreObjects.firstNonNull(details.branchName, ""),
            "lastTag" to details.lastTag,
            "commitDistance" to details.commitDistance,
            "isCleanTag" to details.isCleanTag
        )
      }
    }
  }

configure<JavaPluginExtension> {
  sourceCompatibility = JavaVersion.VERSION_11
}

jaxb {
  javaGen {
    register("mypivot") {
      args = listOf("-wsdl")
      outputDir = file(project.buildDir.name + "/generated/jaxb/java")
      schema = file("src/main/resources/wsdl/mypivot/mypivot-per-ente.wsdl")
      bindings = layout.files("src/main/resources/wsdl/mypivot/mypivot-per-ente.xjb")
    }
  }
  schemaGen {
    register("mypivot") {
      outputDir = file(project.buildDir.name + "/generated/jaxb/resources/xsd")
      inputDir = file(project.buildDir.name + "/generated/jaxb/java")
      namespaceconfigs = mapOf(
              "http://www.regione.veneto.it/schemas/2012/Pagamenti/" to "PagInf_RP_Esito_6_0_2.xsd",
              "http://www.digitpa.gov.it/schemas/2011/Pagamenti/" to "FlussoRiversamento_1_0_4.xsd"
      )
    }
    tasks.named("jaxbSchemaGenMypivot"){
      dependsOn("jaxbJavaGenMypivot")
    }
  }
}
