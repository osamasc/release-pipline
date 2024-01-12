package de.check24.energy.slack

import groovy.json.JsonSlurper

/**
 * @author Osama Ahmed <osama.ahmed@check24.de>
 * @copyright Check24 Vergleichsportal Energie GmbH
 */
class SlackResponse extends Expando {

    SlackResponse parseResponse(String responseAsString) {
        new JsonSlurper().parseText(responseAsString).each { key, val ->
            this[key] = val
        }
        this['raw'] = responseAsString
        return this
    }

}
