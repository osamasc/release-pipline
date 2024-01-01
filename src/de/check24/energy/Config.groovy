package de.check24.energy
import groovy.transform.CompileStatic

@CompileStatic
class Config {

    private final static List DEFAULTSSHKEYS = [ 'bitbucket', 'deploy' ]

    private final static Map CHANNEL = [
            'energie-f2-monitoring': 'C031UXXXXJ1',
    ]
    public final static Map PROJECT = [
            'deploy.dev': [
                    'Slack': [
                            'Icon': ':toolbox:',
                            'Channel': CHANNEL['energie-f2-monitoring'],
                            'User': 'Development Deploy Release'
                    ],
                    'SSH-Credentials': DEFAULTSSHKEYS
            ],
            'deploy.staging': [
                    'Slack': [
                            'Icon': ':toolbox:',
                            'Channel': CHANNEL['energie-f2-monitoring'],
                            'User': 'Staging Deploy Release'
                    ],
                    'SSH-Credentials': DEFAULTSSHKEYS
            ]
    ]
}