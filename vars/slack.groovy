#!/usr/bin/env groovy
import de.check24.energy.Config
import de.check24.energy.Slack
import de.check24.energy.slack.SlackResponse

def sendBuildNotification(project, tag, environment, status) {

    if (!env.SLACK_TIMESTAMP && state != Slack.BuildStatus.STARTED) {
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
    String icon = ':e-mail:'

    Slack slackInstance = new Slack(this, 'C06C1GJPAJE', username, icon, gitContext, 'slack')
    SlackResponse response = slackInstance.sendBuildMessage(tag, project, environment, status, env.SLACK_TIMESTAMP)

    if (!env.SLACK_TIMESTAMP) {
        env.SLACK_TIMESTAMP = response.ts
    }
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
