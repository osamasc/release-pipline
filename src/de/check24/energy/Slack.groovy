package de.check24.energy

import de.check24.energy.slack.BlockFactory
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

    public final static enum BuildStatus {

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

    SlackResponse sendIssues(String repoAddress, String productionCommit, String masterCommit) {
        BlockFactory builder = new BlockFactory()
        ArrayList block = builder.getIssuesMessage(this.gitContext, repoAddress, productionCommit, masterCommit)
        return this.sendBlock(block)
    }

    SlackResponse sendBuildMessage(
            String version = '',
            BuildStatus status = BuildStatus.START,
            String timestamp = null
    ) {
        this.context.wrap([$class: 'BuildUser']) {
            BlockFactory builder = new BlockFactory()

            String builderName = '<@' + this.getUserId(this.context.env.BUILD_USER_EMAIL) + '>'
            Integer commitCount = this.gitContext.commits.size()

            ArrayList block = builder.getBuildMessage(
                    projectName,
                    this.context.env.BUILD_NUMBER,
                    builderName,
                    commitCount,
                    version,
                    getStatus(status)
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
                return ':large_blue_circle: START'
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
