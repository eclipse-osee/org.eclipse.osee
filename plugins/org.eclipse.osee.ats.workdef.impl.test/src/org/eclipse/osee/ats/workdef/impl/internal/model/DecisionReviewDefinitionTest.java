/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.workdef.api.ReviewBlockType;
import org.eclipse.osee.ats.workdef.api.StateEventType;
import org.junit.Test;

/**
 * Test case for {@link DecisionReviewDefinition}
 *
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionTest {

   @Test
   public void testGetSetName() {
      IAtsDecisionReviewDefinition rev = new DecisionReviewDefinition("review");
      Assert.assertEquals("review", rev.getName());
      rev.setName("new rev");
      Assert.assertEquals("new rev", rev.getName());
   }

   @Test
   public void testGetSetDescription() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertEquals("", item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testGetSetBlockingType() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getBlockingType());
      item.setBlockingType(ReviewBlockType.Commit);
      Assert.assertEquals(ReviewBlockType.Commit, item.getBlockingType());
   }

   @Test
   public void testGetSetStateEventType() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getStateEventType());
      item.setStateEventType(StateEventType.CommitBranch);
      Assert.assertEquals(StateEventType.CommitBranch, item.getStateEventType());
   }

   @Test
   public void testIsAutoTransitionToDecision() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertFalse(item.isAutoTransitionToDecision());
      item.setAutoTransitionToDecision(true);
      Assert.assertTrue(item.isAutoTransitionToDecision());
   }

   @Test
   public void testGetAssignees() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertTrue(item.getAssignees().isEmpty());
      item.getAssignees().add("Joe");
      Assert.assertEquals(1, item.getAssignees().size());
      Assert.assertEquals("Joe", item.getAssignees().iterator().next());
   }

   @Test
   public void testGetOptions() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertTrue(item.getOptions().isEmpty());
      item.getOptions().add(new DecisionReviewOption("Completed"));
      Assert.assertEquals(1, item.getOptions().size());
   }

   @Test
   public void testToString() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertEquals("review", item.toString());
   }

   @Test
   public void testGetTitle() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getReviewTitle());
      item.setReviewTitle("title");
      Assert.assertEquals("title", item.getReviewTitle());
   }

   @Test
   public void testGetSetRelatedToState() {
      IAtsDecisionReviewDefinition item = new DecisionReviewDefinition("review");
      Assert.assertNull(item.getRelatedToState());
      item.setRelatedToState("Implement");
      Assert.assertEquals("Implement", item.getRelatedToState());
   }

}
