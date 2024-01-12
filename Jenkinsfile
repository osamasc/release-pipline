@Library('utils') _

import de.check24.energy.Slack

pipeline {
    agent any
    stages {

        stage('Checkout') {
            steps {
                script {
                    echo env.GIT_COMMIT
                    slack.checkk()
                }
            }
        }

        stage('Hello') {
            steps {
                script {
                    slack.sendBuildNotification("r20230915-105717")
                }

            }
        }
    }
}
