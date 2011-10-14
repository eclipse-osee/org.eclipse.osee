/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.presenter.WebSearchPresenter;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockSearchHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.mock.MockArtifact;
import org.eclipse.osee.orcs.mock.MockAttribute;
import org.eclipse.osee.orcs.mock.MockMatch;
import org.eclipse.osee.orcs.search.Match;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class WebSearchPresenterTest {

   private List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchReslts() {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      MockArtifact art = new MockArtifact("guid1", "matchArt");
      MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");
      Match match = new MockMatch(art, attr);
      toReturn.add(match);
      return toReturn;
   }

   @Test
   public void testInitSearchResults() {
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setResultList(getSearchReslts());
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url = "branch=branch1?nameOnly=true?search=this%20is%20a%20test";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp);
      List<MockSearchResultComponent> searchResults = searchResultsComp.getSearchResults();
      Assert.assertEquals(1, searchResults.size());
   }

   @Test
   public void testSelectArtifact() {
      MockSearchNavigator navigator = new MockSearchNavigator();
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      WebArtifact artifact = new WebArtifact("artGuid", "name", "type", null, new WebId("branchId", "branchName"));
      presenter.selectArtifact(artifact, navigator);
      String expectedUrl = "branch=branchId?artifact=artGuid";
      Assert.assertEquals(expectedUrl, navigator.getArtifactUrl());
   }

   @Test
   public void testInitSearchHome() {
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      presenter.initSearchHome(searchHeaderComp);
      Assert.assertTrue(searchHeaderComp.isClearAllCalled());
   }
}
