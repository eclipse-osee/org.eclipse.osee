/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.db.internal.search.SearchAsserts;
import org.eclipse.osee.orcs.db.internal.search.tagger.StreamMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author John Misinco
 */
@RunWith(Parameterized.class)
public class SecondPassMatcherTest {

   private final String toSearch;
   private final String data;
   private final List<MatchLocation> expected;
   private final QueryOption caseType;
   private final boolean findAllMatchLocations;
   private final StreamMatcher matcher;
   private final QueryOption delimiter;
   private final QueryOption order;
   private final QueryOption countType;

   public SecondPassMatcherTest(StreamMatcher matcher, String data, String toSearch, QueryOption caseType, QueryOption delimiter, QueryOption order, QueryOption countType, boolean findAllMatchLocations, List<MatchLocation> expected) {
      this.toSearch = toSearch;
      this.data = data;
      this.caseType = caseType;
      this.delimiter = delimiter;
      this.order = order;
      this.findAllMatchLocations = findAllMatchLocations;
      this.countType = countType;
      this.expected = expected;
      this.matcher = matcher;
   }

   @Test
   public void testWordOrderMatcher() throws UnsupportedEncodingException {
      InputStream inputStream = toStream(data);
      List<MatchLocation> actual =
         matcher.findInStream(inputStream, toSearch, findAllMatchLocations, caseType, order, countType, delimiter);
      SearchAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<>();

      StreamMatcher matcher = MatcherFactory.createMatcher();

      // MATCH_ORDER tests
      addTest(data, matcher, "THIS", "THIS", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, false, getLocs(1, 4));
      addTest(data, matcher, "THIS", "this", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, false, getLocs());
      addTest(data, matcher, "THIS", "this", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, false, getLocs(1, 4));
      addTest(data, matcher, "THIS bot", "this", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, false, getLocs());
      addTest(data, matcher, "What  does this mean", "this", QueryOption.CASE__MATCH,
         QueryOption.TOKEN_DELIMITER__EXACT, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         false, getLocs(12, 15));
      addTest(data, matcher, "What   does   this mean", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         false, getLocs(1, 11));
      addTest(data, matcher, "What   does   this mean", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         false, getLocs());
      addTest(data, matcher, "What     does", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         false, getLocs(1, 13));
      addTest(data, matcher, "What->does] .this. mean", "What does   tHis", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, false,
         getLocs(1, 17));
      addTest(data, matcher, "does does does", "does does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         false, getLocs());
      addTest(data, matcher, "   does does does", "does does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         false, getLocs(4, 12));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH,
         QueryOption.TOKEN_COUNT__IGNORE, false, getLocs(18, 35));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH,
         QueryOption.TOKEN_COUNT__IGNORE, false, getLocs(18, 35));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, false, getLocs(41, 58));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, false, getLocs(41, 58));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_\nID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, false, getLocs(41, 59));

      addTest(data, matcher, "THIS", "THIS", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, true, getLocs(1, 4));
      addTest(data, matcher, "THIS", "this", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, true, getLocs());
      addTest(data, matcher, "THIS", "this", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, true, getLocs(1, 4));
      addTest(data, matcher, "THIS bot", "this", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__EXACT,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH, true, getLocs());
      addTest(data, matcher, "What  does this mean", "this", QueryOption.CASE__MATCH,
         QueryOption.TOKEN_DELIMITER__EXACT, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(12, 15));
      addTest(data, matcher, "What   does   this mean", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(1, 11));
      addTest(data, matcher, "What   does   this mean", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         true, getLocs());
      addTest(data, matcher, "What     does", "what does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         true, getLocs(1, 13));
      addTest(data, matcher, "What->does] .this. mean", "What does   tHis", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, true,
         getLocs(1, 17));
      addTest(data, matcher, "does does does", "does does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__MATCH,
         true, getLocs());
      addTest(data, matcher, "   does does does", "does does", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(4, 12));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH,
         QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(18, 35));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_ID]      Selected -> ", "SELECTED_STRING_ID",
         QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH,
         QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(18, 35));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(41, 58));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_ID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(41, 58));
      addTest(data, matcher, " (Selected) -> [.SELECTED_STRING_IWRONG SELECTED_STRING_\nID_TWO]      Selected -> ",
         "SELECTED_STRING_ID", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(41, 59));

      // ANY_ORDER tests
      addTest(data, matcher, "each token should match", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE,
         false, getLocs(1, 4, 6, 10, 12, 17, 19, 23));
      addTest(data, matcher, "each token should match extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE,
         false, getLocs(1, 4, 6, 10, 12, 17, 19, 23));
      addTest(data, matcher, "each token should match extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__MATCH,
         false, getLocs());
      addTest(data, matcher, "each token should match   ToKen", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(1, 4, 6, 10, 12, 17, 19, 23, 27, 31));
      addTest(data, matcher, "each each should extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE, false,
         getLocs());

      addTest(data, matcher, "each token should match", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(1, 4, 6, 10, 12, 17, 19, 23));
      addTest(data, matcher, "each token should match extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE,
         true, getLocs(1, 4, 6, 10, 12, 17, 19, 23, 31, 35));
      addTest(data, matcher, "each token should match extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__WHITESPACE, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__MATCH,
         true, getLocs());
      addTest(data, matcher, "each each should extra token", "should token match each", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE, true,
         getLocs());

      addTest(data, matcher, "Robot API", "Robot", QueryOption.CASE__MATCH, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(1, 5));

      addTest(data, matcher, DemoUsers.Joe_Smith.getName(), "joe", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(1, 3));
      addTest(data, matcher, DemoUsers.Joe_Smith.getName(), "smith", QueryOption.CASE__IGNORE, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.TOKEN_COUNT__IGNORE, true, getLocs(5, 9));
      return data;
   }

   private static void addTest(Collection<Object[]> testData, StreamMatcher matcher, String data, String toSearch, QueryOption caseType, QueryOption delimiter, QueryOption order, QueryOption countType, boolean findAllMatchLocations, List<MatchLocation> expectedLocs) {
      testData.add(new Object[] {
         matcher,
         data,
         toSearch,
         caseType,
         delimiter,
         order,
         countType,
         findAllMatchLocations,
         expectedLocs});
   }

   private static List<MatchLocation> getLocs(int... data) {
      return SearchAsserts.asLocations(data);
   }

   private InputStream toStream(String value) throws UnsupportedEncodingException {
      return new ByteArrayInputStream(value.getBytes("utf-8"));
   }
}
