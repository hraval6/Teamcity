import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.SSHUpload
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildSteps.sshUpload
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    buildType(SshUpload)

    template(Settings)

    params {
        param("username", "harshit")
        param("git_username", "hraval6")
    }
}

object SshUpload : BuildType({
    templates(Settings)
    name = "git operation"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "git pull from repo"
            id = "RUNNER_1"
            scriptContent = """
                git checkout main
                git pull
                echo "hello world" > newfile
                git add newfile
                git commit -m "adding a file"
                git push origin
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_1"
            triggerRules = "+:**"
            branchFilter = ""
        }
    }

    failureConditions {
        failOnText {
            id = "BUILD_EXT_1"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "failed,"
            failureMessage = "One or more tests failed"
            reverse = false
            stopBuildOnFailure = true
        }
    }
})

object Settings : Template({
    name = "settings"

    steps {
        sshUpload {
            name = "UPLOAD"
            id = "RUNNER_1"
            transportProtocol = SSHUpload.TransportProtocol.SCP
            sourcePath = """C:\Users\hrava\Pictures\* => pictures"""
            targetUrl = """192.168.1.18:C:\Users\hrava\Desktop"""
            authMethod = sshAgent {
                username = "harava"
            }
        }
    }
})
