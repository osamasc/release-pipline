package de.check24.energy


import de.check24.energy.slack.BlockTemplate
import de.check24.energy.slack.SlackRequest
import de.check24.energy.slack.SlackResponse

/**
 * @author Osama Ahmed <osama.ahmed@check24.de>
 * @copyright Check24 Vergleichsportal Energie GmbH
 */
class Slack {

    Script context
    String projectName
    SlackRequest requestHandler
    def gitContext

    Slack(
            Script jenkinsContext,
            String projectName,
            String channel,
            String username,
            String iconEmoji,
            def gitContext,
            String credentialId
    ) {
        this.context = jenkinsContext
        this.projectName = projectName
        this.requestHandler = new SlackRequest(jenkinsContext, channel, username, iconEmoji, credentialId)
        this.gitContext = gitContext
    }

    SlackResponse sendMessage(String message, String state = null, String timestamp = null) {
        switch (state) {
            case 'success':
                message = ':successful: ' + message
                break
            case 'warning':
                message = ':warning: ' + message
                break
            case 'error':
                message = ':alert: ' + message
                break
        }

        ArrayList block = [
                [
                        'type': 'section',
                        'text': [
                                'type' : 'plain_text',
                                'text' : message,
                                'emoji': true
                        ]
                ]
        ]

        return this.requestHandler.sendBlock(block, timestamp)
    }

    final static enum BuildStatus {

        START,
        DIFF,
        BUILD,
        TEST,
        DEPLOY,
        ACTIVATE,
        DELAYED_ACTIVATE,
        AWAIT_INPUT,
        ACTIVE,
        FAILED,
        ABORTED

    }

    String getUserId(String emailAddress) {

        return this.requestHandler.getUserIdByEmail(emailAddress)?.user?.id
    }

    String getUserName(String emailAddress) {
        return this.requestHandler.getUserIdByEmail(emailAddress)?.user?.name
    }

    SlackResponse sendSuccess(String message, String timestamp = null) {
        return this.sendMessage(message, 'success', timestamp)
    }

    SlackResponse sendWarning(String message, String timestamp = null) {
        return this.sendMessage(message, 'warning', timestamp)
    }

    SlackResponse sendError(String message, String timestamp = null) {
        return this.sendMessage(message, 'error', timestamp)
    }

    SlackResponse sendBuildMessage(
            String buildTag = '',
            BuildStatus status = BuildStatus.START,
            String timestamp = null
    ) {
        this.context.wrap([$class: 'BuildUser']) {
            String triggeredBy = "Jenkins"
//            if(this.context.env.BUILD_USER_EMAIL) {
//                 triggeredBy = '<@' + this.getUserId(this.context.env.BUILD_USER_EMAIL) + '>'
//            }

            ArrayList block = BlockTemplate.prepare(
                    this.context.env.BUILD_NUMBER,
                    buildTag,
                    getStatus(status),
                    'messagesystem',
                    triggeredBy,
                    'prod',
                    this.gitContext,
                    this.context.env.GIT_COMMIT,
                    this.context.env.GIT_PREVIOUS_SUCCESSFUL_COMMIT,
                    this.context.env.BUILD_URL,
                    this.context.env.BUILD_DISPLAY_NAME
            )

            return this.sendBlock(block, timestamp)
        }
    }

    protected SlackResponse sendBlock(ArrayList block, String timestamp = null) {
        return this.requestHandler.sendBlock(block, timestamp)
    }

    protected sendFile(String path, String description) {
        return this.context.slackUploadFile(
                channel: Config.PROJECT[JOB_BASE_NAME]['Slack']['Channel'],
                credentialId: 'slack',
                filePath: path,
                initialComment: description
        )
    }

    private static String getStatus(BuildStatus status) {
        switch (status) {
            case BuildStatus.START:
                return ':waiting: START'
            case BuildStatus.DIFF:
                return ':pepe_roll: WAIT FOR DIFF'
            case BuildStatus.BUILD:
                return ':waiting: BUILDING'
            case BuildStatus.TEST:
                return ':waiting: TESTING'
            case BuildStatus.DEPLOY:
                return ':waiting: DEPLOYING'
            case BuildStatus.ACTIVATE:
                return ':waiting: ACTIVATING'
            case BuildStatus.DELAYED_ACTIVATE:
                return ':waiting: DELAYED ACTIVATION'
            case BuildStatus.AWAIT_INPUT:
                return ':waiting: AWAIT INPUT'
            case BuildStatus.ACTIVE:
                return ':large_green_circle: ACTIVE'
            case BuildStatus.FAILED:
                return ':alert: FAILED'
            case BuildStatus.ABORTED:
                return ':thumbsdown: ABORTED'
            default:
                return ':question:'
        }
    }

}
