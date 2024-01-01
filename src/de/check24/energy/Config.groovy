package de.check24.energy
import groovy.transform.CompileStatic

@CompileStatic
class Config {

    public final static Map NODES = [
            'Web': [
                    'w01.telko.intern',
            ],
            'Admin': [
                    'a01.telko.intern',
            ]
    ]

    private final static List DEFAULTSSHKEYS = [ 'bitbucket', 'deploy' ]

    private final static Map CHANNEL = [
            'internet-it-pm': 'C031UXXXXJ1',
    ]
    public final static Map PROJECT = [
            'deploy.dev': [
                    'Slack': [
                            'Icon': ':toolbox:',
                            'Channel': CHANNEL['energie-f2-monitoring'],
                            'User': 'Development Deploy Release'
                    ],
                    'Deploy': [
                            'Type': 'RENAME',
                            'User': 'pu_test_deploy',
                            'DirBase': 'deploy.dev',
                            'Nodes': [
                                    NODES['web']
                            ]
                    ],
                    'SSH-Credentials': DEFAULTSSHKEYS
            ],
            'deploy.staging': [
                    'Slack': [
                            'Icon': ':toolbox:',
                            'Channel': CHANNEL['energie-f2-monitoring'],
                            'User': 'Staging Deploy Release'
                    ],
                    ' ': [
                            'Type': 'RENAME',
                            'User': 'pu_test_deploy',
                            'DirBase': 'deploy.staging',
                            'Nodes': [
                                    NODES['web']
                            ]
                    ],
                    'SSH-Credentials': DEFAULTSSHKEYS
            ]
    ]
}