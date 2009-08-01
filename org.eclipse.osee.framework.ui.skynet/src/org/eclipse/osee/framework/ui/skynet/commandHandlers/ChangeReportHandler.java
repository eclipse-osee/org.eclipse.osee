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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportHandler extends AbstractHandler {
   
   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection = (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      if (!selection.isEmpty()) {
         Object selectedObject = selection.getFirstElement();
         try {
            if (selectedObject instanceof TransactionId) {
               ChangeView.open((TransactionId) selectedObject);
            } else if (selectedObject instanceof Branch) {
               ChangeView.open((Branch) selectedObject);
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean enabled = false;

      IStructuredSelection selection = (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      if (!selection.isEmpty()) {
         Object selectedObject = selection.getFirstElement();

         if (selectedObject instanceof TransactionId) {
            enabled = ((TransactionId) selectedObject).getTxType() != TransactionDetailsType.Baselined;
         } else if(selectedObject instanceof Branch){
            enabled = true;
         }
      }

      return enabled;
   }
}
