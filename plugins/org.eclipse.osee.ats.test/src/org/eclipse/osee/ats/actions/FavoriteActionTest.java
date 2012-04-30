/*
 * Created on Oct 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
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
      Assert.assertFalse(AtsUsersClient.getOseeUser().getRelatedArtifacts(AtsRelationTypes.FavoriteUser_Artifact).contains(
         AtsTestUtil.getTeamWf()));
      FavoriteAction action = createAction();
      action.runWithException();
      Assert.assertTrue(AtsUsersClient.getOseeUser().getRelatedArtifacts(AtsRelationTypes.FavoriteUser_Artifact).contains(
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
