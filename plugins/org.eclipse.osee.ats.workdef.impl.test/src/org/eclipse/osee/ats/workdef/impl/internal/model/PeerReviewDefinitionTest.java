/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.ReviewBlockType;
import org.eclipse.osee.ats.workdef.api.StateEventType;
import org.junit.Test;

/**
 * Test case for {@link PeerReviewDefinition}
 * 
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionTest {

   @Test
   public void testGetSetName() {
      PeerReviewDefinition rev = new PeerReviewDefinition("review");
      Assert.assertEquals("review", rev.getName());
      rev.setName("new rev");
      Assert.assertEquals("new rev", rev.getName());
   }

   @Test
   public void testGetSetDescription() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("", item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testGetSetLocation() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("", item.getLocation());
      item.setLocation("loc");
      Assert.assertEquals("loc", item.getLocation());
   }

   @Test
   public void testGetSetBlockingType() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getBlockingType());
      item.setBlockingType(ReviewBlockType.Commit);
      Assert.assertEquals(ReviewBlockType.Commit, item.getBlockingType());
   }

   @Test
   public void testGetSetStateEventType() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getStateEventType());
      item.setStateEventType(StateEventType.CommitBranch);
      Assert.assertEquals(StateEventType.CommitBranch, item.getStateEventType());
   }

   @Test
   public void testGetAssignees() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertTrue(item.getAssignees().isEmpty());
      item.getAssignees().add("Joe");
      Assert.assertEquals(1, item.getAssignees().size());
      Assert.assertEquals("Joe", item.getAssignees().iterator().next());
   }

   @Test
   public void testToString() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("review", item.toString());
   }

   @Test
   public void testGetTitle() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getReviewTitle());
      item.setReviewTitle("title");
      Assert.assertEquals("title", item.getReviewTitle());
   }

   @Test
   public void testGetSetRelatedToState() {
      PeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getRelatedToState());
      item.setRelatedToState("Implement");
      Assert.assertEquals("Implement", item.getRelatedToState());
   }

}
