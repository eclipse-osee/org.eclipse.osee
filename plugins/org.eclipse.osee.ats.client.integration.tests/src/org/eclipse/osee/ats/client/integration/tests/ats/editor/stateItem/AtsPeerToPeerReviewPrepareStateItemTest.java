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
package org.eclipse.osee.ats.client.integration.tests.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.demo.api.DemoActionableItems;
import org.eclipse.osee.ats.editor.stateItem.AtsPeerToPeerReviewPrepareStateItem;
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
 * Test Case for {@link AtsPeerToPeerReviewPrepareStateItem}
 *
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareStateItemTest {

   public static PeerToPeerReviewArtifact peerRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsClientService.get().getStoreService().isProductionDb());

      if (peerRevArt == null) {
         IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName(),
            AtsClientService.get().getUserService().getCurrentUser());
         peerRevArt = PeerToPeerReviewManager.createNewPeerToPeerReview(
            DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code), getClass().getSimpleName(), null, new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), changes);
         // Setup actionable item so don't get error that there is no parent team workflow
         AtsClientService.get().getWorkItemService().getActionableItemService().addActionableItem(peerRevArt,
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
      Assert.assertNotNull(peerRevArt);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam(AtsAttributeTypes.ReviewBlocks.getUnqualifiedName());
      decisionComboDam.setDataStrings(new String[] {"None", "Transition", "Commit"});
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      Composite comp = new Composite(shell, SWT.None);
      decisionComboDam.createWidgets(comp, SWT.NONE);
      decisionComboDam.setEnabled(true);
      decisionComboDam.setRequiredEntry(true);

      // verify enabled and required (Default)
      Assert.assertNull(peerRevArt.getParentAWA()); // condition that causes combo to disable
      Assert.assertTrue(decisionComboDam.getComboBox().isEnabled());
      Assert.assertTrue(decisionComboDam.isRequiredEntry());

      IAtsStateDefinition reviewStateDef =
         peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Prepare.getName());

      // make call to state item that should
      AtsPeerToPeerReviewPrepareStateItem stateItem = new AtsPeerToPeerReviewPrepareStateItem();
      stateItem.xWidgetCreated(decisionComboDam, null, reviewStateDef, peerRevArt, true);

      // verify the decision combo has been disabled
      Assert.assertFalse(decisionComboDam.getComboBox().isEnabled());
      Assert.assertFalse(decisionComboDam.isRequiredEntry());

   }
}
