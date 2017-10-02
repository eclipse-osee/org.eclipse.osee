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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeBranchArchivedStateDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeBranchStateDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeBranchTypeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Megumi Telles
 */
public class PromptChangeUtil {

   public static boolean promptChangeBranchType(final Collection<TreeItem> branches)  {
      ChangeBranchTypeDialog ld = new ChangeBranchTypeDialog(Displays.getActiveShell());
      int result = ld.open();
      if (result == 0) {
         BranchType type = ld.getSelection();
         for (TreeItem item : branches) {
            BranchId branch = (BranchId) item.getData();
            BranchManager.setType(branch, type);
         }
         return true;
      }
      return false;
   }

   public static boolean promptChangeBranchState(final Collection<TreeItem> branches)  {
      ChangeBranchStateDialog ld = new ChangeBranchStateDialog(Displays.getActiveShell());
      int result = ld.open();
      if (result == 0) {
         BranchState state = ld.getSelection();
         for (TreeItem item : branches) {
            BranchManager.setState((BranchId) item.getData(), state);
         }
         return true;
      }
      return false;
   }

   public static boolean promptChangeBranchArchivedState(final Collection<TreeItem> branches)  {
      ChangeBranchArchivedStateDialog ld = new ChangeBranchArchivedStateDialog(Displays.getActiveShell());
      int result = ld.open();
      if (result == 0) {
         BranchArchivedState state = ld.getSelection();
         for (TreeItem item : branches) {
            BranchManager.setArchiveState((BranchId) item.getData(), state);
         }
         return true;
      }
      return false;
   }

}
