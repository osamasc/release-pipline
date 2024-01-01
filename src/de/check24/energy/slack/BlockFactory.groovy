package de.check24.energy.slack

/**
 * @author Osama Ahmed <osama.ahmed@check24.de>
 * @copyright Check24 Vergleichsportal Energie GmbH
 */
class BlockFactory {

    Block getBuildMessage(
            String projectName,
            String buildNumber,
            String builderName,
            Integer commitCount,
            String version,
            String status
    ) {
        Block result = new BlockTemplate().buildTemplate
        result[0].text.text = result[0].text.text.replace('{{ BUILD_NUMBER }}', buildNumber.toString())
        String checkedVersion = version ? '(' + version.toString() + ')' : ''
        result[0].text.text = result[0].text.text.replace('{{ VERSION }}', escapeString(checkedVersion))
        result[1].fields[0].text = result[1].fields[0].text.replace('{{ STATUS }}', escapeString(status))
        result[1].fields[2].text = result[1].fields[2].text.replace('{{ BUILDER_NAME }}', escapeString(builderName))
        result[1].fields[3].text = result[1].fields[3].text.replace('{{ TESTER_NAME }}', escapeString('@osama.ahmed'))

        return result
    }

    Block getIssuesMessage(
            Serializable gitContext,
            String repoAddress,
            String productionCommit,
            String masterCommit
    ) {
        ArrayList issues = []
        String repoName = repoAddress.split('/')[1].split('.git')[0]
        String repoLink = 'https://bitbucket.org/check24/' +
                repoName +
                "/branches/compare/${masterCommit}%0D${productionCommit}"
        LinkedHashMap bitbucketButton = [
                'type': 'button',
                'text': [
                        'type' : 'plain_text',
                        'emoji': true,
                        'text' : ':bitbucket2: Git Diff'
                ],
                'url' : repoLink
        ]
        issues.push(bitbucketButton)
        gitContext.issues.each { issue ->
            if (issue.hasIssue) {
                issues.push(this.getIssueButton(issue.title.toString(), issue.link.toString()))
            }
        }

        println issue

        Block block = new Block([
                [
                        type    : 'actions',
                        elements: issues
                ]
        ])

        return block
    }

    private Map getIssueButton(
            String issueName,
            String issueLink
    ) {
        LinkedHashMap result = new BlockTemplate().issueMessage
        result.text.text = result.text.text.replace('{{ ISSUE_NAME }}', escapeString(issueName))
        result.url = escapeString(issueLink)

        return result
    }

    private String escapeString(String rawString) {
        return rawString ? rawString.replaceAll(/('|")/, /\\$0/) : rawString
    }

}
