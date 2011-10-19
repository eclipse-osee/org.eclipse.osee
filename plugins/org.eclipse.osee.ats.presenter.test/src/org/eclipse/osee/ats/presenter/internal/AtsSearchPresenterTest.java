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
package org.eclipse.osee.ats.presenter.internal;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.mocks.MockAtsArtifactProvider;
import org.eclipse.osee.ats.mocks.MockAtsSearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class AtsSearchPresenterTest {

   @Test
   public void testSelectProgram() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponent> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent>(provider);
      MockAtsSearchHeaderComponent comp = new MockAtsSearchHeaderComponent();
      WebId program = new WebId("prg1Guid_18H74Zqo3gA", "program1");
      presenter.selectProgram(program, comp);
      Assert.assertEquals(2, comp.getBuilds().size());
   }

   @Test
   public void testSelectSearch() {
      AtsSearchPresenterImpl<AtsSearchHeaderComponent> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent>(null);
      MockSearchNavigator navigator = new MockSearchNavigator();
      String programGuid = GUID.create();
      String buildGuid = GUID.create();
      WebId program = new WebId(programGuid, "prgName");
      WebId build = new WebId(buildGuid, "bldName");
      AtsSearchParameters params = new AtsSearchParameters("test search phrase", true, false, build, program);
      presenter.selectSearch(params, navigator);
      String url = navigator.getResultsUrl();
      String expected =
         "/program=" + programGuid + "&build=" + buildGuid + "&nameOnly=true&search=test%20search%20phrase";
      Assert.assertEquals(expected, url);
   }

   @Test
   public void testInitSearchHome() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponent> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent>(provider);
      MockAtsSearchHeaderComponent headerComp = new MockAtsSearchHeaderComponent();
      presenter.initSearchHome(headerComp);
      Assert.assertTrue(headerComp.isClearAllCalled());
      Assert.assertEquals(3, headerComp.getPrograms().size());
   }

   @Test
   public void testInitSearchResults() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponent> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent>(provider);
      String url = null;
      MockAtsSearchHeaderComponent headerComp = new MockAtsSearchHeaderComponent();
      MockSearchResultsListComponent resultsComponent = new MockSearchResultsListComponent();
      presenter.initSearchResults(url, headerComp, resultsComponent);
   }
}
