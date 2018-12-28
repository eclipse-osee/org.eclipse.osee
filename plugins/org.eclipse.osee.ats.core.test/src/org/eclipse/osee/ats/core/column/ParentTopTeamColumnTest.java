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
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ParentTopTeamColumn}
 *
 * @author Donald G. Dunne
 */
public class ParentTopTeamColumnTest {

   // @formatter:off

   @Mock private IAtsTeamWorkflow teamWf1;
   @Mock private IAtsTeamDefinition teamDef_top;
   @Mock private IAtsTeamDefinition teamDef_child;
   @Mock private IAtsActionableItem aia1;
   @Mock private IAtsActionableItem aia_parent;
   @Mock private IAtsVersion ver1;
   @Mock private IAtsAbstractReview rev1;

   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(teamWf1.getParentTeamWorkflow()).thenReturn(teamWf1);
      when(teamWf1.getTeamDefinition()).thenReturn(teamDef_child);
      when(teamDef_child.getTeamDefinitionHoldingVersions()).thenReturn(teamDef_top);
      when(teamDef_child.getName()).thenReturn("TEAM child");
      when(teamDef_top.getName()).thenReturn("TEAM top");
   }

   /**
    * Test that team name comes from parent team definition when workflow given
    */
   @org.junit.Test
   public void testGetColumnText_fromTeamWf() throws Exception {
      Set<IAtsVersion> ver = new HashSet<>();
      ver.addAll(Arrays.asList(ver1));
      when(teamDef_top.getVersions()).thenReturn(ver);

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
      when(teamDef_top.getVersions()).thenReturn(ver);
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
      when(aia1.getTeamDefinition()).thenReturn(teamDef_child);
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
      when(aia1.getTeamDefinitionInherited()).thenReturn(teamDef_child);

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("TEAM top", columnText);
   }

   /**
    * Test that get empty string when no AIs and no Team Defs related
    */
   @org.junit.Test
   public void testGetColumnText_fromStandAloneReview_noAis() throws Exception {
      when(rev1.getActionableItems()).thenReturn(java.util.Collections.<IAtsActionableItem> emptySet());
      when(aia1.getTeamDefinitionInherited()).thenReturn(teamDef_child);

      String columnText = ParentTopTeamColumn.getColumnText(rev1);

      Assert.assertEquals("", columnText);
   }

   /**
    * Test that get current Team Def if no versionss and no ais
    */
   @org.junit.Test
   public void testGetTopTeamDefName_noVersions() throws Exception {
      IAtsTeamDefinition noVersions = Mockito.mock(IAtsTeamDefinition.class);
      when(noVersions.getName()).thenReturn("TEAM");
      when(noVersions.getTeamDefinitionHoldingVersions()).thenReturn(null);

      String topTeamDefName = ParentTopTeamColumn.getTopTeamDefName(noVersions);

      Assert.assertEquals("TEAM", topTeamDefName);
   }

}
