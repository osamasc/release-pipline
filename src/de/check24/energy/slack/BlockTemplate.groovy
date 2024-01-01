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
                        'type': 'header',
                        'text': [
                                'type': 'plain_text',
                                'text': "Release ${buildNumber}  ${buildTag}"
                        ]
                ],
                [
                        'type': 'section',
                        'fields': [
                                [
                                        'type': 'mrkdwn',
                                        'text': "*Status:*\n ${status}"
                                ],
                                [
                                        'type': 'mrkdwn',
                                        'text': ' \n '
                                ],
                                [
                                        'type': 'mrkdwn',
                                        'text': "*Triggered By:*\n ${triggeredBy}"
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
