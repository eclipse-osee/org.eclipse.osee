/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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
      List<TransactionId> records = provider.getSelectedTransactionRecords();
      EntryDialog ed = new EntryDialog("Edit Transaction Record Comment", "Enter Transaction Record comment");
      if (ed.open() == 0) {
         for (TransactionId record : records) {
            try {
               TransactionManager.setTransactionComment(record, ed.getEntry());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
         provider.refreshUI(records);
         AWorkbench.popup("Transaction Record comment(s) updated.");
      }
   }
}