#!/usr/bin/python

import sys, re

# argv[1] should be the keywords string, argv[2:] should be the list of changed file paths
def main(argv):
    keywords = argv[1]
    files = argv[2:]

    # create a regex pattern like "(keyword1|keyword2|keyword3)/i"
    regexPattern = "("
    for keyword in keywords.split(';'):
        regexPattern += keyword + "|"
    regexPattern = regexPattern[:-1] + ")"

    for file in files:
        with open(file, 'r') as f:
            lines = f.readlines()
            requireDistStatement = re.search("(java|html|js|ts|sass|scss)$", file) is not None
            distStatementFound = False
            for line in lines:
                if requireDistStatement and line.find("* Copyright") > -1:
                    distStatementFound = True
                if re.search(regexPattern, line, re.IGNORECASE) != None:
                    print("Found keyword on line " + str(lines.index(line)) + " of " + file)
                    sys.exit(-1)
            if requireDistStatement and not distStatementFound:
                print("No distribution statement in " + file)
                sys.exit(-1)

    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv)
