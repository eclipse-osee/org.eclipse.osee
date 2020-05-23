/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import java.io.File;
import org.eclipse.osee.framework.core.util.OseeInf;

/**
 * @author Donald G. Dunne
 */
public class ImportAgileActionsViaSpreadsheetBlam extends ImportActionsViaSpreadsheetBlam {

   @Override
   public String getName() {
      return "Import Agile Actions Via Spreadsheet";
   }

   @Override
   protected boolean includeGoalWidget() {
      return false;
   }

   @Override
   public File getSampleSpreadsheetFile() throws Exception {
      return OseeInf.getResourceAsFile("atsImport/Agile_Action_Import.xml", getClass());
   }

}
