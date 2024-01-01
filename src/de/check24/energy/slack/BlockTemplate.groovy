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

    static def prepare(buildNumber, buildTag, status, job, triggeredBy, env, tickets) {
        def tmp = [
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
                                        "text": "*⌁Job:*\n> ${job}*"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁ Environment:*\n>${env}"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Status:*\n> ${status} (blue/green) :approve:"
                                ]
                        ]
                ],
                [
                        "type": "divider"
                ]]

        if (tickets) {
            tmp.addAll([
                    "type": "context",
                    "elements": [
                            [
                                    "type": "mrkdwn",
                                    "text": ":jira:   *Tickets queued for release.*"
                            ]
                    ]
            ],
            )
        }

        return new Block(tmp);
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
