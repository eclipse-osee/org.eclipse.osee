/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenChangeReportByTransactionIdAction extends Action {
   private static final String NAME = "Open Change Report by Transaction Id";

   public OpenChangeReportByTransactionIdAction() {
      super(NAME, IAction.AS_PUSH_BUTTON);
      setId("open.by.transaction.id");
      setToolTipText(NAME);
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.OPEN));
   }

   @Override
   public void run() {
      EntryDialog dialog = new EntryDialog(NAME, "Enter Transaction Id");
      if (dialog.open() == Window.OK) {
         String entry = dialog.getEntry();
         if (Strings.isNumeric(entry)) {
            ChangeUiUtil.open(TransactionManager.getTransaction(Long.valueOf(entry)));
         } else {
            AWorkbench.popup("Entry must be numeric.");
         }
      }
   }
}