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

package org.eclipse.osee.define.ide.traceability.blam;

import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class AddTraceMarksToTraceUnitsBlamTest extends AbstractBlamTest {

   public AddTraceMarksToTraceUnitsBlamTest() {
      super(new AddTraceMarksToTraceUnitsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(XWidgetLabels.SELECT_DIR, "my dir");
      setSelected(XWidgetLabels.SELECT_FILE, "my file");
      setSelected(AddTraceMarksToTraceUnitsBlam.INCLUDE_SUBFOLDERS, true);
      setSelected(AddTraceMarksToTraceUnitsBlam.REQUIREMENTS_BRANCH, DemoBranches.SAW_Bld_2); // TBD: set branch

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(XWidgetLabels.SELECT_DIR));
      rd.assertNotNull(varMap.getValue(XWidgetLabels.SELECT_FILE));
      rd.assertNotNull(varMap.getValue(AddTraceMarksToTraceUnitsBlam.INCLUDE_SUBFOLDERS));
      rd.assertNotNull(varMap.getValue(AddTraceMarksToTraceUnitsBlam.REQUIREMENTS_BRANCH));

      rd.warning("Future - Needs Operation Test");
   }

}
