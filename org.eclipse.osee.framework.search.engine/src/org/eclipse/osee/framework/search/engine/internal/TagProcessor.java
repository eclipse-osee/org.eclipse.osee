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
package org.eclipse.osee.framework.search.engine.internal;

import java.io.InputStream;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagEncoder;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

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
      Scanner scanner = new Scanner(inputStream, "UTF-8");
      while (scanner.hasNext()) {
         processWord(scanner.next(), tagCollector);
      }
   }

   public static void collectFromScanner(Scanner sourceScanner, ITagCollector tagCollector) {
      while (sourceScanner.hasNext()) {
         Scanner innerScanner = new Scanner(sourceScanner.next());
         while (innerScanner.hasNext()) {
            processWord(innerScanner.next(), tagCollector);
         }
      }
   }

   private static void processWord(String original, ITagCollector tagCollector) {
      boolean originalStored = false;
      if (Strings.isValid(original)) {
         original = original.toLowerCase();
         for (String toEncode : WordsUtil.splitOnPunctuation(original)) {
            String target = WordsUtil.toSingular(WordsUtil.stripPossesive(toEncode));
            if (target.equals(original)) {
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
