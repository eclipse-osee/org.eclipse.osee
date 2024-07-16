/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for client {@link AtsQueryServiceImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImplTest {

   /**
    * IAtsQuery AtsQueryServiceImpl.createQuery() tested in AtsQueryImplTest
    */

   /**
    * IAtsConfigQuery AtsQueryServiceImpl.createQuery() tested in AtsQueryImplTest
    */

   /**
    * IAtsWorkItemFilter createFilter() is tested in AtsWorkItemFilterTest
    */

   /**
    * Test Cases for<br/>
    * 1. saveSearch<br/>
    * 2. getSearches<br/>
    * 3. getSearch(user, id)<br/>
    * 4. removeSearch(user, data)<br/>
    * 5. getAttrById - indirectly tested by removeSearch
    */
   @Test
   public void testSaveSearchAndGetSavedSearchesAndRemoveSearch() {
      AtsUtil.setIsInTest(true);

      String namespace = AtsSearchUtil.ATS_QUERY_NAMESPACE;
      List<AtsSearchData> savedSearches = AtsApiService.get().getQueryService().getSavedSearches(namespace);
      Assert.assertEquals(savedSearches.toString(), 0, savedSearches.size());

      AtsSearchData data = new AtsSearchData("my search");
      data.setColorTeam("blue");
      data.setNamespace(namespace);
      AtsApiService.get().getQueryService().saveSearch(data);

      savedSearches = AtsApiService.get().getQueryService().getSavedSearches(namespace);
      Assert.assertEquals(savedSearches.toString(), 1, savedSearches.size());

      AtsSearchData data2 = new AtsSearchData("my search 2");
      data2.setColorTeam("green");
      data2.setNamespace(namespace);
      AtsApiService.get().getQueryService().saveSearch(data2);

      savedSearches = AtsApiService.get().getQueryService().getSavedSearches(namespace);
      Assert.assertEquals(savedSearches.toString(), 2, savedSearches.size());

      String namespace2 = AtsSearchUtil.ATS_QUERY_GOAL_NAMESPACE;
      data = new AtsSearchData("my search 3");
      data.setColorTeam("gold");
      data.setNamespace(namespace2);
      AtsApiService.get().getQueryService().saveSearch(data);

      savedSearches = AtsApiService.get().getQueryService().getSavedSearches(namespace2);
      Assert.assertEquals(savedSearches.toString(), 1, savedSearches.size());

      // retrieve the saved search cause it has the search it
      data = savedSearches.iterator().next();

      AtsApiService.get().getQueryService().removeSearch(data);

      savedSearches = AtsApiService.get().getQueryService().getSavedSearches(namespace2);
      Assert.assertEquals(savedSearches.toString(), 0, savedSearches.size());
   }
}