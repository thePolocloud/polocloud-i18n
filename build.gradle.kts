plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    `maven-publish`
    signing

    alias(libs.plugins.nexus.publish)
}

group = "dev.httpmarco.polocloud"
version = "3.0.0-pre.8-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.log4j.api)
    implementation(libs.gson)
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("polocloud-i18n")
                description.set("Dynamic translation system for the PoloCloud ecosystem.")
                url.set("https://github.com/thePolocloud/polocloud-i18n")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("httpmarco")
                        name.set("Mirco Lindenau")
                        email.set("mirco.lindenau@gmx.de")
                    }
                }

                scm {
                    url.set("https://github.com/thePolocloud/polocloud-i18n")
                    connection.set("scm:git:https://github.com/thePolocloud/polocloud-i18n.git")
                    developerConnection.set("scm:git:https://github.com/thePolocloud/polocloud-i18n.git")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            username.set(System.getenv("OSSRH_USERNAME"))
            password.set(System.getenv("OSSRH_PASSWORD"))
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassphrase = System.getenv("GPG_PASSPHRASE")

    if (signingKey != null && signingPassphrase != null) {
        useInMemoryPgpKeys(signingKey, signingPassphrase)
        sign(publishing.publications)
    }
}