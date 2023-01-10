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

import sys, re, os
from os.path import exists
from validationUtils import *

javaStatement = "/*********************************************************************\n * Copyright (c) 2023 Boeing\n *\n * This program and the accompanying materials are made\n * available under the terms of the Eclipse Public License 2.0\n * which is available at https://www.eclipse.org/legal/epl-2.0/\n *\n * SPDX-License-Identifier: EPL-2.0\n *\n * Contributors:\n *     Boeing - initial API and implementation\n **********************************************************************/"
jsStatement = "/*********************************************************************\n* Copyright (c) 2023 Boeing\n*\n* This program and the accompanying materials are made\n* available under the terms of the Eclipse Public License 2.0\n* which is available at https://www.eclipse.org/legal/epl-2.0/\n*\n* SPDX-License-Identifier: EPL-2.0\n*\n* Contributors:\n*     Boeing - initial API and implementation\n**********************************************************************/"
htmlStatement = "<!--\n * Copyright (c) 2023 Boeing\n *\n * This program and the accompanying materials are made\n * available under the terms of the Eclipse Public License 2.0\n * which is available at https://www.eclipse.org/legal/epl-2.0/\n *\n * SPDX-License-Identifier: EPL-2.0\n *\n * Contributors:\n *     Boeing - initial API and implementation\n -->"

# argv[1] should be the keywords string
def main(argv):
    javaFiles = []
    jsFiles = []
    htmlFiles = []

    for (dirpath, dirnames, filenames) in os.walk("./"):
        if ignoreDirectory(dirpath):
            continue
        for file in filenames:
            fullPath = dirpath + "/" + file
            if file.find(".class") == -1 and exists(fullPath) and not isBinaryFile(file):
                with open(fullPath, 'r', encoding="utf-8", errors="ignore") as f:
                    lines = f.readlines()
                    if isDistStatementRequired(file):
                        distStatementFound = False
                        for line in lines:
                            if containsCopyright(line):
                                distStatementFound = True
                                break
                        if not distStatementFound:
                            if re.search("java$", file) is not None:
                                javaFiles.append(fullPath)
                            elif re.search("\.(js|ts)$", file) is not None:
                                jsFiles.append(fullPath)
                            elif re.search("\.(html|htm)$", file) is not None:
                                htmlFiles.append(fullPath)

    for file in javaFiles:
        print(file)
        with open(file, "r+") as f:
            lines = f.readlines()
            f.seek(0, 0)
            f.write(javaStatement + "\n")
            passedDist = False
            for line in lines:
                # Remove placeholder dist statements
                if not (line.startswith("/*") or line.startswith(" *")):
                    passedDist = True
                if passedDist:
                    f.write(line)
            f.truncate()

    for file in jsFiles:
        print(file)
        with open(file, "r+") as f:
            content = f.read()
            f.seek(0, 0)
            f.write(jsStatement + "\n" + content)

    for file in htmlFiles:
        print(file)
        with open(file, "r+") as f:
            content = f.read()
            f.seek(0, 0)
            f.write(htmlStatement + "\n" + content)

if __name__ == "__main__":
    main(sys.argv)
