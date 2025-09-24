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

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch.actions;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class SetAsFavoriteAction extends Action {

   private final XBranchWidget xBranchViewer;

   public SetAsFavoriteAction(XBranchWidget xBranchViewer) {
      super("Toggle Branch as Favorite");
      this.xBranchViewer = xBranchViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_FAVORITE_OVERLAY);
   }

   @Override
   public void run() {
      User user;
      try {
         user = OseeApiService.getUserArt();
         if (user.isSystemUser()) {
            AWorkbench.popup("Can not set favorite as system user");
            return;
         }
         List<BranchToken> branches = xBranchViewer.getSelectedBranches();
         if (branches.isEmpty()) {
            AWorkbench.popup("Must select branches");
            return;
         }
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch Favorites",
            "Toggle Branch Favorites for " + branches.size() + " branch(s)")) {
            // Make sure we have latest artifact
            user.reloadAttributesAndRelations();
            for (BranchId branch : branches) {
               user.toggleFavoriteBranch(branch);
            }
            user.persist("Toggle Branch Favorites");
            xBranchViewer.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

}
