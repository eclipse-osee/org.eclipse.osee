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
package org.eclipse.osee.ats.presenter;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.tokens.AtsRelationTypes;
import org.eclipse.osee.ats.presenter.internal.AtsSearchPresenterImpl;
import org.eclipse.osee.ats.presenter.mock.MockAtsSearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.mock.MockArtifact;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class WebProgramsPresenterTest {

   @Test
   public void testSelectProgram() {
      MockArtifact programArtifact = new MockArtifact("prg1Guid", "Program1");
      MockArtifact teamDef = new MockArtifact("teamGuid", "TeamDefinition");
      programArtifact.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, teamDef);

      MockArtifact build1Artifact = new MockArtifact("bld1Guid", "Build1");
      MockArtifact build2Artifact = new MockArtifact("bld2Guid", "Build2");
      MockArtifact build3Artifact = new MockArtifact("bld3Guid", "Build3");
      teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, build1Artifact);
      teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, build2Artifact);
      teamDef.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, build3Artifact);
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setArtifact(programArtifact);
      AtsSearchPresenterImpl presenter = new AtsSearchPresenterImpl(provider);
      MockAtsSearchHeaderComponent comp = new MockAtsSearchHeaderComponent();
      WebId program = new WebId("prg1Guid", "Program1");
      presenter.selectProgram(program, comp);
      Assert.assertEquals(3, comp.getBuilds().size());
      Assert.assertEquals("Build3", comp.getBuilds().get(2).getName());
   }

   @Test
   public void testSelectSearch() {
      AtsSearchPresenterImpl presenter = new AtsSearchPresenterImpl(null);
      MockSearchNavigator navigator = new MockSearchNavigator();
      WebId program = new WebId("prgGuid", "prgName");
      WebId build = new WebId("bldGuid", "bldName");
      presenter.selectSearch(program, build, true, "test search phrase", navigator);
      String url = navigator.getResultsUrl();
      String expected = "program=prgGuid?build=bldGuid?nameOnly=true?search=test%20search%20phrase";
      Assert.assertEquals(expected, url);
   }

   @Test
   public void testInitSearchHome() {
      MockArtifact webPrograms = new MockArtifact("webPrgGuid", "Web Programs");
      MockArtifact program1 = new MockArtifact("prg1Guid", "Program1");
      webPrograms.addRelation(CoreRelationTypes.Universal_Grouping__Members, program1);
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setArtifact(webPrograms);
      AtsSearchPresenterImpl presenter = new AtsSearchPresenterImpl(provider);
      MockAtsSearchHeaderComponent headerComp = new MockAtsSearchHeaderComponent();
      presenter.initSearchHome(headerComp);
      Assert.assertTrue(headerComp.isClearAllCalled());
      Assert.assertEquals(1, headerComp.getPrograms().size());
      Assert.assertEquals("prg1Guid", headerComp.getPrograms().get(0).getGuid());
      Assert.assertEquals("Program1", headerComp.getPrograms().get(0).getName());

      MockArtifact program2 = new MockArtifact("prg2Guid", "Program2");
      MockArtifact program3 = new MockArtifact("prg3Guid", "Program3");
      webPrograms.addRelation(CoreRelationTypes.Universal_Grouping__Members, program2);
      webPrograms.addRelation(CoreRelationTypes.Universal_Grouping__Members, program3);
      presenter.initSearchHome(headerComp);
      Assert.assertEquals(3, headerComp.getPrograms().size());
      Assert.assertEquals("prg3Guid", headerComp.getPrograms().get(2).getGuid());
      Assert.assertEquals("Program3", headerComp.getPrograms().get(2).getName());
   }

   @Test
   public void testInitSearchResults() {

   }
}
