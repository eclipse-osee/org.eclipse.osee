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
package org.eclipse.osee.ats.ide.integration.tests.ats.navigate;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Megumi Telles
 */
public class CustomizeTableDialog extends XViewerCustomizeDialog {

   public boolean DEBUG = false;

   public CustomizeTableDialog(XViewer viewer) {
      super(viewer);
   }

   /**
    * for testing purposes - simulate customization view
    */
   @Override
   public Control createDialogArea(Composite parent) {
      if (DEBUG) {
         OseeLog.log(CustomizeTableDialog.class, Level.INFO, "===> Simulating CustomizationView " + "\"...");
      }
      return super.createDialogArea(parent);
   }

   /**
    * for testing purposes - simulate the add all button click
    */
   public void handleAddAllItemButtonClick() {
      if (DEBUG) {
         OseeLog.log(CustomizeTableDialog.class, Level.INFO,
            "===> Simulating CustomizationView Add All Columns" + "\"...");
      }
      super.handleAddAllItem();
      super.handleLoadConfigCust();
   }
}
