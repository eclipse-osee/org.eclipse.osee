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

package org.eclipse.osee.ats.ide.workflow.duplicate;

import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowBlamTest extends AbstractBlamTest {

   public DuplicateWorkflowBlamTest() {
      super(new DuplicateWorkflowBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(DuplicateWorkflowBlam.TEAM_WORKFLOW, DemoUtil.getSawCodeCommittedWf().getStoreObject());
      setSelected(DuplicateWorkflowBlam.DUPLICATE_METHOD, "my test value");
      setSelected(DuplicateWorkflowBlam.DUPLICATE_TASKS, true);
      setSelected(DuplicateWorkflowBlam.TITLE, "my test value");

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(DuplicateWorkflowBlam.TEAM_WORKFLOW));
      rd.assertNotNull(varMap.getValue(DuplicateWorkflowBlam.DUPLICATE_METHOD));
      rd.assertNotNull(varMap.getValue(DuplicateWorkflowBlam.DUPLICATE_TASKS));
      rd.assertNotNull(varMap.getValue(DuplicateWorkflowBlam.TITLE));

      rd.warning("Future - Needs Operation Test");
   }

}
