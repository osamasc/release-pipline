package de.check24.energy.slack

import java.time.*
import java.time.format.*

class BlockBuilder {

    static def prepare(buildNumber, buildTag, status, job, triggeredBy, env, gitContext, gitFrom, gitTo, buildUrl, buildDisplayName) {

        String diffLink = $/https://bitbucket.org/\${gitContext.ownerName}/\${gitContext.repoName}/branches/compare/\${gitFrom}%0D${gitTo}/$
        String tagLink = $/https://bitbucket.org/\${gitContext.ownerName}/\${gitContext.repoName}/commits/tag/\${buildTag}/$

        def result = [
                [type: 'header', text: [type: "plain_text", text: ":c24heartbeat: Release ${buildTag} (${buildNumber})", emoji: true]],
                [type: "context", elements: [[type: "mrkdwn", text: "*▷ Triggered by ${triggeredBy}*"]]],
                [type: "section", fields: [
                    [type: "mrkdwn", text: "*⌁Tag:*\n> <${tagLink}|${buildTag}>"],
                    [type: "mrkdwn", text: "*⌁Job:*\n> *${buildDisplayName}*"],
                    [type: "mrkdwn", text: "*⌁ Environment:*\n>${env}"],
                    [type: "mrkdwn", text: "*⌁Status:*\n> ${status}"]
                ]],
                [type: "divider"]
        ]

        ArrayList issues = []

        if (gitContext.commits.size()) {
            gitContext.issues.each { issue ->
                issue.properties.each { println "$it.key -> $it.value" }

                if (issue.hasIssue) {
                    issues.push(getIssueButton(issue.title.toString(), issue.link.toString()))
                }
            }

            result.add([type: 'context', elements: [[type: 'mrkdwn', text: ':jira: *Tickets queued for release.*']]])
            result.add([type: 'actions', elements: issues])
            result.add([type: 'divider'])
            result.add([type: 'context', elements: [[type: 'mrkdwn', text: '⌞ Latest Commits']]])

            gitContext.commits.each { commit ->
                String commitLink = "https://bitbucket.org/${gitContext.ownerName}/${gitContext.repoName}/commit/${commit.hash}"
                String commitDate = getRelativeDateFromNow(commit.commitTime)
                result.add([
                        type: 'context',
                        elements: [[
                            type: 'mrkdwn',
                            text: "> <${commitLink}|${commitDate} ⏌> \n> Author | ${commit.authorName} \n> *${commit.messageTitle}*"
                        ]]
                ])
            }

            result.add([type: 'actions', elements: [[type: 'button', text: [type: 'plain_text', emoji: true, text: ':merge:  Git diff'], style: 'primary', url: diffLink]]])

        }

        result.add([type: 'divider'])
        result.add([type: 'actions', elements: [
            [type: 'button', text: [type: 'plain_text', emoji: true, text: ':approve: activate'], style: 'primary', url: buildUrl],
            [type: 'button', text: [type: 'plain_text', emoji: true, text: ':needswork: Rollback'], style: 'primary', url: buildUrl]
        ]],
        )
        result.add([type: 'context', elements: [[type: 'mrkdwn', text: 'Powered by F2. :c24tick:']]])

        return new Block(result);
    }

    private static Map getIssueButton(
            String issueName,
            String issueLink
    ) {
        return [
                type: 'button',
                text: [
                        "type": 'plain_text',
                        "emoji": true,
                        "text": ":jira2: ${escapeString(issueName)}",
                ],
                style: 'primary',
                url: "${escapeString(issueLink)}"
        ]
    }

    private static String escapeString(String rawString) {
        return rawString ? rawString.replaceAll(/(['"])/, /\\$0/) : rawString
    }

    static String getRelativeDateFromNow(String dateString) {
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        def dateTime = LocalDateTime.parse(dateString, formatter)
        def date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())

        def now = new Date()
        def duration = Duration.between(date.toInstant(), now.toInstant())

        if (duration.seconds < 60) {
            return "just now"
        } else if (duration.toMinutes() < 60) {
            def minutes = duration.toMinutes()
            return "${minutes} minute${minutes > 1 ? 's' : ''} ago"
        } else if (duration.toHours() < 24) {
            def hours = duration.toHours()
            return "${hours} hour${hours > 1 ? 's' : ''} ago"
        } else {
            def days = duration.toDays()
            return "${days} day${days > 1 ? 's' : ''} ago"
        }
    }
}
