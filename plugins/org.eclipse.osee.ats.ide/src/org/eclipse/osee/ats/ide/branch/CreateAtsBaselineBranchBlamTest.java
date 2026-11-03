/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.branch;

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class CreateAtsBaselineBranchBlamTest extends AbstractBlamTest {

   public CreateAtsBaselineBranchBlamTest() {
      super(new CreateAtsBaselineBranchBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(XWidgetLabels.PARENT_BRANCH, DemoBranches.SAW_Bld_2);

      String uniqueBranchName = getClass().getSimpleName() + " - " + Lib.generateId();
      setSelected(XWidgetLabels.BRANCH_NAME, uniqueBranchName);
      rd.logf("Using branch name [%s]", uniqueBranchName);

      editor.executeBlam();

      Branch newBranch = (Branch) BranchManager.getBranch(uniqueBranchName);
      if (newBranch == null || newBranch.isInvalid()) {
         rd.errorf("Branch not created!");
         return;
      }
      if (!newBranch.getBranchType().isBaselineBranch()) {
         rd.errorf("Branch not BASELINE");
      }

      AtsApiService.get().getBranchService().deleteBranch(newBranch);
   }

}
