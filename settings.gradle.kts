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

rootProject.name = "Solaris"
include(":app")
include(":feature:home")
include(":core:network")
include(":core:model")
include(":core:database")
include(":core:data")
include(":feature:locations")
include(":core:domain")
include(":feature:sync-ui")
include(":core:datastore")
include(":core:workmanager")
