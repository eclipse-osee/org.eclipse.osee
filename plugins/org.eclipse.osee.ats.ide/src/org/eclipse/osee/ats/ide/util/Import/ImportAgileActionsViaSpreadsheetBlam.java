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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.util.Import.action.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

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

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.IMPORT);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.IMPORT);
   }

}
