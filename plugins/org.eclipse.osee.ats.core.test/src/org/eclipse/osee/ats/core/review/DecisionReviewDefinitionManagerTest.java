/*
 * Created on Jun 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review;

import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workflow.transition.TestTransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test unit for {@link DecisionReviewDefinitionManager}
 * 
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManagerTest extends DecisionReviewDefinitionManager {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_ToDecision() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - ToDecision");

      // configure WorkDefinition to create a new Review on transition to Implement
      StateDefinition implement = AtsTestUtil.getImplementStateDef();

      DecisionReviewDefinition revDef = new DecisionReviewDefinition("Create New on Implement");
      revDef.setAutoTransitionToDecision(true);
      revDef.setBlockingType(ReviewBlockType.Transition);
      revDef.setDescription("the description");
      revDef.setRelatedToState(implement.getPageName());
      revDef.setStateEventType(StateEventType.TransitionTo);
      revDef.setReviewTitle("This is my review title");
      revDef.getOptions().addAll(DecisionReviewManager.getDefaultDecisionReviewOptions());
      revDef.getAssignees().add(SystemUser.UnAssigned.getUserId());

      implement.getDecisionReviews().add(revDef);

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), implement.getPageName(),
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transMgr.handleAll();
      transaction.execute();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Decision.getPageName(), decArt.getCurrentStateName());
      Assert.assertEquals("UnAssigned", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Transition.name(),
         decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is my review title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(implement.getPageName(), decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

   @org.junit.Test
   public void testCreateDecisionReviewDuringTransition_Prepare() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("DecisionReviewDefinitionManagerTest - Prepare");

      // configure WorkDefinition to create a new Review on transition to Implement
      StateDefinition implement = AtsTestUtil.getImplementStateDef();

      DecisionReviewDefinition revDef = new DecisionReviewDefinition("Create New on Implement");
      revDef.setAutoTransitionToDecision(false);
      revDef.setBlockingType(ReviewBlockType.Commit);
      revDef.setReviewTitle("This is the title");
      revDef.setDescription("the description");
      revDef.setRelatedToState(implement.getPageName());
      revDef.setStateEventType(StateEventType.TransitionTo);
      revDef.getOptions().addAll(DecisionReviewManager.getDefaultDecisionReviewOptions());

      implement.getDecisionReviews().add(revDef);

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("No reviews should be present", 0, ReviewManager.getReviews(teamArt).size());

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      TestTransitionHelper helper =
         new TestTransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), implement.getPageName(),
            Arrays.asList(UserManager.getUser()), null, TransitionOption.None);
      TransitionManager transMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transMgr.handleAll();
      transaction.execute();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("One review should be present", 1, ReviewManager.getReviews(teamArt).size());
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();

      Assert.assertEquals(DecisionReviewState.Prepare.getPageName(), decArt.getCurrentStateName());
      // Current user assigned if non specified
      Assert.assertEquals("Joe Smith", decArt.getStateMgr().getAssigneesStr());
      Assert.assertEquals(ReviewBlockType.Commit.name(), decArt.getSoleAttributeValue(AtsAttributeTypes.ReviewBlocks));
      Assert.assertEquals("This is the title", decArt.getName());
      Assert.assertEquals("the description", decArt.getSoleAttributeValue(AtsAttributeTypes.Description));
      Assert.assertEquals(implement.getPageName(), decArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState));

      AtsTestUtil.validateArtifactCache();
   }

}
