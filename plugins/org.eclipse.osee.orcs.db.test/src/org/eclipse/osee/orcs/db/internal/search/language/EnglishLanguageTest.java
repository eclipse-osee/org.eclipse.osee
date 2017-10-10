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
package org.eclipse.osee.orcs.db.internal.search.language;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.search.tagger.Language;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link EnglishLanguage}
 *
 * @author Roberto E. Escobar
 */
public class EnglishLanguageTest {

   private static Language language;

   @BeforeClass
   public static void setup() {
      Log log = new MockLog();
      language = new EnglishLanguage(log);
   }

   @AfterClass
   public static void tearDown() {
      language = null;
   }

   private Map<String, String> getSingularToPluralData() {
      Map<String, String> testMap = new LinkedHashMap<>();
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
      testMap.put("indices", "index");
      testMap.put("leaves", "leaf");
      testMap.put("hello", "hello");
      testMap.put("axes", "axis");
      testMap.put("boxes", "box");
      testMap.put("foxes", "fox");
      testMap.put("species", "species");
      testMap.put("series", "series");
      testMap.put("status", "status");
      testMap.put("as", "as");
      testMap.put("appendeces", "appendix");
      testMap.put("don't", "don't");
      testMap.put("yours", "yours");
      testMap.put("Larry's", "Larry");
      testMap.put("Joe's bag of tricks", "Joe bag of trick");
      testMap.put("What's that", "What that");
      testMap.put("Company's books", "Company book");
      testMap.put("Charles's cars", "Charles car");
      testMap.put("witches' brooms", "witch broom");
      testMap.put("babies' beds", "baby bed");
      testMap.put("children's books", "child book");
      return testMap;
   }

   @Test
   public void testSingularToPlural() {
      Map<String, String> testMap = getSingularToPluralData();
      for (String key : testMap.keySet()) {
         StringBuilder builder = new StringBuilder();
         Scanner scanner = new Scanner(key);
         while (scanner.hasNext()) {
            if (builder.length() > 0) {
               builder.append(" ");
            }
            builder.append(language.toSingular(scanner.next()));
         }
         scanner.close();
         Assert.assertEquals(String.format("Original: [%s] ", key), testMap.get(key).toLowerCase(), builder.toString());
      }
   }
}
