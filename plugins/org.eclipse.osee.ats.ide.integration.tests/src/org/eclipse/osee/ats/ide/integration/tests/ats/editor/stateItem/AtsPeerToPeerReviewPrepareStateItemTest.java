/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.hooks.AtsPeerToPeerReviewPrepareWorkItemHookIde;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsPeerToPeerReviewPrepareWorkItemHookIde}
 *
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareStateItemTest {

   public static IAtsPeerToPeerReview peerRev;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsApiService.get().getStoreService().isProductionDb());

      if (peerRev == null) {
         IAtsChangeSet changes = AtsApiService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName(),
            AtsApiService.get().getUserService().getCurrentUser());
         peerRev = AtsApiService.get().getReviewService().createNewPeerToPeerReview(
            DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code), getClass().getSimpleName(), null, new Date(),
            AtsApiService.get().getUserService().getCurrentUser(), changes);
         // Setup actionable item so don't get error that there is no parent team workflow
         AtsApiService.get().getActionableItemService().addActionableItem(peerRev,
            DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code), changes);
         changes.execute();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(AtsPeerToPeerReviewPrepareStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioning() {
      Assert.assertNotNull(peerRev);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam(AtsAttributeTypes.ReviewBlocks.getUnqualifiedName());
      decisionComboDam.setDataStrings(new String[] {"None", "Transition", "Commit"});
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      Composite comp = new Composite(shell, SWT.None);
      decisionComboDam.createWidgets(comp, SWT.NONE);
      decisionComboDam.setEnabled(true);
      decisionComboDam.setRequiredEntry(true);

      // verify enabled and required (Default)
      Assert.assertNull(peerRev.getParentTeamWorkflow()); // condition that causes combo to disable
      Assert.assertTrue(decisionComboDam.getComboBox().isEnabled());
      Assert.assertTrue(decisionComboDam.isRequiredEntry());

      StateDefinition reviewStateDef =
         peerRev.getWorkDefinition().getStateByName(PeerToPeerReviewState.Prepare.getName());

      // make call to state item that should
      AtsPeerToPeerReviewPrepareWorkItemHookIde stateItem = new AtsPeerToPeerReviewPrepareWorkItemHookIde();
      stateItem.xWidgetCreated(decisionComboDam, null, reviewStateDef, (Artifact) peerRev, true);

      // verify the decision combo has been disabled
      Assert.assertFalse(decisionComboDam.getComboBox().isEnabled());
      Assert.assertFalse(decisionComboDam.isRequiredEntry());

   }
}
