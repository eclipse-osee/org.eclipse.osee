/*
 * Created on Jan 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.junit.Assert;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewState;
import org.eclipse.osee.ats.artifact.ReviewManager;
import org.eclipse.osee.ats.editor.stateItem.AtsDecisionReviewPrepareStateItem;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsDecisionReviewPrepareStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItemTest {

   public static DecisionReviewArtifact decRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());

      if (decRevArt == null) {
         // setup fake review artifact with decision options set
         decRevArt =
            (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.DecisionReview,
               AtsUtil.getAtsBranch());
         decRevArt.setName(getClass().getSimpleName());
         decRevArt.persist();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(AtsDecisionReviewPrepareStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioning() throws OseeCoreException {
      Assert.assertNotNull(decRevArt);

      // set valid options
      String decisionOptionStr =
         ReviewManager.getDecisionReviewOptionsString(ReviewManager.getDefaultDecisionReviewOptions());
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist();

      IWorkPage fromState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Prepare.getPageName());
      IWorkPage toState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Decision.getPageName());

      // make call to state item that should set options based on artifact's attribute value
      AtsDecisionReviewPrepareStateItem stateItem = new AtsDecisionReviewPrepareStateItem();
      Result result = stateItem.transitioning(decRevArt, fromState, toState, Arrays.asList(UserManager.getUser()));

      // verify no errors
      Assert.assertTrue(result.getText(), result.isTrue());

      // set invalid options; NoState is invalid, should only be Completed or FollowUp
      decisionOptionStr = decisionOptionStr.replaceFirst("Completed", "NoState");
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist();
      result = stateItem.transitioning(decRevArt, fromState, toState, Arrays.asList(UserManager.getUser()));
      Assert.assertFalse(result.getText(), result.isTrue());

   }

}
