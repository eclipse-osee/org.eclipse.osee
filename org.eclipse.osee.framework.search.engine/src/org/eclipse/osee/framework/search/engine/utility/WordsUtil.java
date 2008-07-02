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

import java.net.URL;
import java.util.Properties;
import org.eclipse.osee.framework.search.engine.Activator;

/**
 * @author Roberto E. Escobar
 */
public class WordsUtil {

   private static final String VOWELS = "aeiou";
   private static final String IES_ENDING = "ies";
   private static final String OES_ENDING = "oes";
   private static final String ES_ENDING = "es";
   private static final String S_ENDING = "s";

   private static final Properties dictionary;
   static {
      dictionary = new Properties();
      try {
         URL url =
               Activator.getInstance().getContext().getBundle().getResource("/support/pluralToSingularExceptions.xml");
         dictionary.loadFromXML(url.openStream());
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static boolean hasConstantBeforeEnding(String word, String ending) {
      String remainder = word.substring(word.length() - (ending.length() + 1));
      return VOWELS.indexOf(remainder, 1) < 0;
   }

   private static boolean hasEitherSequenceBeforeEnding(String word, String ending, String... sequences) {
      boolean toReturn = false;
      String remainder = word.substring(0, word.length() - ending.length());
      for (String sequence : sequences) {
         toReturn |= remainder.endsWith(sequence);
      }
      return toReturn;
   }

   private static String replaceEndingWith(String word, String ending, String replaceWith) {
      return word.substring(0, word.length() - ending.length()) + replaceWith;
   }

   public static String stripPossesive(String original) {
      String toReturn = original;
      if (original.endsWith("'")) {
         toReturn = replaceEndingWith(original, "'", "");
      } else if (original.endsWith("'s")) {
         toReturn = replaceEndingWith(original, "'s", "");
      }
      return toReturn;
   }

   public static String[] splitOnPunctuation(String original) {
      return original.split("^[a-zA-Z0-9]");
   }

   public static String toSingular(String word) {
      word = word.toLowerCase();
      String toReturn = dictionary.getProperty(word);
      if (toReturn == null) {
         if (word.endsWith(IES_ENDING) && hasConstantBeforeEnding(word, IES_ENDING)) {
            toReturn = replaceEndingWith(word, IES_ENDING, "y");
         } else if (word.endsWith(OES_ENDING) && hasConstantBeforeEnding(word, OES_ENDING)) {
            toReturn = replaceEndingWith(word, OES_ENDING, "o");
         } else if (word.endsWith(ES_ENDING) && hasConstantBeforeEnding(word, ES_ENDING)) {
            String replaceWith = "e";
            String ending = ES_ENDING;
            if (hasEitherSequenceBeforeEnding(word, ES_ENDING, "ss", "sh", "ch")) {
               replaceWith = "";
            } else if (hasEitherSequenceBeforeEnding(word, ES_ENDING, "v")) {
               ending = "ves";
               replaceWith = "f";
            }
            toReturn = replaceEndingWith(word, ending, replaceWith);
         } else if (word.endsWith(S_ENDING)) {
            toReturn = word.substring(0, word.length() - 1);
         } else {
            toReturn = word;
         }
      }
      return toReturn;
   }

   //   public static void main(String[] args) {
   //      Map<String, String> testMap = new HashMap<String, String>();
   //      testMap.put("tries", "try");
   //      testMap.put("volcanoes", "volcano");
   //      testMap.put("geese", "goose");
   //      testMap.put("windows", "window");
   //      testMap.put("glasses", "glass");
   //      testMap.put("fishes", "fish");
   //      testMap.put("houses", "house");
   //      testMap.put("judges", "judge");
   //      testMap.put("dishes", "dish");
   //      testMap.put("phases", "phase");
   //      testMap.put("witches", "witch");
   //      testMap.put("baths", "bath");
   //      testMap.put("calves", "calf");
   //      testMap.put("lives", "life");
   //      testMap.put("proofs", "proof");
   //      testMap.put("boys", "boy");
   //      testMap.put("dwarfs", "dwarf");
   //      testMap.put("dwarves", "dwarf");
   //      testMap.put("hooves", "hoof");
   //      testMap.put("chairs", "chair");
   //      testMap.put("heroes", "hero");
   //      testMap.put("cantos", "canto");
   //      testMap.put("porticos", "portico");
   //      testMap.put("indeces", "index");
   //      testMap.put("leaves", "leaf");
   //      testMap.put("hello", "hello");
   //      testMap.put("axes", "axis");
   //      testMap.put("species", "species");
   //      testMap.put("series", "series");
   //      testMap.put("appendeces", "appendix");
   //
   //      long start = System.currentTimeMillis();
   //      int errCount = 0;
   //      StringBuilder messages = new StringBuilder();
   //      for (String key : testMap.keySet()) {
   //         String actual = WordsUtil.toSingular(key);
   //         String expected = testMap.get(key);
   //
   //         if (actual.equals(expected) != true) {
   //            messages.append(String.format("Value:[%s] Actual: [%s] Expected: [%s] Result:[ FAILED ] \n", key, actual,
   //                  expected));
   //            errCount++;
   //         }
   //      }
   //      messages.append(String.format("Execution Time: %s ms", System.currentTimeMillis() - start));
   //      if (errCount > 0) {
   //         System.err.println(messages);
   //      } else {
   //         System.out.println(messages);
   //         System.out.println("All Passed !");
   //      }
   //   }
}
