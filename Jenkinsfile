@Library('utils') _

import de.check24.energy.Slack

pipeline {
    agent any
    stages {

        stage('Checkout') {
            steps {
                script {
                    slack.checkk()
                }
            }
        }

        stage('Hello') {
            steps {
//                 git branch: 'main', url: 'https://github.com/osamasc/release-pipline'
                script {
                    slack.sendReleaseMessage("123")
                }

            }
        }
    }
}
