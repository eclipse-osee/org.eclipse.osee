/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ParentTopTeamColumn}
 *
 * @author Donald G. Dunne
 */
public class ParentTopTeamColumnTest {

   // @formatter:off

   @Mock private IAtsTeamWorkflow teamWf1;
   @Mock private IAtsTeamDefinition teamDefTop;
   @Mock private IAtsTeamDefinition teamDefChild;
   @Mock private IAtsTeamDefinition noVersionsTeamDef;
   @Mock private IAtsActionableItem aia1;
   @Mock private IAtsActionableItem aia_parent;
   @Mock private IAtsVersion ver1;
   @Mock private IAtsAbstractReview rev1;
   @Mock private IAtsTeamDefinitionService teamDefinitionService;
   @Mock private AtsApi atsApi;
   @Mock private IAtsVersionService versionService;
   @Mock private IAtsActionableItemService actionableItemService;

   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(teamWf1.getParentTeamWorkflow()).thenReturn(teamWf1);
      when(teamWf1.getTeamDefinition()).thenReturn(teamDefChild);
      when(teamDefTop.getAtsApi()).thenReturn(atsApi);
      when(teamDefChild.getAtsApi()).thenReturn(atsApi);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(teamDefinitionService.getTeamDefHoldingVersions(teamDefChild)).thenReturn(teamDefTop);
      when(teamDefChild.getName()).thenReturn("TEAM child");
      when(teamDefTop.getName()).thenReturn("TEAM top");
      when(atsApi.getVersionService()).thenReturn(versionService);
      when(noVersionsTeamDef.getAtsApi()).thenReturn(atsApi);
      when(aia1.getAtsApi()).thenReturn(atsApi);
      when(atsApi.getActionableItemService()).thenReturn(actionableItemService);
   }

   /**
    * Test that team name comes from parent team definition when workflow given
    */
   @org.junit.Test
   public void testGetColumnText_fromTeamWf() throws Exception {
      Set<IAtsVersion> ver = new HashSet<>();
      ver.addAll(Arrays.asList(ver1));
      when(versionService.getVersions(teamDefTop)).thenReturn(ver);

      String columnText = ParentTopTeamColumn.getColumnText(teamWf1);

      Assert.assertEquals("TEAM top", columnText);
   }

   /**
    * Test that team name comes from parent team definition when review with related teamDef given
    */
   @org.junit.Test
   public void testGetColumnText_fromRelatedReview() throws Exception {
      Set<IAtsVersion> ver = new HashSet<>();
      ver.addAll(Arrays.asList(ver1));
      when(versionService.getVersions(teamDefTop)).thenReturn(ver);
      when(rev1.getParentTeamWorkflow()).thenReturn(teamWf1);

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("TEAM top", columnText);
   }

   /**
    * Test that team name comes from parent team definition when review with NO related teamDef but Actionable Item is
    * related to Team Def
    */
   @org.junit.Test
   public void testGetColumnText_fromStandAloneReview() throws Exception {
      when(aia1.getTeamDefinition()).thenReturn(teamDefChild);
      when(rev1.getActionableItems()).thenReturn(Collections.asHashSet(aia1));

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("TEAM top", columnText);
   }

   /**
    * Test that team name comes from parent team definition when review with NO related teamDef but inherited Actionable
    * Item is related to Team Def
    */
   @org.junit.Test
   public void testGetColumnText_fromStandAloneReview_inheritedAi() throws Exception {
      when(rev1.getActionableItems()).thenReturn(Collections.asHashSet(aia1));
      when(aia1.getAtsApi().getActionableItemService().getTeamDefinitionInherited(aia1)).thenReturn(teamDefChild);

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("TEAM top", columnText);
   }

   /**
    * Test that get empty string when no AIs and no Team Defs related
    */
   @org.junit.Test
   public void testGetColumnText_fromStandAloneReview_noAis() throws Exception {
      when(rev1.getActionableItems()).thenReturn(java.util.Collections.<IAtsActionableItem> emptySet());
      when(aia1.getAtsApi().getActionableItemService().getTeamDefinitionInherited(aia1)).thenReturn(teamDefChild);

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("", columnText);
   }

   /**
    * Test that get current Team Def if no versionss and no ais
    */
   @org.junit.Test
   public void testGetTopTeamDefName_noVersions() throws Exception {
      when(noVersionsTeamDef.getName()).thenReturn("TEAM");
      when(teamDefinitionService.getTeamBranchId(noVersionsTeamDef)).thenReturn(null);

      String topTeamDefName = ParentTopTeamColumn.getTopTeamDefName(noVersionsTeamDef);

      Assert.assertEquals("TEAM", topTeamDefName);
   }

}
