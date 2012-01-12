/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.review;

import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workflow.transition.MockTransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test unit for {@link PeerReviewDefinitionManager}
 * 
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionManagerTest extends PeerReviewDefinitionManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreatePeerReviewDuringTransition() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("PeerReviewDefinitionManagerTest");

      // configure WorkDefinition to create a new Review on transition to Implement
      StateDefinition implement = AtsTestUtil.getImplementStateDef();

      PeerReviewDefinition revDef = new PeerReviewDefinition("Create New on Implement");
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setDescription("the description");
      revDef.setRelatedToState(implement.getPageName());
      revDef.setStateEventType(StateEventType.TransitionTo);
      revDef.setReviewTitle("This is my review title");
      revDef.getAssignees().add(SystemUser.UnAssigned.getUserId());

      implement.getPeerReviews().add(revDef);

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      MockTransitionHelper helper =
         new MockTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), implement.getPageName(),
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transMgr.handleAll();
      transaction.execute();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      PeerToPeerReviewArtifact decArt = (PeerToPeerReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(PeerToPeerReviewState.Prepare.getPageName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(implement.getPageName(), decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
