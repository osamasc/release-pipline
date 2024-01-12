#!/usr/bin/env groovy
import de.check24.energy.Slack
import de.check24.energy.slack.SlackResponse

def call(project, tag, environment, status = Slack.BuildStatus.STARTED) {


    if (!env.SLACK_TIMESTAMP && status != Slack.BuildStatus.STARTED) {
        return
    }

    def gitContext = gitChangelog(
            returnType: 'CONTEXT',
            from: [type: 'REF', value: 'main'],
            to: [type: 'COMMIT', value: env.GIT_COMMIT],
            ignoreCommitsIfMessageMatches: '^Merge.*',
            ignoreCommitsWithoutIssue: false,
            customIssues: [
                    [
                            issuePattern: '([A-Z]+-[0-9]+)',
                            link        : 'https://c24-energie.atlassian.net/browse/${PATTERN_GROUP}',
                            name        : 'JIRA',
                            title       : '${PATTERN_GROUP}',
                    ]
            ],
    )

    String username = 'slack-user'
    String icon = ':jenkins:'

    String triggeredBy = 'Gatekeeper'

    env.BUILD_USER_EMAIL = 'osama.ahmed@check24.de'
    if (env.BUILD_USER_EMAIL) {
        def userId = slackUserIdFromEmail email: env.BUILD_USER_EMAIL, botUser: true
        println userId

        triggeredBy = "<@${userId}>"
    }


    Slack slackInstance = new Slack(this, 'C06C1GJPAJE', username, icon, gitContext, 'slack')
    def block = slackInstance.sendBuildMessage(tag, project, environment, triggeredBy, status, env.SLACK_TIMESTAMP)

    def slackResponse = slackSend color: "#439FE0", channel: 'C06C1GJPAJE', blocks: block, botUser: true, iconEmoji: icon
    slackSend color: "#439FE0", channel: slackResponse.threadId, message: "text", botUser: true

//    if (!env.SLACK_TIMESTAMP) {
//        env.SLACK_TIMESTAMP = response.ts
//        def slackResponse = slackSend(channel: "C06C1GJPAJE", message: "Here is the primary message")
//        println slackResponse.threadId
//
//    }
}

def checkk() {
    def scmVars = checkout([$class                           : 'GitSCM',
                            branches                         : [[name: '*/main']],
                            doGenerateSubmoduleConfigurations: false,
                            extensions                       : [],
                            submoduleCfg                     : [],
                            userRemoteConfigs                : [
                                    [url: 'https://github.com/osamasc/test-pro.git', credentialsId: 'github']]
    ])

    env.GIT_COMMIT = scmVars.GIT_COMMIT
    env.GIT_BRANCH = scmVars.GIT_BRANCH
}
