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
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.Activator;

/**
 * @author Roberto E. Escobar
 */
public class TagProcessor {

   private static final Set<String> wordsToSkip;
   static {
      wordsToSkip = new HashSet<String>();
      Scanner scanner = null;
      try {
         URL url = Activator.getInstance().getContext().getBundle().getResource("/support/wordsToSkip.txt");
         scanner = new Scanner(url.openStream(), "UTF-8");
         while (scanner.hasNext()) {
            wordsToSkip.add(scanner.next());
         }
      } catch (Exception ex) {
         OseeLog.log(TagProcessor.class, Level.SEVERE, "Unable to process word skip file.", ex);
      } finally {
         if (scanner != null) {
            scanner.close();
         }
      }
   }

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
      if (Strings.isValid(original) && (original.length() >= 2 || 0 == WordsUtil.countPuntuation(original))) {
         original = original.toLowerCase();
         for (String toEncode : WordsUtil.splitOnPunctuation(original)) {
            if (wordsToSkip.contains(toEncode) != true) {
               String target = WordsUtil.toSingular(WordsUtil.stripPossesive(toEncode));
               TagEncoder.encode(target, tagCollector);
            }
         }
      }
   }
}
