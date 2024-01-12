package de.check24.energy


import de.check24.energy.slack.BlockBuilder
import de.check24.energy.slack.SlackRequest
import de.check24.energy.slack.SlackResponse

/**
 * @author Osama Ahmed <osama.ahmed@check24.de>
 * @copyright Check24 Vergleichsportal Energie GmbH
 */
class Slack {

    Script context
    SlackRequest requestHandler
    def gitContext

    Slack(
            Script jenkinsContext,
            String channel,
            String username,
            String iconEmoji,
            def gitContext,
            String credentialId
    ) {
        this.context = jenkinsContext
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

        STARTED,
        SUCCESS,
        ACTIVE,
        BLUEGREEN_ROLLBACK,
        BLUEGREEN_ACTIVE,
        DEPLOY,
        ABORTED,
        DELAYED_ACTIVATE,
        ROLLBACK,
        FAILURE,

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

    ArrayList sendBuildMessage(
            String buildTag = '',
            String project = '',
            String projectEnv = '',
            String triggeredBy = '',
            BuildStatus status = BuildStatus.STARTED,
            String timestamp = null
    ) {
        this.context.wrap([$class: 'BuildUser']) {

            ArrayList block = BlockBuilder.prepare(
                    this.context.env.BUILD_NUMBER,
                    buildTag,
                    getStatus(status),
                    project,
                    triggeredBy,
                    projectEnv,
                    this.gitContext,
                    this.context.env.GIT_COMMIT,
                    this.context.env.GIT_PREVIOUS_SUCCESSFUL_COMMIT,
                    this.context.env.BUILD_URL,
                    this.context.env.BUILD_DISPLAY_NAME
            )

            return block;
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
            case BuildStatus.STARTED:
                return '*STARTED* :waiting:'
            case BuildStatus.TESTING:
                return ':waiting: TESTING :waiting:'
            case BuildStatus.DEPLOY:
                return ':waiting: DEPLOYING :waiting:'
            case BuildStatus.ACTIVE:
                return ':approve: ACTIVATING :waiting:'
            case BuildStatus.FAILURE:
                return ':alert: FAILED:'
            case BuildStatus.ABORTED:
                return ':thumbsdown: ABORTED'
            default:
                return ':question:'
        }
    }

}
