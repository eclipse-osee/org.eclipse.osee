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

package org.eclipse.osee.ats.ide.operation;

import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ConvertWorkflowStatesBlamTest extends AbstractBlamTest {

   public ConvertWorkflowStatesBlamTest() {
      super(new ConvertWorkflowStatesBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(ConvertWorkflowStatesBlam.SOURCE_TEAM_WORKFLOWS, DemoUtil.getSawCodeCommittedWf().getStoreObject());
      setSelected(ConvertWorkflowStatesBlam.FROM_TO_MAP, "mytest:value");
      setSelected(ConvertWorkflowStatesBlam.PERSIST, true);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(ConvertWorkflowStatesBlam.SOURCE_TEAM_WORKFLOWS));
      rd.assertNotNull(varMap.getValue(ConvertWorkflowStatesBlam.FROM_TO_MAP));
      rd.assertNotNull(varMap.getValue(ConvertWorkflowStatesBlam.PERSIST));

      rd.warning("Future - Needs Operation Test");
   }

}
