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

    static def prepare(buildNumber, buildTag, status, triggeredBy) {
        return new Block([
                [
                        "type": "header",
                        "text": [
                                "type": "plain_text",
                                "text": ":c24heartbeat: New release in progress",
                                "emoji": true
                        ]
                ],
                [
                        "type": "context",
                        "elements": [
                                [
                                        "type": "mrkdwn",
                                        "text": "*▷ Triggerd by @Osama Ahmed*"
                                ]
                        ]
                ],
                [
                        "type": "section",
                        "fields": [
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Tag:*\n> r20230915-105717"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Job:*\n> *messagesystem*"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*✎ Environment:*\n>Production"
                                ],
                                [
                                        "type": "mrkdwn",
                                        "text": "*⌁Status:*\n> Active (blue/green) :approve:"
                                ]
                        ]
                ],
                [
                        "type": "divider"
                ],
                [
                        "type": "context",
                        "elements": [
                                [
                                        "type": "mrkdwn",
                                        "text": ":jira:   *Tickets queued for release.*"
                                ]
                        ]
                ],
                [
                        "type": "actions",
                        "elements": [
                                [
                                        "type": "button",
                                        "text": [
                                                "type": "plain_text",
                                                "emoji": true,
                                                "text": ":jira2: FEATII-879"
                                        ],
                                        "value": "click_me_123"
                                ],
                                [
                                        "type": "button",
                                        "text": [
                                                "type": "plain_text",
                                                "emoji": true,
                                                "text": ":jira2: FEATII-880"
                                        ],
                                        "value": "click_me_123"
                                ],
                                // ... (other button elements)
                                [
                                        "type": "button",
                                        "text": [
                                                "type": "plain_text",
                                                "emoji": true,
                                                "text": ":approve:   Activate"
                                        ],
                                        "style": "primary",
                                        "value": "click_me_123"
                                ],
                                [
                                        "type": "button",
                                        "text": [
                                                "type": "plain_text",
                                                "emoji": true,
                                                "text": ":needswork:  Rollback"
                                        ],
                                        "style": "primary",
                                        "value": "click_me_123"
                                ]
                        ]
                ],
                [
                        "type": "divider"
                ],
                [
                        "type": "context",
                        "elements": [
                                [
                                        "type": "mrkdwn",
                                        "text": "*▷ Powered by F2. :c24tick:*"
                                ]
                        ]
                ]
                ])
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
