#!/usr/bin/env groovy

import de.check24.energy.Config
import de.check24.energy.Slack
import de.check24.energy.slack.SlackResponse

//void sendSuccess(String message) {
//    sendNotification(message, 'success')
//}
//
//void sendWarning(String message) {
//    sendNotification(message, 'warning')
//}
//
//void sendError(String message) {
//    sendNotification(message, 'error')
//}

//void sendNotification(String message, String state = null) {
//    String teamName = (JOB_NAME =~ /(.*)\//)[0][1]
//    String channel = Config.PROJECT[JOB_BASE_NAME]['Slack']['Channel']
//    String username = Config.PROJECT[JOB_BASE_NAME]['Slack']['User']
//    String icon = Config.PROJECT[JOB_BASE_NAME]['Slack']['Icon']
//    new Slack(this, teamName, JOB_BASE_NAME, channel, username, icon, [], 'slack').sendMessage(message, state)
//}

def sendReleaseMessage(String version = null, def state = Slack.BuildStatus.START) {

    if (!env.SLACK_TIMESTAMP && state != Slack.BuildStatus.START) {
        return
    }

    def gitContext = gitChangelog(
            returnType: 'CONTEXT',
            from: [type: 'REF', value: 'production'],
            to: [type: 'COMMIT', value: GIT_COMMIT],
            customIssues: [
                    [
                            issuePattern: '([A-Z]+-[0-9]+)',
                            link        : 'https://energy.atlassian.net/browse/${PATTERN_GROUP}',
                            name        : 'JIRA',
                            title       : '${PATTERN_GROUP}'
                    ]
            ],
    )

    String channel = 'release-pipeline'
    String username = 'slack-user'
    String icon = ':release:'

    Slack slackInstance = new Slack(this, JOB_BASE_NAME, channel, username, icon, gitContext, 'slack')
    SlackResponse response = slackInstance.sendBuildMessage(version, state, env.SLACK_TIMESTAMP)
    if (!env.SLACK_TIMESTAMP) {
        env.SLACK_TIMESTAMP = response.ts
        String productionCommit = sh(returnStdout: true, script: 'git rev-list -n 1 production').trim()
        slackInstance.sendIssues(GIT_URL, productionCommit, GIT_COMMIT)
    }
}

//void changeReleaseToDiff(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.DIFF)
//}
//
//void changeReleaseToBuild(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.BUILD)
//}
//
//void changeReleaseToTest(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.TEST)
//}
//
//void changeReleaseToDeploy(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.DEPLOY)
//}
//
//void changeReleaseToActivate(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.ACTIVATE)
//}
//
//void changeReleaseToDelayedActivate(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.DELAYED_ACTIVATE)
//}
//
//void changeReleaseToAwaitInput(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.AWAIT_INPUT)
//}
//
//void changeReleaseToActive(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.ACTIVE)
//}
//
//void changeReleaseToFailed(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.FAILED)
//}
//
//void changeReleaseToAborted(String version = null) {
//    sendReleaseMessage(version, Slack.BuildStatus.ABORTED)
//}
//
//String getUserName(String email) {
//    return '<@' + new Slack(this, '', JOB_BASE_NAME, '', '', '', [], 'slack').getUserName(email) + '>'
//}
