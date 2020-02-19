/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * Convert all OSEE Types hex to long in .java and .osee files
 *
 * @author Donald G. Dunne
 */
public class ConvertOseeTypesToLong {

   public static void main(String[] args) {
      try {
         XResultData results = new XResultData();
         for (String dirNam : Arrays.asList(
            "C:\\UserData\\git_fix\\org.eclipse.osee\\plugins\\org.eclipse.osee.ats.api\\")) {
            File dir1 = new File(dirNam);
            for (String filename : Lib.readListFromDir(dir1, new MatchFilter(".*"), true)) {
               if (!Strings.isValid(filename)) {
                  continue;
               }
               XConsoleLogger.err(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               try {
                  recurseAndFind(file, results);
               } catch (Exception ex) {
                  XConsoleLogger.err(ex.getLocalizedMessage());
               }
            }
         }

         String outputFilename = "C:\\UserData\\ConvertOseeTypesToLong.txt";
         Lib.writeStringToFile(results.toString(), new File(outputFilename));

      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }
   static Pattern oseeUuidPattern = Pattern.compile("uuid +(0x.*)");

   private static void recurseAndFind(File file, XResultData results) throws IOException {
      try {
         if (file.isDirectory()) {
            for (String filename : Lib.readListFromDir(file, new MatchFilter(".*"), true)) {
               File childFile = new File(file.getAbsolutePath() + "\\" + filename);
               try {
                  recurseAndFind(childFile, results);
               } catch (Exception ex) {
                  XConsoleLogger.err(ex.getLocalizedMessage());
               }
            }
         }
      } catch (Exception ex) {
         XConsoleLogger.err(ex.getLocalizedMessage());
      }
      if (file.getAbsolutePath().endsWith(".java")) {
         //         results.log("Converting java file " + file);
         String text = Lib.fileToString(file);

         for (String createTypePrefix : Arrays.asList("createType\\(", "AttributeTypeToken.valueOf\\(",
            "ArtifactTypeToken.valueOf\\(", "RelationTypeSide.create\\(RelationSide.SIDE_A, ")) {
            Pattern javaTypePattern = Pattern.compile(createTypePrefix + "(.*)L,");
            Matcher matcher = javaTypePattern.matcher(text);
            Map<String, Long> hexToLong = getHextToLong(matcher);
            if (!hexToLong.isEmpty()) {
               for (Entry<String, Long> entry : hexToLong.entrySet()) {
                  String hex = entry.getKey();
                  Long id = entry.getValue();
                  if (id > Integer.MAX_VALUE) {
                     text = text.replaceAll(createTypePrefix + hex + "L,", createTypePrefix + id + "L,");
                  } else {
                     text = text.replaceAll(createTypePrefix + hex + "L,", createTypePrefix + id + ",");
                  }
               }
            }
            Lib.writeStringToFile(text, file);
         }

      } else if (file.getAbsolutePath().endsWith(".osee")) {
         XConsoleLogger.err("File " + file.getName());
         results.log("Converting osee types file " + file);
         String text = Lib.fileToString(file);
         Matcher matcher = oseeUuidPattern.matcher(text);
         Map<String, Long> hexToLong = getHextToLong(matcher);
         // some have 2 spaces between uuid and <hex>
         text = text.replaceAll("uuid  0x", "uuid 0x");
         for (Entry<String, Long> entry : hexToLong.entrySet()) {
            text = text.replaceAll("uuid " + entry.getKey(), "id " + entry.getValue().toString());
         }
         Lib.writeStringToFile(text, file);
      }
   }

   private static Map<String, Long> getHextToLong(Matcher matcher) {
      Map<String, Long> hexToLong = new HashMap<>();
      // can't change text while searching, so find them all first
      while (matcher.find()) {
         String hex = matcher.group(1);
         // don't convert twice
         if (hex.startsWith("0x")) {
            String hexStr = hex.replaceFirst("0x", "");
            hexStr = hexStr.replaceFirst("L", "");
            hexStr = hexStr.replaceAll(" ", "");
            long longVal = Long.parseUnsignedLong(hexStr, 16);
            XConsoleLogger.err(hex + "," + longVal);
            hexToLong.put(hex, longVal);
         }
      }
      return hexToLong;
   }
}
