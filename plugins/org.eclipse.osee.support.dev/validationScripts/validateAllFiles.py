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

import sys, os
from os.path import exists
from validationUtils import *

# argv[1] should be the keywords string
def main(argv):
    keywords = argv[1]
    findings = [str]

    regexPattern = createKeywordRegex(keywords)
    exclusionsRegex = getDistStatementExclusionsRegex()

    for (dirpath, dirnames, filenames) in os.walk(".\\"):
        if ignoreDirectory(dirpath):
            continue
        for file in filenames:
            fullPath = dirpath + "\\" + file
            fileNameResults = searchTextIgnoreCase(regexPattern, file)
            if fileNameResults != None:
                findings.append("Found keyword \"" + fileNameResults[0] + "\" in file name of " + file + " (" + fullPath + ")\n")
            if file.find(".class") == -1 and exists(fullPath) and not isBinaryFile(file):
                with open(fullPath, 'r', errors="ignore") as f:
                    lines = f.readlines()
                    requireDistStatement = isDistStatementRequired(fullPath, exclusionsRegex)
                    distStatementFound = False
                    for line in lines:
                        if requireDistStatement and containsCopyright(line):
                            distStatementFound = True
                        searchResults = searchTextIgnoreCase(regexPattern, line)
                        if searchResults != None:
                            findings.append("Found keyword \"" + searchResults[0] + "\" on line " + str(lines.index(line)) + " of " + file + " (" + fullPath + ")" + "\n  -->  " + line)
                    if requireDistStatement and not distStatementFound:
                        findings.append("No distribution statement in " + file + " (" + fullPath + ")\n")

    if len(findings) > 0:
        for finding in findings:
            print(finding)
            print()
        sys.exit(-1)

    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv)
