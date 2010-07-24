/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SaveImportRecordAction extends Action {
   private final ISaveable saveable;
   private final CoverageImport coverageImport;

   public SaveImportRecordAction(CoverageImport coverageImport, ISaveable saveable) {
      super("Save Coverage Import Record");
      this.coverageImport = coverageImport;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      if (MessageDialog.openConfirm(Displays.getActiveShell(), "Save Coverage Import Record",
         "Overwrite coverage import record with current import information?")) {
         try {
            SkynetTransaction transaction =
               new SkynetTransaction(saveable.getBranch(), "Coverage - Save Import Record");
            saveable.saveImportRecord(transaction, coverageImport);
            transaction.execute();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            return;
         }
      }
      try {
         saveable.save();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }
   }
}
