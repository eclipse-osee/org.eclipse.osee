/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test.testDb;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author B1375980
 */
public class CustomizeDemoTableTestUtil extends XViewerCustomizeDialog {

   /**
    * @param viewer
    */
   public CustomizeDemoTableTestUtil(XViewer viewer) {
      super(viewer);
   }

   /**
    * for testing purposes - simulate customization view
    */
   public Control createDialogArea(Composite parent) {
      OseeLog.log(AtsPlugin.class, Level.INFO, "===> Simulating CustomizationView " + "\"...");
      return super.createDialogArea(parent);
   }

   /**
    * for testing purposes - simulate the add all button click
    */
   public void handleAddAllItemButtonClick() {
      OseeLog.log(AtsPlugin.class, Level.INFO, "===> Simulating CustomizationView Add All Columns" + "\"...");
      super.handleAddAllItem();
      super.handleLoadConfigCust();
   }
}
