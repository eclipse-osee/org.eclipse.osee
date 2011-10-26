/*
 * Created on Oct 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class SubscribedActionTest extends AbstractAtsActionTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Assert.assertFalse(UserManager.getUser().getRelatedArtifacts(AtsRelationTypes.SubscribedUser_Artifact).contains(
         AtsTestUtil.getTeamWf()));
      SubscribedAction action = createAction();
      action.runWithException();
      Assert.assertTrue(UserManager.getUser().getRelatedArtifacts(AtsRelationTypes.SubscribedUser_Artifact).contains(
         AtsTestUtil.getTeamWf()));
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public SubscribedAction createAction() {
      SubscribedAction action = new SubscribedAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
      action.setPrompt(false);
      return action;
   }

}
