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
package org.eclipse.osee.orcs.db.internal.search.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.db.internal.search.SearchAsserts;
import org.eclipse.osee.orcs.search.CaseType;
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
   private final CaseType caseType;
   private final boolean findAllMatchLocations;
   private final WordOrderMatcher matcher;

   public WordOrderMatcherTest(String data, String toSearch, CaseType caseType, boolean findAllMatchLocations, List<MatchLocation> expected) {
      super();
      this.toSearch = toSearch;
      this.data = data;
      this.caseType = caseType;
      this.findAllMatchLocations = findAllMatchLocations;
      this.expected = expected;
      this.matcher = new WordOrderMatcher();
   }

   @Test
   public void testWordOrderMatcher() throws UnsupportedEncodingException, OseeCoreException {
      InputStream inputStream = toStream(data);
      List<MatchLocation> actual = matcher.findInStream(inputStream, toSearch, caseType, findAllMatchLocations);
      SearchAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      addTest(data, "Why is this here again.", "THIS", CaseType.IGNORE_CASE, false, getLocs(8, 11));
      addTest(data, "Why is this here again.", "THIS", CaseType.MATCH_CASE, false, getLocs());
      addTest(data, "Why is this here this again.", "THIS", CaseType.IGNORE_CASE, false, getLocs(8, 11));
      addTest(data, "Why is this here this again.", "THIS", CaseType.IGNORE_CASE, true, getLocs(8, 11, 18, 21));
      addTest(data, "hello #@!@$$%'- again.", "hello again", CaseType.IGNORE_CASE, false, getLocs(1, 21));
      addTest(data, "hello word again.", "hello again", CaseType.IGNORE_CASE, false, getLocs());
      addTest(data, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         CaseType.IGNORE_CASE, false, getLocs(18, 35));
      addTest(data, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         CaseType.MATCH_CASE, false, getLocs(18, 35));
      addTest(data, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", CaseType.MATCH_CASE, false, getLocs(41, 58));
      addTest(data, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", CaseType.IGNORE_CASE, false, getLocs(41, 58));
      addTest(data, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_\nID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", CaseType.IGNORE_CASE, false, getLocs(41, 59));
      return data;
   }

   private static void addTest(Collection<Object[]> testData, String data, String toSearch, CaseType caseType, boolean findAllMatchLocations, List<MatchLocation> expectedLocs) {
      testData.add(new Object[] {data, toSearch, caseType, findAllMatchLocations, expectedLocs});
   }

   private static List<MatchLocation> getLocs(int... data) {
      return SearchAsserts.asLocations(data);
   }

   private InputStream toStream(String value) throws UnsupportedEncodingException {
      return new ByteArrayInputStream(value.getBytes("utf-8"));
   }
}
