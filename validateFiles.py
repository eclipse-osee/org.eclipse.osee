#!/usr/bin/python

import sys, re
from os.path import exists

# argv[1] should be the keywords string, argv[2:] should be the list of changed file paths
def main(argv):
    keywords = argv[1]
    files = argv[2:]
    findings = []

    # create a regex pattern like "(keyword1|keyword2|keyword3)/i"
    regexPattern = "("
    for keyword in keywords.split(';'):
        regexPattern += keyword + "|"
    regexPattern = regexPattern[:-1] + ")"

    for file in files:
        if exists(file):
            fileNameResults = re.search(regexPattern, file, re.IGNORECASE)
            if fileNameResults != None:
                findings.append("Found keyword \"" + fileNameResults[0] + "\" in file name of " + file)
            # Do not check contents of binary files such as images
            if re.search("(png|jpeg|jpg|gif)$", file) is None:
                with open(file, 'r') as f:
                    lines = f.readlines()
                    requireDistStatement = re.search("(java|html|js|ts|sass|scss)$", file) is not None
                    distStatementFound = False
                    for line in lines:
                        if requireDistStatement and line.find("* Copyright") > -1:
                            distStatementFound = True
                        searchResults = re.search(regexPattern, line, re.IGNORECASE)
                        if searchResults != None:
                            findings.append("Found keyword \"" + searchResults[0] + "\" on line " + str(lines.index(line)) + " of " + file + "\n  -->  " + line)
                    if requireDistStatement and not distStatementFound:
                        findings.append("No distribution statement in " + file)
        else:
            print("File not found or was deleted: " + file)

    if len(findings) > 0:
        for finding in findings:
            print(finding)
        sys.exit(-1)

    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv)
