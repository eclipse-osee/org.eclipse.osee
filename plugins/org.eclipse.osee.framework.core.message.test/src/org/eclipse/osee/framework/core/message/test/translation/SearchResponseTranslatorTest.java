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
package org.eclipse.osee.framework.core.message.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.message.internal.translation.SearchResponseTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link SearchResponseTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SearchResponseTranslatorTest extends BaseTranslatorTest<SearchResponse> {

   public SearchResponseTranslatorTest(SearchResponse data, ITranslator<SearchResponse> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(SearchResponse expected, SearchResponse actual) {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      ITranslator<SearchResponse> translator = new SearchResponseTranslator();
      List<Object[]> data = new ArrayList<Object[]>();
      createTest(data, translator, null, false, false);
      createTest(data, translator, "An Error", false, true);
      createTest(data, translator, null, true, true);
      return data;
   }

   private static void createTest(Collection<Object[]> data, ITranslator<SearchResponse> translator, String errorMessage, boolean hasSearchTags, boolean addMatchData) {
      SearchResponse response = new SearchResponse();
      response.setErrorMessage(errorMessage);

      if (hasSearchTags) {
         response.getSearchTags().put("one", -123141L);
         response.getSearchTags().put("two", -123141L);
      }
      if (addMatchData) {
         response.add(0, 1, 1);
         response.add(0, 1, 2);
         response.add(1, 2, 3, 1, 2);
         response.add(1, 3, 4, 3, 4);
      }
      data.add(new Object[] {response, translator});
   }
}
