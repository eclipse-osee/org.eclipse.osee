/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Shows a selected user's resolved but not closed peer review defects.
 */
public class UsersOpenDefectItemsNavigateItem extends OpenDefectItemsNavigateItem {

   public UsersOpenDefectItemsNavigateItem() {
      super("User's Open Defect Items");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      UserListDialog dialog = new UserListDialog(Displays.getActiveShell(), "Select User", Active.Active, true);
      dialog.setMultiSelect(false);
      if (dialog.open() != 0) {
         return;
      }
      AtsUser selectedUser = dialog.getSelection();
      if (selectedUser == null) {
         return;
      }
      AtsUser user = AtsApiService.get().getUserService().getUserById(selectedUser);
      if (user == null) {
         return;
      }
      openDefectItems(user,
         String.format("Find all open Defects [%s] Originated that have a Resolution.\n\nContinue?", user.getName()),
         String.format("User's Open Defect Items - %s (Read-Only)", user.getName()), tableLoadOptions);
   }
}
