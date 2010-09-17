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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.internal.translation.SearchRequestTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link SearchRequestTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SearchRequestTranslatorTest extends BaseTranslatorTest<SearchRequest> {

   public SearchRequestTranslatorTest(SearchRequest data, ITranslator<SearchRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(SearchRequest expected, SearchRequest actual) {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      ITranslator<SearchRequest> translator = new SearchRequestTranslator();
      List<Object[]> data = new ArrayList<Object[]>();

      SearchOptions options = new SearchOptions();
      data.add(new Object[] {new SearchRequest(CoreBranches.COMMON, "", options), translator});

      options = new SearchOptions();
      options.setDeletedIncluded(DeletionFlag.INCLUDE_DELETED);
      options.setCaseSensive(true);
      options.setFindAllLocationsEnabled(false);
      options.setMatchWordOrder(false);
      options.setAttributeTypeFilter(CoreAttributeTypes.Active);

      data.add(new Object[] {
         new SearchRequest(CoreBranches.SYSTEM_ROOT, "a search &&!@#$%!%@!$^!$^!.+", options),
         translator});

      options = new SearchOptions();
      options.setDeletedIncluded(DeletionFlag.EXCLUDE_DELETED);
      options.setCaseSensive(false);
      options.setFindAllLocationsEnabled(true);
      options.setMatchWordOrder(false);
      options.setAttributeTypeFilter(CoreAttributeTypes.Afha, CoreAttributeTypes.Annotation);
      data.add(new Object[] {new SearchRequest(CoreBranches.SYSTEM_ROOT, "a search string", options), translator});

      options = new SearchOptions();
      options.setDeletedIncluded(DeletionFlag.INCLUDE_DELETED);
      options.setCaseSensive(false);
      options.setFindAllLocationsEnabled(false);
      options.setMatchWordOrder(true);
      options.setAttributeTypeFilter(CoreAttributeTypes.ContentUrl, CoreAttributeTypes.Country);
      data.add(new Object[] {new SearchRequest(CoreBranches.COMMON, "one more", options), translator});
      return data;
   }
}
