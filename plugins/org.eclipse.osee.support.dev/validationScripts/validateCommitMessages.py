#!/usr/bin/python
#####################################################################
# Copyright (c) 2023 Boeing
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Boeing - initial API and implementation
#####################################################################

from subprocess import check_output
import sys, json
from validationUtils import *

# argv[1] should be the keywords string, argv[2] should be the gitlab api url for merge request commits, argv[3] should be the gitlab api token
def main(argv):
    keywords = argv[1]
    api_url = argv[2]
    api_token = argv[3]
    findings = []

    regexPattern = createKeywordRegex(keywords)

    commitsJson = check_output(["curl", "-H", "PRIVATE-TOKEN: " + api_token, api_url])
    commits = json.loads(commitsJson)

    for commit in commits:
        message = commit['message']
        for line in message.split('\n'):
            # Ignore empty lines and change ids
            if line == '':
                continue
            if len(line) > 10 and line[0:10] == 'Change-Id:':
                continue
            searchResults = searchTextIgnoreCase(regexPattern, line)
            if searchResults != None:
                findings.append("Found keyword \"" + searchResults[0] + "\" in commit message: " + "\n  -->  " + line)
            
    if len(findings) > 0:
        print("\n\n\n")
        print("--- FINDINGS ---")
        for finding in findings:
            print(finding)
        sys.exit(-1)

    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv)
