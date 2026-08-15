group = "app.purifree"

patches {
    about {
        name = "PuriFree"
        description = "Hide ads and the Pur subscription option, and strip UTM tracking parameters from share URLs in Kleinanzeigen."
        source = "https://github.com/cyborium/purifree.git"
        author = "cyborium"
        contact = "https://github.com/cyborium/purifree/issues"
        website = "https://github.com/cyborium/purifree"
        license = "GPLv3"
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
