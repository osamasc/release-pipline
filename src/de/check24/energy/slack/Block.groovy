package de.check24.energy.slack

/**
 * @author Osama Ahmed <osama.ahmed@check24.de>
 * @copyright Check24 Vergleichsportal Energie GmbH
 */
class Block extends ArrayList<LinkedHashMap<Object, Object>> {

    private static final long serialVersionUID = -1145916367616491793L

    Block(List<LinkedHashMap<Object, Object>> blocks) {
        this.addAll(blocks)
    }
}
