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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Karol M. Wilk
 * @author Jeff C. Phillips
 */
public final class PurgeBranchHandler extends GeneralBranchHandler {

   public PurgeBranchHandler() {
      super(OpTypeEnum.PURGE);
   }

   @Override
   public void performOperation(List<Branch> branches) {
      List<Branch> hasChildren = new LinkedList<Branch>();
      for (Branch branch : branches) {
         try {
            if (branch.getAllChildBranches(false).isEmpty()) {
               BranchManager.purgeBranch(branch);
            } else {
               hasChildren.add(branch);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      if (!hasChildren.isEmpty()) {
         StringBuilder message = new StringBuilder();
         message.append("Can not purge branches that have children.\n");
         for (Branch branch : hasChildren) {
            message.append("Branch ");
            message.append(branch.getName());
            message.append(" has child branches: ");
            try {
               message.append(Collections.toString(", ", branch.getChildBranches(true)));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            message.append(".\n");
         }
         MessageDialog.openError(Displays.getActiveShell(), "Purge Branch", message.toString());
      }
   }
}
