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

import os, re, sys

def isBinaryFile(fileName: str):
    return re.search("(png|jpeg|jpg|gif|bmp|BMP|jar|wav|zip|pdf|ppt|pptx|xlsx|swf|pot)$", fileName) is not None

def ignoreDirectory(dirPath: str):
    ignoreDirs = [
        "node_modules", 
        ".git",
        "\\bin\\",
        "org.eclipse.osee.icteam.ui",
        "org.eclipse.osee.web\dist", 
        "org.eclipse.osee.web\.angular\cache",
        "OSEE-INF\spellCheck"
    ]

    for dir in ignoreDirs:
        if dirPath.find(dir) != -1:
            return True
    return False

def getDistStatementExclusionsRegex():
    noDist = ""
    with open(os.path.join(sys.path[0], "./no_dist_statement.txt"), "r") as f:
        lines = f.readlines()
        noDist = "("
        for line in lines:
            noDist += line.strip() + "|"
        noDist = noDist[:-1] + ")$"
        noDist = noDist.replace("\\", "\\\\")
        noDist = noDist.replace(".", "\.")
    return noDist

def isDistStatementRequired(fileName: str, exclusionsRegex: str):
    return re.search("(java|html|js|ts|sass|scss|py)$", fileName) is not None and re.search(exclusionsRegex, fileName) is None

def containsCopyright(line: str):
    return line.find("Copyright (c)") > -1

def createKeywordRegex(keywords):
    # create a regex pattern like "(keyword1|keyword2|keyword3)/i"
    regexPattern = "("
    for keyword in keywords.split(';'):
        regexPattern += keyword + "|"
    regexPattern = regexPattern[:-1] + ")"
    return regexPattern

def searchTextIgnoreCase(regexPattern: str, text: str):
    return re.search(regexPattern, text, re.IGNORECASE) if regexPattern != "()" else None