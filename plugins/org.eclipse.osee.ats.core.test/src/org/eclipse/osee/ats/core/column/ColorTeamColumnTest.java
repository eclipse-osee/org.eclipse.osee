/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.Everyone;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.TeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ColorTeamColumn}
 *
 * @author Donald G. Dunne
 */
public class ColorTeamColumnTest {

   // @formatter:off
   @Mock private IAtsTask childWorkItem;
   @Mock private IAtsPeerToPeerReview peerReview;
   @Mock private IAtsServices atsServices;
   @Mock private IAttributeResolver attributeResolver;
   // @formatter:on

   public static final ArtifactToken workPackageArt = ArtifactToken.valueOf(5, "Work Package", COMMON);
   private final IAtsTeamWorkflow teamWf1 = new TeamWorkflow(null, null, Everyone);

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);

      when(atsServices.getAttributeResolver()).thenReturn(attributeResolver);
   }

   @org.junit.Test
   public void testTeamWorkflow() throws Exception {
      when(attributeResolver.getSoleAttributeValue(teamWf1, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL)).thenReturn(ArtifactId.SENTINEL);

      Pair<String, Boolean> result = ColorTeamColumn.getWorkItemColorTeam(teamWf1, atsServices);
      assertEquals("", result.getFirst());

      when(attributeResolver.getSoleAttributeValue(teamWf1, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL)).thenReturn(workPackageArt);

      result = ColorTeamColumn.getWorkItemColorTeam(teamWf1, atsServices);
      assertEquals("", result.getFirst());

      when(atsServices.getArtifactById("id")).thenReturn(workPackageArt);
      when(attributeResolver.getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, "")).thenReturn("");

      result = ColorTeamColumn.getWorkItemColorTeam(teamWf1, atsServices);
      assertEquals("", result.getFirst());

      when(attributeResolver.getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, "")).thenReturn("red");

      result = ColorTeamColumn.getWorkItemColorTeam(teamWf1, atsServices);
      assertEquals("red", result.getFirst());
   }

   @org.junit.Test
   public void testTask() throws Exception {
      testChildWorkItem(childWorkItem);
   }

   @org.junit.Test
   public void testReview() throws Exception {
      testChildWorkItem(peerReview);
   }

   private void testChildWorkItem(IAtsWorkItem childWorkItem) {
      when(attributeResolver.getSoleAttributeValue(childWorkItem, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL)).thenReturn(ArtifactId.SENTINEL);

      Pair<String, Boolean> result = ColorTeamColumn.getWorkItemColorTeam(childWorkItem, atsServices);
      assertEquals("", result.getFirst());

      when(childWorkItem.getParentTeamWorkflow()).thenReturn(teamWf1);
      when(attributeResolver.getSoleAttributeValue(teamWf1, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL)).thenReturn(ArtifactId.SENTINEL);

      result = ColorTeamColumn.getWorkItemColorTeam(childWorkItem, atsServices);
      assertEquals("", result.getFirst());

      when(attributeResolver.getSoleAttributeValue(teamWf1, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL)).thenReturn(workPackageArt);

      result = ColorTeamColumn.getWorkItemColorTeam(childWorkItem, atsServices);
      assertEquals("", result.getFirst());

      when(attributeResolver.getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, "")).thenReturn("");

      result = ColorTeamColumn.getWorkItemColorTeam(childWorkItem, atsServices);
      assertEquals("", result.getFirst());

      when(attributeResolver.getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, "")).thenReturn("red");

      result = ColorTeamColumn.getWorkItemColorTeam(childWorkItem, atsServices);
      assertEquals("red", result.getFirst());
   }

}
