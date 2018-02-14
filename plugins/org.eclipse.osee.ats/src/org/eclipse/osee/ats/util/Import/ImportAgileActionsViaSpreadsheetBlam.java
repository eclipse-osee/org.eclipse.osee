/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.Import;

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
