package de.check24.energy.slack

import groovy.json.JsonBuilder

class SlackRequest {

    Script context
    String channel
    String username
    String iconEmoji
    String credentialId

    SlackRequest(
            Script context,
            String channel,
            String username,
            String iconEmoji,
            String credentialId
    ) {
        this.context = context
        this.channel = channel
        this.username = username
        this.iconEmoji = iconEmoji
        this.credentialId = credentialId
    }

    SlackResponse sendBlock(List block, String timestamp = null) {
        LinkedHashMap slackConfig = [
                channel   : this.channel,
                blocks    : block,
                icon_emoji: this.iconEmoji,
                username  : this.username,
                ts        : timestamp
        ]

        this.context.withCredentials([
                this.context.string(credentialsId: this.credentialId, variable: 'slackToken')
        ]) {
            String apiEndpoint = 'https://slack.com/api/chat.postMessage'
            if (timestamp) {
                apiEndpoint = 'https://slack.com/api/chat.update'
            }
            URLConnection postHandler = new URL(apiEndpoint).openConnection()
            postHandler.setRequestMethod('POST')
            postHandler.setRequestProperty('Content-Type', 'application/json; charset=utf-8')
            postHandler.setRequestProperty('Authorization', 'Bearer ' + this.context.env.slackToken)
            postHandler.setDoOutput(true)
            String requestBody = new JsonBuilder(slackConfig)
            postHandler.getOutputStream().write(requestBody.getBytes('UTF-8'))
            String response = postHandler.getInputStream().getText()
            return new SlackResponse().parseResponse(response)
        }
    }

    SlackResponse getUserIdByEmail(String email) {

        this.context.withCredentials([
                this.context.string(credentialsId: 'slack', variable: 'slackToken')
        ]) {
            URLConnection getHandler = new URL("https://slack.com/api/users.lookupByEmail?email=${email}")
                    .openConnection()
            getHandler.setRequestMethod('GET')
            getHandler.setRequestProperty('Authorization', 'Bearer ' + this.context.env.slackToken)
            return new SlackResponse().parseResponse(getHandler.getInputStream().getText())
        }
    }

}
