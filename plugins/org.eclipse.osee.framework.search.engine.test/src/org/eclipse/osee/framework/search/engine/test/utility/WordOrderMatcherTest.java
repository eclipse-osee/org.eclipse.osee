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
package org.eclipse.osee.framework.search.engine.test.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.search.engine.test.mocks.EngineAsserts;
import org.eclipse.osee.framework.search.engine.utility.WordOrderMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link WordOrderMatcher}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class WordOrderMatcherTest {

   private final String toSearch;
   private final String data;
   private final List<MatchLocation> expected;
   private final SearchOptions options;

   public WordOrderMatcherTest(String data, String toSearch, SearchOptions options, List<MatchLocation> expected) {
      super();
      this.toSearch = toSearch;
      this.data = data;
      this.options = options;
      this.expected = expected;
   }

   @Test
   public void testWordOrderMatcher() throws UnsupportedEncodingException, OseeCoreException {
      InputStream inputStream = toStream(data);
      List<MatchLocation> actual = WordOrderMatcher.findInStream(inputStream, toSearch, options);
      EngineAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      addTest(data, "Why is this here again.", "THIS", false, false, getLocs(8, 11));
      addTest(data, "Why is this here again.", "THIS", true, false, getLocs());
      addTest(data, "Why is this here this again.", "THIS", false, false, getLocs(8, 11));
      addTest(data, "Why is this here this again.", "THIS", false, true, getLocs(8, 11, 18, 21));
      addTest(data, "hello #@!@$$%'- again.", "hello again", false, false, getLocs(1, 21));
      addTest(data, "hello word again.", "hello again", false, false, getLocs());
      return data;
   }

   private static void addTest(Collection<Object[]> testData, String data, String toSearch, boolean caseSensitive, boolean isFindAllLocationsEnabled, List<MatchLocation> expectedLocs) {
      SearchOptions options = new SearchOptions();
      options.setCaseSensive(caseSensitive);
      options.setFindAllLocationsEnabled(isFindAllLocationsEnabled);
      testData.add(new Object[] {data, toSearch, options, expectedLocs});
   }

   private static List<MatchLocation> getLocs(int... data) {
      return EngineAsserts.asLocations(data);
   }

   private InputStream toStream(String value) throws UnsupportedEncodingException {
      return new ByteArrayInputStream(value.getBytes("utf-8"));
   }
}
