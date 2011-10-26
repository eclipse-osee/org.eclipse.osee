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
public class FavoriteActionTest extends AbstractAtsActionTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Assert.assertFalse(UserManager.getUser().getRelatedArtifacts(AtsRelationTypes.FavoriteUser_Artifact).contains(
         AtsTestUtil.getTeamWf()));
      FavoriteAction action = createAction();
      action.runWithException();
      Assert.assertTrue(UserManager.getUser().getRelatedArtifacts(AtsRelationTypes.FavoriteUser_Artifact).contains(
         AtsTestUtil.getTeamWf()));
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public FavoriteAction createAction() {
      FavoriteAction action = new FavoriteAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
      action.setPrompt(false);
      return action;
   }

}
