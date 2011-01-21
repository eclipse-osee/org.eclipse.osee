/*
 * Created on Jan 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.editor.stateItem;

import static org.junit.Assert.assertFalse;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewState;
import org.eclipse.osee.ats.editor.stateItem.AtsPeerToPeerReviewPrepareStateItem;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsPeerToPeerReviewPrepareStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareStateItemTest {

   public static PeerToPeerReviewArtifact peerRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());

      if (peerRevArt == null) {
         // setup fake review artifact with decision options set
         peerRevArt =
            (PeerToPeerReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.PeerToPeerReview,
               AtsUtil.getAtsBranch());
         peerRevArt.setName(getClass().getSimpleName());
         // Setup actionable item so don't get error that there is no parent team workflow
         peerRevArt.getActionableItemsDam().addActionableItem(
            DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code));
         peerRevArt.persist();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(AtsPeerToPeerReviewPrepareStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioning() throws OseeCoreException {
      Assert.assertNotNull(peerRevArt);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam(AtsAttributeTypes.ReviewBlocks.getUnqualifiedName());
      decisionComboDam.setDataStrings(new String[] {"None", "Transition", "Commit"});
      Composite comp = new Composite(Displays.getActiveShell(), SWT.None);
      decisionComboDam.createWidgets(comp, SWT.NONE);
      decisionComboDam.setEnabled(true);
      decisionComboDam.setRequiredEntry(true);

      // verify enabled and required (Default)
      Assert.assertNull(peerRevArt.getParentSMA()); // condition that causes combo to disable
      Assert.assertTrue(decisionComboDam.getComboBox().isEnabled());
      Assert.assertTrue(decisionComboDam.isRequiredEntry());

      StateDefinition reviewStateDef =
         peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Prepare.getPageName());

      // make call to state item that should 
      AtsPeerToPeerReviewPrepareStateItem stateItem = new AtsPeerToPeerReviewPrepareStateItem();
      stateItem.xWidgetCreated(decisionComboDam, null, reviewStateDef, peerRevArt, null, true);

      // verify the decision combo has been disabled
      Assert.assertFalse(decisionComboDam.getComboBox().isEnabled());
      Assert.assertFalse(decisionComboDam.isRequiredEntry());

   }
}
