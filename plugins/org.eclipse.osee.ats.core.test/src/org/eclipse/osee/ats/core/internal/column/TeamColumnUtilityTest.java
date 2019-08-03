/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column;

import static org.mockito.Mockito.when;
import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @tests TeamColumnUtility
 * @author Donald G. Dunne
 */
public class TeamColumnUtilityTest {

   private static final String TEAM_NAME = "Team Name";
   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf;
   @Mock private IAtsTeamDefinition teamDef;
   @Mock private IAtsAbstractReview review;
   @Mock private IAtsPeerToPeerReview standAloneReview;
   @Mock private IAtsActionableItem ai;
   @Mock private IAtsWorkItemService workItemService;
   @Mock private IAtsReviewService reviewService;
   @Mock private AtsApi atsApi;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(teamWf.getTeamDefinition()).thenReturn(teamDef);
      when(review.getParentTeamWorkflow()).thenReturn(teamWf);
      when(teamDef.toString()).thenReturn(TEAM_NAME);
      when(standAloneReview.getParentTeamWorkflow()).thenReturn(null);
      when(standAloneReview.getActionableItems()).thenReturn(Collections.singleton(ai));
      when(teamDef.getName()).thenReturn(TEAM_NAME);
      when(reviewService.isStandAloneReview(standAloneReview)).thenReturn(true);
      when(ai.getTeamDefinitionInherited()).thenReturn(teamDef);
      when(ai.getTeamDefinition()).thenReturn(teamDef);
      when(atsApi.getReviewService()).thenReturn(reviewService);
   }

   @Test
   public void testGetColumnText() throws Exception {
      TeamColumn utility = new TeamColumn(atsApi);

      Assert.assertEquals(TEAM_NAME, utility.getColumnText(teamWf));
      Assert.assertEquals(TEAM_NAME, utility.getColumnText(review));
      Assert.assertEquals(TEAM_NAME, utility.getColumnText(standAloneReview));
   }
}
