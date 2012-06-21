/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal.model;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.workdef.api.ReviewBlockType;
import org.eclipse.osee.ats.workdef.api.StateEventType;
import org.eclipse.osee.ats.workdef.impl.internal.model.PeerReviewDefinition;
import org.junit.Test;

/**
 * Test case for {@link PeerReviewDefinition}
 *
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionTest {

   @Test
   public void testGetSetName() {
      IAtsPeerReviewDefinition rev = new PeerReviewDefinition("review");
      Assert.assertEquals("review", rev.getName());
      rev.setName("new rev");
      Assert.assertEquals("new rev", rev.getName());
   }

   @Test
   public void testGetSetDescription() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("", item.getDescription());
      item.setDescription("desc");
      Assert.assertEquals("desc", item.getDescription());
   }

   @Test
   public void testGetSetLocation() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("", item.getLocation());
      item.setLocation("loc");
      Assert.assertEquals("loc", item.getLocation());
   }

   @Test
   public void testGetSetBlockingType() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getBlockingType());
      item.setBlockingType(ReviewBlockType.Commit);
      Assert.assertEquals(ReviewBlockType.Commit, item.getBlockingType());
   }

   @Test
   public void testGetSetStateEventType() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getStateEventType());
      item.setStateEventType(StateEventType.CommitBranch);
      Assert.assertEquals(StateEventType.CommitBranch, item.getStateEventType());
   }

   @Test
   public void testGetAssignees() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertTrue(item.getAssignees().isEmpty());
      item.getAssignees().add("Joe");
      Assert.assertEquals(1, item.getAssignees().size());
      Assert.assertEquals("Joe", item.getAssignees().iterator().next());
   }

   @Test
   public void testToString() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertEquals("review", item.toString());
   }

   @Test
   public void testGetTitle() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getReviewTitle());
      item.setReviewTitle("title");
      Assert.assertEquals("title", item.getReviewTitle());
   }

   @Test
   public void testGetSetRelatedToState() {
      IAtsPeerReviewDefinition item = new PeerReviewDefinition("review");
      Assert.assertNull(item.getRelatedToState());
      item.setRelatedToState("Implement");
      Assert.assertEquals("Implement", item.getRelatedToState());
   }

}
