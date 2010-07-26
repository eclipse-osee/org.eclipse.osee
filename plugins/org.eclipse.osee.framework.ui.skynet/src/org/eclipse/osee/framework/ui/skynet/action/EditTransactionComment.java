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
package org.eclipse.osee.framework.ui.skynet.action;

import java.util.ArrayList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditTransactionComment extends Action {

   private final ITransactionRecordSelectionProvider provider;

   public EditTransactionComment(ITransactionRecordSelectionProvider provider) {
      super("Edit Transaction Comment");
      this.provider = provider;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

   @Override
   public void run() {
      if (provider.getSelectedTransactionRecords().isEmpty()) {
         AWorkbench.popup("Transaction Record must be selected.");
         return;
      }
      ArrayList<TransactionRecord> records = provider.getSelectedTransactionRecords();
      EntryDialog ed = new EntryDialog("Edit Transaction Record Comment", "Enter Transaction Record comment");
      if (ed.open() == 0) {
         for (TransactionRecord record : records) {
            try {
               TransactionManager.setTransactionComment(record, ed.getEntry());
               record.setComment(ed.getEntry());
            } catch (OseeDataStoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
         provider.refreshUI(records);
         AWorkbench.popup("Transaction Record comment(s) updated.");
      }

   }
}
