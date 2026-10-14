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

package org.eclipse.osee.ats.ide.util.Import.action;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetBlamTest extends AbstractBlamTest {

   public ImportActionsViaSpreadsheetBlamTest() {
      super(new ImportActionsViaSpreadsheetBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(ImportActionsViaSpreadsheetBlam.EXCEL_SPREADSHEET_SAVED_AS_XML, "C:/MySpreadsheet.xml");
      setSelected(ImportActionsViaSpreadsheetBlam.EMAIL_POCS, true);
      setSelected(ImportActionsViaSpreadsheetBlam.PERSIST, true);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(ImportActionsViaSpreadsheetBlam.EXCEL_SPREADSHEET_SAVED_AS_XML));
      rd.assertNotNull(varMap.getValue(ImportActionsViaSpreadsheetBlam.EMAIL_POCS));
      rd.assertNotNull(varMap.getValue(ImportActionsViaSpreadsheetBlam.PERSIST));

      // Operation and vars tested in another test

   }

}
