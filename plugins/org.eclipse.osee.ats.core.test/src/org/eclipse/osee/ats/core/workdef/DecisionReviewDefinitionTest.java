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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.review.DecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.model.DecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link DecisionReviewDefinition}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionTest {

   @Test
   public void testGetSetName() {
      DecisionReviewDefinition rev = new DecisionReviewDefinition("review");
      Assert.assertEquals("review", rev.getName());
      rev.setName("new rev");
      Assert.assertEquals("new rev", rev.getName());
   }

   @Test
   public void testGetSetDescription() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertEquals("", item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testGetSetBlockingType() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getBlockingType());
      item.setBlockingType(ReviewBlockType.Commit);
      Assert.assertEquals(ReviewBlockType.Commit, item.getBlockingType());
   }

   @Test
   public void testGetSetStateEventType() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getStateEventType());
      item.setStateEventType(StateEventType.CommitBranch);
      Assert.assertEquals(StateEventType.CommitBranch, item.getStateEventType());
   }

   @Test
   public void testIsAutoTransitionToDecision() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertFalse(item.isAutoTransitionToDecision());
      item.setAutoTransitionToDecision(true);
      Assert.assertTrue(item.isAutoTransitionToDecision());
   }

   @Test
   public void testGetAssignees() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertTrue(item.getAssignees().isEmpty());
      item.getAssignees().add("Joe");
      Assert.assertEquals(1, item.getAssignees().size());
      Assert.assertEquals("Joe", item.getAssignees().iterator().next());
   }

   @Test
   public void testGetOptions() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertTrue(item.getOptions().isEmpty());
      item.getOptions().add(new DecisionReviewOption("Completed"));
      Assert.assertEquals(1, item.getOptions().size());
   }

   @Test
   public void testToString() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertEquals("review", item.toString());
   }

   @Test
   public void testGetTitle() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getReviewTitle());
      item.setReviewTitle("title");
      Assert.assertEquals("title", item.getReviewTitle());
   }

   @Test
   public void testGetSetRelatedToState() {
      DecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getRelatedToState());
      item.setRelatedToState("Implement");
      Assert.assertEquals("Implement", item.getRelatedToState());
   }

}
