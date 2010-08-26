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
import org.eclipse.osee.framework.search.engine.MatchLocation;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.SearchOptions.SearchOptionsEnum;
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

   public WordOrderMatcherTest(String data, String toSearch, boolean caseSensitive, boolean allLocations, List<MatchLocation> expected) {
      super();
      this.toSearch = toSearch;
      this.data = data;
      this.expected = expected;
      this.options = new SearchOptions();
      options.put(SearchOptionsEnum.case_sensitive.asStringOption(), caseSensitive);
      options.put(SearchOptionsEnum.find_all_locations.asStringOption(), allLocations);
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
      data.add(new Object[] {"Why is this here again.", "THIS", false, false, getLocs(8, 11)});
      data.add(new Object[] {"Why is this here again.", "THIS", true, false, getLocs()});
      data.add(new Object[] {"Why is this here this again.", "THIS", false, false, getLocs(8, 11)});
      data.add(new Object[] {"Why is this here this again.", "THIS", false, true, getLocs(8, 11, 18, 21)});
      data.add(new Object[] {"hello #@!@$$%'- again.", "hello again", false, false, getLocs(1, 21)});
      data.add(new Object[] {"hello word again.", "hello again", false, false, getLocs()});
      return data;
   }

   private static List<MatchLocation> getLocs(int... data) {
      return EngineAsserts.asLocations(data);
   }

   private InputStream toStream(String value) throws UnsupportedEncodingException {
      return new ByteArrayInputStream(value.getBytes("utf-8"));
   }
}
