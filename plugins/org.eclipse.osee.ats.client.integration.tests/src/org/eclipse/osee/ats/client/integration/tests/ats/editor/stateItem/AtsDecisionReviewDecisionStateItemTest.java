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
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.editor.stateItem.AtsDecisionReviewDecisionStateItem;
import org.eclipse.osee.ats.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsDecisionReviewDecisionStateItem}
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewDecisionStateItemTest {

   public static DecisionReviewArtifact decRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsClientService.get().getStoreService().isProductionDb());

      if (decRevArt == null) {
         // setup fake review artifact with decision options set
         decRevArt = (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.DecisionReview,
            AtsClientService.get().getAtsBranch());
         decRevArt.setName(getClass().getSimpleName());
         decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions,
            AtsClientService.get().getReviewService().getDecisionReviewOptionsString(
               AtsClientService.get().getReviewService().getDefaultDecisionReviewOptions()));
         decRevArt.persist(getClass().getSimpleName());
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(AtsDecisionReviewDecisionStateItemTest.class.getSimpleName());
   }

   @Test
   public void testXWidgetCreating() {
      Assert.assertNotNull(decRevArt);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam("Decision");
      // set combo values an verify they got set
      decisionComboDam.setDataStrings(new String[] {"One", "Two", "Three"});
      IAtsStateDefinition stateDef = new StateDefinition("Decision");
      Assert.assertEquals("Two", decisionComboDam.getDisplayArray()[2]);

      // make call to state item that should set options based on artifact's attribute value
      AtsDecisionReviewDecisionStateItem stateItem = new AtsDecisionReviewDecisionStateItem();
      Result result = stateItem.xWidgetCreating(decisionComboDam, null, stateDef, decRevArt, true);

      // verify no errors and options are as specified in artifact's attribute
      Assert.assertTrue(result.getText(), result.isTrue());
      Assert.assertEquals("Yes", decisionComboDam.getDisplayArray()[1]);
      Assert.assertEquals("No", decisionComboDam.getDisplayArray()[2]);
   }

   @Test
   public void testGetOverrideTransitionToAssignees() {
      Assert.assertNotNull(decRevArt);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam("Decision");
      // set combo values an verify they got set
      decisionComboDam.setDataStrings(new String[] {"Yes", "No"});

      // Set Yes
      decisionComboDam.set(1);

      AtsDecisionReviewDecisionStateItem stateItem = new AtsDecisionReviewDecisionStateItem();
      Collection<IAtsUser> users = stateItem.getOverrideTransitionToAssignees(decRevArt, decisionComboDam.get());
      Assert.assertEquals(1, users.size());
      Assert.assertEquals(AtsClientService.get().getUserService().getCurrentUser(), users.iterator().next());

      // Set No
      decisionComboDam.set(2);

      users = stateItem.getOverrideTransitionToAssignees(decRevArt, decisionComboDam.get());
      Assert.assertTrue(users.isEmpty());
   }

   @Test
   public void testGetOverrideTransitionToStateName() {
      Assert.assertNotNull(decRevArt);

      // setup fake combo that will hold values
      XComboDam decisionComboDam = new XComboDam("Decision");
      // set combo values an verify they got set
      decisionComboDam.setDataStrings(new String[] {"Yes", "No"});

      // Set Yes
      decisionComboDam.set(1);

      AtsDecisionReviewDecisionStateItem stateItem = new AtsDecisionReviewDecisionStateItem();
      String toStateName = stateItem.getOverrideTransitionToStateName(decRevArt, decisionComboDam);
      Assert.assertEquals(DecisionReviewState.Followup.getName(), toStateName);

      // Set No
      decisionComboDam.set(2);

      toStateName = stateItem.getOverrideTransitionToStateName(decRevArt, decisionComboDam);
      Assert.assertEquals(DecisionReviewState.Completed.getName(), toStateName);
   }

}
