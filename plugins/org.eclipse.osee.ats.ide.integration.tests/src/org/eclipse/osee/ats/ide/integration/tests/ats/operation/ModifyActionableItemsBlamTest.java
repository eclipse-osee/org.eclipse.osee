/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.operation;

import java.util.Collection;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
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
   public void testOpenAtsExportBlam() {
      BlamEditor.closeAll();
      ModifyActionableItemsBlam blam = new ModifyActionableItemsBlam();
      TeamWorkFlowArtifact sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      blam.setDefaultTeamWorkflow(sawCodeCommittedWf);
      BlamEditor.edit(blam);
      Collection<BlamEditor> editors = BlamEditor.getEditors();
      Assert.assertEquals(1, editors.size());

      Assert.assertEquals(1, blam.getWorkflowTreeItemCount());
      Assert.assertEquals(2, blam.getOtherTreeItemCount());
      Assert.assertTrue(blam.getNewTreeItemCount() >= 5);
   }

}
