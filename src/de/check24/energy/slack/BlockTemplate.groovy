package de.check24.energy.slack

class BlockTemplate {

    Block buildTemplate = new Block([
        [
            'type': 'header',
            'text': [
                'type': 'plain_text',
                'text': 'Release {{ BUILD_NUMBER }} {{ VERSION }}'
            ]
        ],
        [
            'type': 'section',
            'fields': [
                [
                    'type': 'mrkdwn',
                    'text': '*Status:*\n {{ STATUS }}'
                ],
                [
                    'type': 'mrkdwn',
                    'text': ' \n '
                ],
                [
                    'type': 'mrkdwn',
                    'text': '*Started By:*\n {{ BUILDER_NAME }}'
                ],
                [
                    'type': 'mrkdwn',
                    'text': '*Tester:*\n {{ TESTER_NAME }}'
                ]
            ]
        ]
    ])

    static def prepare(buildNumber, buildTag, status, job, triggeredBy, env, gitContext, gitFrom, gitTo) {

        String diffLink = 'https://bitbucket.org/check24/' + gitContext.repoName +
                "/branches/compare/${gitFrom}%0D${gitTo}"


        def result = [
                [
                        "type": "header",
                        "text": [
                                "type": "plain_text",
                                "text": ":c24heartbeat: Release ${buildTag} (${buildNumber})",
                                "emoji": true
                        ]
                ],
                [
                        "type": "context",
                        "elements": [
                                [
                                        "type": "mrkdwn",
                                        "text": "*▷ Triggered by ${triggeredBy}*"
                                ]
                        ]
                ],
                [
                        "type": "section",
                        "fields": [
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Tag:*\n> ${buildTag}"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Job:*\n> *${job}*"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁ Environment:*\n>${env}"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Status:*\n> ${status}"
                                ]
                        ]
                ],
                [
                        "type": "divider"
                ]]

        ArrayList issues = []

        if (gitContext.commits.size()) {
            gitContext.issues.each { issue ->
                if (issue.hasIssue) {
                    issues.push(getIssueButton(issue.title.toString(), issue.link.toString()))
                }
            }

            result.add(
                    [
                            "type": "context",
                            "elements": [
                                    [
                                            "type": "mrkdwn",
                                            "text": ":jira:   *Tickets queued for release.*"
                                    ]
                            ]
                    ],
            )

            result.add(
                    [
                            type    : 'actions',
                            elements: issues
                    ]
            )

            result.addAll([
                    [
                            "type": "divider"
                    ],
                    [
                            "type"    : "context",
                            "elements": [
                                    [
                                            "type": "mrkdwn",
                                            "text": "*_Last Commits_*"
                                    ]
                            ]
                    ]])

            gitContext.commits.each { commit ->
                result.add([
                        "type": "context",
                        "elements": [
                                [
                                        "type": "mrkdwn",
                                        "text": "> <https://bitbucket.org/${gitContext.ownerName}/${gitContext.repoName}/commit/${commit.hash}|${commit.commitTime}> \n> Author | ${commit.authorName} \n> *${commit.messageTitle}*"
                                ]
                        ]
                ])
            }

            result.add(
                    [
                            "type"    : "actions",
                            "elements": [
                                    [
                                            type: "button",
                                            text: [
                                                  type: "plain_text",
                                                  emoji: true,
                                                  text: ":merge:  Git diff"
                                            ],
                                            style: "primary",
                                            url: diffLink
                                    ]
                            ]
                    ])

        }

        return new Block(result);
    }

    private static Map getIssueButton(
            String issueName,
            String issueLink
    ) {
        return [
                "type": "button",
                "text": [
                        "type": "plain_text",
                        "emoji": true,
                        "text": ":jira2: ${escapeString(issueName)}",
                ],
                'style': 'primary',
                "url": "${escapeString(issueLink)}"
        ]
    }

    private static String escapeString(String rawString) {
        return rawString ? rawString.replaceAll(/('|")/, /\\$0/) : rawString
    }


    Map issueMessage = [
        'type': 'button',
        'text': [
            'type': 'plain_text',
            'emoji': true,
            'text': ':jira2: {{ ISSUE_NAME }}'
        ],
        'style': 'primary',
        'url': '{{ ISSUE_LINK }}'
    ]

}
