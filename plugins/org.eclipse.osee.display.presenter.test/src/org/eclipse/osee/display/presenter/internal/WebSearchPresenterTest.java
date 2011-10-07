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

import junit.framework.Assert;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.presenter.WebSearchPresenter;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockSearchHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class WebSearchPresenterTest {

   @Test
   public void testInitSearchResults() {
      MockArtifactProvider provider = new MockArtifactProvider();
      WebSearchPresenter presenter = new WebSearchPresenter(provider);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url = "branch=branch1?nameOnly=true?search=this%20is%20a%20test";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp);
   }

   @Test
   public void testSelectArtifact() {
      MockSearchNavigator navigator = new MockSearchNavigator();
      WebSearchPresenter presenter = new WebSearchPresenter(null);
      WebArtifact artifact = new WebArtifact("artGuid", "name", "type", null, new WebId("branchId", "branchName"));
      presenter.selectArtifact(artifact, navigator);
      String expectedUrl = "branch=branchId?artifact=artGuid";
      Assert.assertEquals(expectedUrl, navigator.getArtifactUrl());
   }

   @Test
   public void testInitSearchHome() {
      WebSearchPresenter presenter = new WebSearchPresenter(null);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      presenter.initSearchHome(searchHeaderComp);
      Assert.assertTrue(searchHeaderComp.isClearAllCalled());
   }
}
