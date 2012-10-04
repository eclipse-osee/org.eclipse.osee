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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      boolean enabled = false;

      if (!structuredSelection.isEmpty()) {
         Object selectedObject = structuredSelection.getFirstElement();

         if (selectedObject instanceof TransactionRecord) {
            enabled = ((TransactionRecord) selectedObject).getTxType() != TransactionDetailsType.Baselined;
         } else if (selectedObject instanceof IOseeBranch) {
            enabled = true;
         }
      }

      return enabled;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (!selection.isEmpty()) {
         Object selectedObject = selection.getFirstElement();
         try {
            if (selectedObject instanceof TransactionRecord) {
               ChangeUiUtil.open((TransactionRecord) selectedObject);
            } else if (selectedObject instanceof IOseeBranch) {
               ChangeUiUtil.open((IOseeBranch) selectedObject);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }
}
