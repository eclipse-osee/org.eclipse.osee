/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.search.engine.utility;

import java.io.InputStream;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TagProcessor {

   private TagProcessor() {
   }

   public static void collectFromString(String value, ITagCollector tagCollector) {
      if (value != null && value.length() > 0) {
         Scanner scanner = new Scanner(value);
         while (scanner.hasNext()) {
            processWord(scanner.next(), tagCollector);
         }
      }
   }

   public static void collectFromInputStream(InputStream inputStream, ITagCollector tagCollector) {
      if (inputStream != null) {
         Scanner scanner = new Scanner(inputStream, "UTF-8");
         while (scanner.hasNext()) {
            processWord(scanner.next(), tagCollector);
         }
      }
   }

   public static void collectFromScanner(Scanner sourceScanner, ITagCollector tagCollector) {
      try {
         while (sourceScanner.hasNext()) {
            String entry = sourceScanner.next();
            if (entry.length() > 0) {
               Scanner innerScanner = new Scanner(entry);
               while (innerScanner.hasNext()) {
                  String entry1 = innerScanner.next();
                  processWord(entry1, tagCollector);
               }
            }
         }
      } catch (Exception ex) {
         // Do nothing
      }
   }

   private static void processWord(String original, ITagCollector tagCollector) {
      boolean originalStored = false;
      if (Strings.isValid(original) && (original.length() >= 2 || 0 == WordsUtil.countPuntuation(original))) {
         original = original.toLowerCase();
         String toCheck =
               WordsUtil.endsWithPunctuation(original) ? original.substring(0, original.length() - 1) : original;
         for (String toEncode : WordsUtil.splitOnPunctuation(original)) {
            String target = WordsUtil.toSingular(WordsUtil.stripPossesive(toEncode));
            if (toEncode.equals(toCheck)) {
               originalStored = true;
            }
            TagEncoder.encode(target, tagCollector);
         }
         if (!originalStored) {
            TagEncoder.encode(original, tagCollector);
         }
      }
   }
}
