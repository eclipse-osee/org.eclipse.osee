/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.rest.importing.parsers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class RequirementTraceTableParser {
   private static Pattern wtrPattern = Pattern.compile("<w:tr .*?>(.*?)</w:tr>");
   private static Pattern wtcPattern = Pattern.compile("<w:tc>(.*?)</w:tc>");
   private static Pattern wpPattern = Pattern.compile("<w:p .*?>(.*?)</w:p>");
   private static Pattern wtPattern = Pattern.compile("<w:t>(.*?)</w:t>");
   private final XResultData results;

   private final LinkedList<String>[] reqtsArray;
   private final Integer numColumns;
   LinkedList<Pair<String, String>> reqtsTraces = new LinkedList<>();

   @SuppressWarnings("unchecked")
   public RequirementTraceTableParser(Integer numColumns, XResultData results) {
      this.numColumns = numColumns;
      this.reqtsArray = new LinkedList[numColumns];
      for (int i = 0; i < numColumns; i++) {
         reqtsArray[i] = new LinkedList<String>();
      }
      this.results = results;
   }

   public String outputTraces() throws IOException {
      String fileLocation = "N/A";
      if (!reqtsTraces.isEmpty()) {
         Date d = new Date();
         String tempDir = System.getProperty("java.io.tmpdir");
         String fileName = d.toString().replaceAll("[\\s:]", "_") + ".txt";
         File output = new File(tempDir, fileName);
         StringBuilder generated = new StringBuilder();
         for (Pair<String, String> item : reqtsTraces) {
            generated.append(String.format("%s<-->%s", item.getFirst(), item.getSecond()));
            generated.append("\r\n");
         }
         Lib.writeStringToFile(generated.toString(), output);
         fileLocation = output.getPath();
      }
      return fileLocation;
   }

   public Collection<Pair<String, String>> getTraces() {
      return reqtsTraces;
   }

   public void handleAppendixATable(String content) {
      Matcher m = wtrPattern.matcher(content);
      boolean first = true;
      while (m.find()) {
         if (!first) {
            String wtrContent = m.group(1);
            handleAppendixAContent(wtrContent);
            buildRelations();
            clearValues();
         }
         first = false;
      }
   }

   private void handleAppendixAContent(String content) {
      Matcher m = wtcPattern.matcher(content);
      int count = 0;
      while (m.find()) {
         String wtcContent = m.group(1);
         handleAppendixAParagraph(wtcContent, count);
         ++count;
      }
   }

   private void handleAppendixAParagraph(String content, int level) {
      Matcher m = wpPattern.matcher(content);
      while (m.find()) {
         String wpContent = m.group(1);
         handleAppendixAItem(wpContent, level);
      }
   }

   private void handleAppendixAItem(String content, int level) {
      Matcher m = wtPattern.matcher(content);
      while (m.find()) {
         String wtContent = m.group(1);
         if (Strings.isValid(wtContent)) {
            addToLevel(wtContent, level);
         }
      }
   }

   private void addToLevel(String item, int level) {
      if (level > -1 && level < numColumns) {
         reqtsArray[level].add(item);
         return;
      }
      throw new OseeCoreException("trace level in trace table is invald");
   }

   private void buildRelations() {
      for (int i = 0; i < numColumns - 1; ++i) {
         for (String parent : reqtsArray[i]) {
            for (String child : reqtsArray[i + 1]) {
               if (Strings.isValid(parent) && Strings.isValid(child)) {
                  // some tables contain all of the reqts separated by commas
                  if (child.contains(",")) {
                     String[] elements = child.split("\\s*,\\s*");
                     for (String element : elements) {
                        if (Strings.isValid(element)) {
                           reqtsTraces.add(new Pair<String, String>(parent, element));
                        }
                     }
                  } else {
                     reqtsTraces.add(new Pair<String, String>(parent, child));
                  }
               }
            }
         }
      }
   }

   private void clearValues() {
      for (int i = 0; i < numColumns; ++i) {
         reqtsArray[i].clear();
      }
   }
}
