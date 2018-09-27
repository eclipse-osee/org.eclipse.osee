/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch.actions;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
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
         user = UserManager.getUser();
         if (user.isSystemUser()) {
            AWorkbench.popup("Can not set favorite as system user");
            return;
         }
         List<BranchId> branches = xBranchViewer.getSelectedBranches();
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
