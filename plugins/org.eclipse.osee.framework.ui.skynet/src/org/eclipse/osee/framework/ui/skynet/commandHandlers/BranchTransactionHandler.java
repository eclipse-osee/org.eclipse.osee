/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class BranchTransactionHandler extends CommandHandler {

   @Override
   public boolean isEnabledWithException(IStructuredSelection selection) {
      boolean enabled = false;

      if (!selection.isEmpty()) {
         if (selection.size() == 1) {
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof BranchId) {
               enabled = true;
            }
         } else {
            enabled = false;
         }
      }
      return enabled;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (!selection.isEmpty()) {
         try {
            if (selection.size() == 1) {
               Object selectedObject = selection.getFirstElement();
               ChangeUiUtil.open((BranchId) selectedObject, true);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }
}
