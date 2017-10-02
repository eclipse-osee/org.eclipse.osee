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
package org.eclipse.osee.ats.core.column;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ActionableItemsColumn}
 *
 * @author Donald G. Dunne
 */
public class ActionableItemsColumnTest {

   // @formatter:off
   @Mock private IAtsAction action;

   @Mock private IAtsTeamWorkflow teamWf1;
   @Mock private IAtsActionableItem aia1;
   @Mock private IAtsActionableItem aia2;

   @Mock private IAtsTeamWorkflow teamWf2;
   @Mock private IAtsActionableItem aia3;
   // @formatter:on

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);

      when(action.getTeamWorkflows()).thenReturn(Arrays.asList(teamWf1, teamWf2));

      when(teamWf1.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(aia1, aia2));

      when(teamWf2.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(aia3, aia2));
   }

   @org.junit.Test
   public void testGetActionableItems_action() throws Exception {
      Collection<IAtsActionableItem> ais = ActionableItemsColumn.getActionableItems(action);

      Assert.assertEquals(3, ais.size());
      Assert.assertTrue(ais.contains(aia1));
      Assert.assertTrue(ais.contains(aia2));
      Assert.assertTrue(ais.contains(aia3));
   }

   @org.junit.Test
   public void testGetActionableItems_teamWf() throws Exception {
      Collection<IAtsActionableItem> ais = ActionableItemsColumn.getActionableItems(teamWf1);

      Assert.assertEquals(2, ais.size());
      Assert.assertTrue(ais.contains(aia1));
      Assert.assertTrue(ais.contains(aia2));
   }

   @org.junit.Test
   public void testGetActionableItems_review_peerToPeerInheritsParent() throws Exception {
      IAtsPeerToPeerReview peer = Mockito.mock(IAtsPeerToPeerReview.class);
      when(peer.getParentTeamWorkflow()).thenReturn(teamWf1);
      when(teamWf1.notEqual(peer)).thenReturn(true);

      Collection<IAtsActionableItem> ais = ActionableItemsColumn.getActionableItems(peer);

      Assert.assertEquals(2, ais.size());
      Assert.assertTrue(ais.contains(aia1));
      Assert.assertTrue(ais.contains(aia2));
   }

   @org.junit.Test
   public void testGetActionableItems_review_standalone_peerToPeer() throws Exception {
      IAtsPeerToPeerReview peer = Mockito.mock(IAtsPeerToPeerReview.class);
      when(peer.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(aia3, aia2));

      Collection<IAtsActionableItem> ais = ActionableItemsColumn.getActionableItems(peer);

      Assert.assertEquals(2, ais.size());
      Assert.assertTrue(ais.contains(aia2));
      Assert.assertTrue(ais.contains(aia3));
   }

   @org.junit.Test
   public void testGetActionableItems_review_standalone_decision() throws Exception {
      IAtsDecisionReview decision = Mockito.mock(IAtsDecisionReview.class);
      when(decision.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(aia3, aia2));

      Collection<IAtsActionableItem> ais = ActionableItemsColumn.getActionableItems(decision);

      Assert.assertEquals(2, ais.size());
      Assert.assertTrue(ais.contains(aia2));
      Assert.assertTrue(ais.contains(aia3));
   }

   @org.junit.Test
   public void testGetActionableItemsStr() throws Exception {
      when(aia1.getName()).thenReturn("AI 1");
      when(aia2.getName()).thenReturn("AI 2");

      String results = new ActionableItemsColumn(null).getActionableItemsStr(teamWf1);

      Assert.assertTrue(results.contains("AI 1"));
      Assert.assertTrue(results.contains("AI 2"));
      Assert.assertEquals(2, results.split(", ").length);
   }

}
