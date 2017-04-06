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

import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.model.PeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.junit.Assert;
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
