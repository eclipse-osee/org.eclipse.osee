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
package org.eclipse.osee.framework.search.engine.test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.framework.search.engine.utility.WordChunker;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public class TestWordsUtil extends TestCase {

   private Map<String, String> getSingularToPluralData() {
      Map<String, String> testMap = new LinkedHashMap<String, String>();
      testMap.put("tries", "try");
      testMap.put("volcanoes", "volcano");
      testMap.put("geese", "goose");
      testMap.put("windows", "window");
      testMap.put("glasses", "glass");
      testMap.put("fishes", "fish");
      testMap.put("houses", "house");
      testMap.put("judges", "judge");
      testMap.put("dishes", "dish");
      testMap.put("phases", "phase");
      testMap.put("witches", "witch");
      testMap.put("baths", "bath");
      testMap.put("calves", "calf");
      testMap.put("lives", "life");
      testMap.put("proofs", "proof");
      testMap.put("boys", "boy");
      testMap.put("dwarfs", "dwarf");
      testMap.put("dwarves", "dwarf");
      testMap.put("hooves", "hoof");
      testMap.put("chairs", "chair");
      testMap.put("heroes", "hero");
      testMap.put("cantos", "canto");
      testMap.put("porticos", "portico");
      testMap.put("indeces", "index");
      testMap.put("leaves", "leaf");
      testMap.put("hello", "hello");
      testMap.put("axes", "axis");
      testMap.put("species", "species");
      testMap.put("series", "series");
      testMap.put("appendeces", "appendix");
      return testMap;
   }

   public void testSingularToPlural() {
      Map<String, String> testMap = getSingularToPluralData();
      for (String key : testMap.keySet()) {
         String expected = testMap.get(key);
         String actual = WordsUtil.toSingular(key);
         assertEquals(String.format("Original: [%s] ", key), expected, actual);
      }
   }

   private Map<String, String> getStripPossessiveData() {
      Map<String, String> toReturn = new LinkedHashMap<String, String>();
      toReturn.put("don't", "don't");
      toReturn.put("yours", "yours");
      toReturn.put("Larry's", "Larry");
      toReturn.put("Joe's bag of tricks", "Joe bag of tricks");
      toReturn.put("What's that?", "What that?");
      toReturn.put("Company's books", "Company books");
      toReturn.put("Charles's cars.", "Charles cars.");
      toReturn.put("witches' brooms", "witches brooms");
      toReturn.put("babies' beds.", "babies beds.");
      toReturn.put("children's books", "children books");
      return toReturn;
   }

   public void testStripPossessive() throws UnsupportedEncodingException {
      Map<String, String> testMap = getStripPossessiveData();
      for (String key : testMap.keySet()) {
         StringBuilder builder = new StringBuilder();
         WordChunker chunker = new WordChunker(new ByteArrayInputStream(key.getBytes()));
         for (String word : chunker) {
            if (builder.length() > 0) {
               builder.append(" ");
            }
            builder.append(WordsUtil.stripPossesive(word));
         }
         assertEquals(String.format("Original: [%s] ", key), testMap.get(key), builder.toString());
      }
   }

   private Map<String, String[]> getSplitOnPuntucationData() {
      Map<String, String[]> toReturn = new LinkedHashMap<String, String[]>();
      toReturn.put("test.db.preset", new String[] {"test", "db", "preset"});
      toReturn.put("{what is this}", new String[] {"what", "is", "this"});
      toReturn.put("What is your name?", new String[] {"What", "is", "your", "name"});
      toReturn.put("Run!!", new String[] {"Run"});
      toReturn.put("don't", new String[] {"don't"});
      toReturn.put("hello", new String[] {"hello"});
      return toReturn;
   }

   public void testSplitOnPunctuation() {
      Map<String, String[]> testMap = getSplitOnPuntucationData();
      for (String key : testMap.keySet()) {
         String[] results = WordsUtil.splitOnPunctuation(key);
         assertEquals(String.format("Original: [%s] ", key), Arrays.deepToString(testMap.get(key)),
               Arrays.deepToString(results));
      }
   }

   private Map<String, String> getXmlMarkupRemovalData() {
      Map<String, String> toReturn = new LinkedHashMap<String, String>();

      return toReturn;
   }

   public void testXmlMarkupRemoval() {
      Map<String, String> testMap = getXmlMarkupRemovalData();
      for (String key : testMap.keySet()) {
         String results = WordsUtil.extractTextDataFromXMLTags(key);
         assertEquals(String.format("Original: [%s] ", key), testMap.get(key), results);
      }
   }
}
