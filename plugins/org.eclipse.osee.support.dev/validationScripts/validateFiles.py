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

import sys
from os.path import exists
from validationUtils import *

# argv[1] should be the keywords string, argv[2:] should be the list of changed file paths
def main(argv):
    keywords = argv[1]
    files = argv[2:]
    findings = []

    regexPattern = createKeywordRegex(keywords)
    exclusionsRegex = getDistStatementExclusionsRegex()

    for file in files:
        if not exists(file):
            print("File not found or was deleted: " + file)
            continue
        fileNameResults = searchTextIgnoreCase(regexPattern, file)
        if fileNameResults != None:
            findings.append("Found keyword \"" + fileNameResults[0] + "\" in file name of " + file)
        # Do not check contents of binary files such as images
        try:
            with open(file, 'r') as f:
                lines = f.readlines()
                requireDistStatement = isDistStatementRequired(file, exclusionsRegex)
                distStatementFound = False
                for line in lines:
                    if requireDistStatement and containsCopyright(line):
                        distStatementFound = True
                    searchResults = searchTextIgnoreCase(regexPattern, line)
                    if searchResults != None:
                        findings.append("Found keyword \"" + searchResults[0] + "\" on line " + str(lines.index(line)) + " of " + file + "\n  -->  " + line)
                if requireDistStatement and not distStatementFound:
                    findings.append("No distribution statement in " + file)
        except UnicodeDecodeError:
            findings.append("Non-text data found in " + file)
            continue

    if len(findings) > 0:
        for finding in findings:
            print(finding)
        sys.exit(-1)

    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv)
