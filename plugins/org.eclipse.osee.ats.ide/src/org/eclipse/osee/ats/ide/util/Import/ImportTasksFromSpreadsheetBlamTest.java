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

package org.eclipse.osee.ats.ide.util.Import;

import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksFromSpreadsheetBlamTest extends AbstractBlamTest {

   public ImportTasksFromSpreadsheetBlamTest() {
      super(new ImportTasksFromSpreadsheetBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(ImportTasksFromSpreadsheetBlam.TASK_IMPORT_SPREADSHEET, "C:/MySpreadsheet.xml");
      setSelected(ImportTasksFromSpreadsheetBlam.TEAM_WORKFLOW, DemoUtil.getSawCodeCommittedWf().getStoreObject());

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(ImportTasksFromSpreadsheetBlam.OPEN_EXCEL_IMPORT_EXAMPLE_SPREADSHEET));

      // Operation and vars tested in another test
   }

}
