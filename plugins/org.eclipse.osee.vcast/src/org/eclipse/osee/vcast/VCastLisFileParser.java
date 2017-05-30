/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Shawn F. Cook
 */
public class VCastLisFileParser {
   private static final Pattern sourceLinePattern = Pattern.compile("^[0-9]+ [0-9]+(.*?)$");
   private static final Pattern exceptionPattern = Pattern.compile("^\\s+EXCEPTION\\s*$");
   private static final Pattern endMethodPattern = Pattern.compile("^\\s*END\\s+(.*);\\s*$");

   private final File lisFile;
   private String fileText = null;
   private String[] lisFileLines;

   public VCastLisFileParser(File lisFile) {
      this.lisFile = lisFile;
   }

   public Pair<String, Boolean> getSourceCodeForLine(Integer method, Integer executionLine) {
      String startsWith = method + " " + executionLine + " ";
      boolean exceptionLine = false;
      for (String line : lisFileLines) {
         if (line.startsWith(startsWith)) {
            Matcher m = sourceLinePattern.matcher(line);
            String lineCode = "";
            if (m.find()) {
               lineCode = m.group(1);
            } else {
               lineCode = "Error parsing *.LIS file";
            }
            return new Pair<String, Boolean>(lineCode, exceptionLine);
         }
         Matcher m = exceptionPattern.matcher(line);
         if (m.find()) {
            exceptionLine = true;
         } else {
            m = endMethodPattern.matcher(line);
            if (m.find()) {
               exceptionLine = false;
            }
         }
      }
      return null;
   }

   public void loadFileText() throws IOException {
      fileText = Lib.fileToString(lisFile);
      lisFileLines = fileText.split("\n");
   }
}