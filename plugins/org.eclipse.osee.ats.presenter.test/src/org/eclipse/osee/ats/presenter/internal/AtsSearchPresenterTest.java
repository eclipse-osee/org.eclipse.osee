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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.presenter.mocks.MockArtifact;
import org.eclipse.osee.display.presenter.mocks.MockAttribute;
import org.eclipse.osee.display.presenter.mocks.MockDisplayOptionsComponent;
import org.eclipse.osee.display.presenter.mocks.MockLogger;
import org.eclipse.osee.display.presenter.mocks.MockMatch;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.UrlQuery;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class AtsSearchPresenterTest {

   @Test
   public void testSelectProgram() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters>(provider, new MockLogger());
      MockAtsSearchHeaderComponent comp = new MockAtsSearchHeaderComponent();
      ViewId program = new ViewId("prg1Guid_18H74Zqo3gA", "program1");
      presenter.selectProgram(program, comp);
      Assert.assertEquals(2, comp.getBuilds().size());
   }

   @Test
   public void testSelectSearch() {
      AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters>(null, new MockLogger());
      MockSearchNavigator navigator = new MockSearchNavigator();
      String programGuid = "prg1Guid_18H74Zqo3gA";
      String buildGuid = "buildGuid1_d74Zqo3gA";
      ViewId program = new ViewId(programGuid, "prgName");
      ViewId build = new ViewId(buildGuid, "bldName");
      AtsSearchParameters params = new AtsSearchParameters("phrase", true, build, program);
      presenter.selectSearch("", params, navigator);
      String url = navigator.getResultsUrl();

      String expected =
         "/" + new UrlQuery().put("search", "phrase").put("program", programGuid).put("nameOnly", "true").put("build",
            buildGuid).toString();

      Assert.assertEquals(expected, url);
   }

   @Test
   public void testInitSearchResults() throws UnsupportedEncodingException {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      List<Match<ArtifactReadable, AttributeReadable<?>>> resultList =
         new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();
      MockArtifact art = new MockArtifact("guid1", "matchArt");
      MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");
      Match match = new MockMatch(art, attr);
      resultList.add(match);
      provider.setResultList(resultList);
      AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters>(provider, new MockLogger());
      MockAtsSearchHeaderComponent headerComp = new MockAtsSearchHeaderComponent();
      MockSearchResultsListComponent resultsComponent = new MockSearchResultsListComponent();
      presenter.initSearchResults(null, headerComp, resultsComponent, optionsComp);
      Assert.assertEquals(3, headerComp.getPrograms().size());

      String programGuid = GUID.create();
      long buildUuid = 70559324;
      String url =
         "/" + new UrlQuery().put("program", programGuid).put("build", buildUuid).put("nameOnly", "true").put("search",
            "phrase").put("verbose", "false").toString();
      presenter.initSearchResults(url, headerComp, resultsComponent, optionsComp);
      Assert.assertEquals(1, resultsComponent.getSearchResults().size());
   }
}
