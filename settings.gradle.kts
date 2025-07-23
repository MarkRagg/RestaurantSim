plugins {
    id("com.gradle.develocity") version "3.19.2"
}

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "ise-lab-code-jason"

include("restaurant-simulator")

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/terms-of-service")
        termsOfUseAgree.set("yes")
        uploadInBackground.set(!System.getenv("CI").toBoolean())
        publishing {
            onlyIf {
                it.buildResult.failures.isNotEmpty()
            }
        }
    }
}
