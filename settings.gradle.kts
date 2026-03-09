pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Nav3Sample"
include(":app")
include(":libraries:navigation")
include(":core:navigation")
include(":core:domain")
include(":core:data")
include(":features:auth")
include(":features:order")
include(":compose:navigation")
include(":compose:auth")
include(":compose:order")
include(":compose:common")
