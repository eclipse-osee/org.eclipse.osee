/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.ArrayList;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
      AtsUser user = AtsClientService.get().getUserService().getCurrentUserNoCache();
      AtsUtil.setIsInText(true);

      String namespace = AtsSearchUtil.ATS_QUERY_NAMESPACE;
      ArrayList<AtsSearchData> savedSearches =
         AtsClientService.get().getQueryService().getSavedSearches(user, namespace);
      Assert.assertEquals(savedSearches.toString(), 0, savedSearches.size());

      AtsSearchData data = new AtsSearchData("my search");
      data.setColorTeam("blue");
      data.setNamespace(namespace);
      AtsClientService.get().getQueryService().saveSearch(user, data);

      savedSearches = AtsClientService.get().getQueryService().getSavedSearches(user, namespace);
      Assert.assertEquals(savedSearches.toString(), 1, savedSearches.size());

      AtsSearchData data2 = new AtsSearchData("my search 2");
      data2.setColorTeam("green");
      data2.setNamespace(namespace);
      AtsClientService.get().getQueryService().saveSearch(user, data2);

      savedSearches = AtsClientService.get().getQueryService().getSavedSearches(user, namespace);
      Assert.assertEquals(savedSearches.toString(), 2, savedSearches.size());

      String namespace2 = AtsSearchUtil.ATS_QUERY_GOAL_NAMESPACE;
      data = new AtsSearchData("my search 3");
      data.setColorTeam("gold");
      data.setNamespace(namespace2);
      AtsClientService.get().getQueryService().saveSearch(user, data);

      savedSearches = AtsClientService.get().getQueryService().getSavedSearches(user, namespace2);
      Assert.assertEquals(savedSearches.toString(), 1, savedSearches.size());

      // retrieve the saved search cause it has the search it
      data = savedSearches.iterator().next();

      AtsClientService.get().getQueryService().removeSearch(user, data);

      savedSearches = AtsClientService.get().getQueryService().getSavedSearches(user, namespace2);
      Assert.assertEquals(savedSearches.toString(), 0, savedSearches.size());

   }

}
