/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.operation;

import java.util.Collection;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.operation.ModifyActionableItemsBlam;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ModifyActionableItemsBlam}
 *
 * @author Donald G. Dunne
 */
public class ModifyActionableItemsBlamTest {

   @Before
   @After
   public void setupCleanup() {
      BlamEditor.closeAll();
   }

   @Test
   public void testModifyActionableItemsBlam() {
      BlamEditor.closeAll();
      ModifyActionableItemsBlam blam = new ModifyActionableItemsBlam();
      TeamWorkFlowArtifact sawCodeCommittedWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeCommittedWf();
      blam.setDefaultTeamWorkflow(sawCodeCommittedWf);
      BlamEditor.edit(blam);
      Collection<BlamEditor> editors = BlamEditor.getEditors();
      Assert.assertEquals(1, editors.size());

      Assert.assertEquals(1, blam.getWorkflowTreeItemCount());
      Assert.assertEquals(2, blam.getOtherTreeItemCount());
      Assert.assertTrue(blam.getNewTreeItemCount() >= 5);
   }

}
