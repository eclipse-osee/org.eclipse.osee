/*
 * Created on Jan 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.review.role.Role;
import org.eclipse.osee.ats.core.review.role.UserRole;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsPeerToPeerReviewReviewStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewStateItemTest {

   public static PeerToPeerReviewArtifact peerRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());

      if (peerRevArt == null) {
         // setup fake review artifact with decision options set
         peerRevArt = ReviewManager.createNewPeerToPeerReview(null, getClass().getName(), "", null);
         peerRevArt.setName(getClass().getSimpleName());
         peerRevArt.persist();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(AtsPeerToPeerReviewReviewStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioned() throws OseeCoreException {
      Assert.assertNotNull(peerRevArt);

      // assignee should be user creating review
      Assert.assertEquals(1, peerRevArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUser(), peerRevArt.getStateMgr().getAssignees().iterator().next());

      // set roles
      UserRole userRole = new UserRole(Role.Author, UserManager.getUserByName("Joe Smith"));
      peerRevArt.getUserRoleManager().addOrUpdateUserRole(userRole, false, null);
      userRole = new UserRole(Role.Reviewer, UserManager.getUserByName("Alex Kay"));
      peerRevArt.getUserRoleManager().addOrUpdateUserRole(userRole, false, null);
      peerRevArt.persist();

      // assignee should be user roles
      Assert.assertEquals(2, peerRevArt.getStateMgr().getAssignees().size());

      // change assignees back to single user so can test transition
      peerRevArt.getStateMgr().setAssignee(UserManager.getUser());
      peerRevArt.persist();
      Assert.assertEquals(1, peerRevArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(UserManager.getUser(), peerRevArt.getStateMgr().getAssignees().iterator().next());

      IWorkPage fromState = peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Prepare.getPageName());
      IWorkPage toState = peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Review.getPageName());

      // make call to state item that should set options based on artifact's attribute value
      AtsPeerToPeerReviewReviewStateItem stateItem = new AtsPeerToPeerReviewReviewStateItem();
      stateItem.transitioned(peerRevArt, fromState, toState, Arrays.asList((IBasicUser) UserManager.getUser()), null);

      // Joe and Alex should have been added to assignees
      Assert.assertEquals(2, peerRevArt.getStateMgr().getAssignees().size());
      boolean joeFound = false, alexFound = false;
      for (IBasicUser user : peerRevArt.getStateMgr().getAssignees()) {
         if (user.getName().equals("Joe Smith")) {
            joeFound = true;
         }
         if (user.getName().equals("Alex Kay")) {
            alexFound = true;
         }
      }
      Assert.assertTrue("Joe should have been added as assignee", joeFound);
      Assert.assertTrue("Alex should have been added as assignee", alexFound);
   }

}
