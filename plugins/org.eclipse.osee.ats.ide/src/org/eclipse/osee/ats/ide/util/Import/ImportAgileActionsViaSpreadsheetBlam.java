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
import org.eclipse.osee.ats.ide.util.Import.action.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ImportAgileActionsViaSpreadsheetBlam extends ImportActionsViaSpreadsheetBlam {

   private static final String BLAM_NAME = "Import Agile Actions Via Spreadsheet";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ImportAgileActionsViaSpreadsheetBlam() {
      super(BLAM_NAME, CoreImage.IMPORT);
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   protected boolean includeGoalWidget() {
      return false;
   }

   @Override
   public File getSampleSpreadsheetFile() throws Exception {
      return OseeInf.getResourceAsFile("atsImport/Agile_Action_Import.xml", getClass());
   }

}
