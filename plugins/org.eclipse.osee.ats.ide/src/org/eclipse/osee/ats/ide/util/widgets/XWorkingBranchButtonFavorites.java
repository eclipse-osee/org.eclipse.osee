/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonFavorites extends XWorkingBranchButtonAbstract {

   public static String WIDGET_NAME = "XWorkingBranchButtonFavorites";

   @Override
   protected void initButton(Button button) {
      button.setToolTipText("Toggle Working Branch as Favorite");
      button.setImage(ImageManager.getImage(AtsImage.FAVORITE));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            markWorkingBranchAsFavorite();
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(!disableAll && isWorkingBranchInWork());
   }

   private void markWorkingBranchAsFavorite() {
      try {
         User user = UserManager.getUserByArtId(AtsClientService.get().getUserService().getCurrentUser());
         // Make sure we have latest artifact
         user.reloadAttributesAndRelations();
         if (user.isSystemUser()) {
            AWorkbench.popup("Can't set preference as System User = " + user);
            return;
         }
         BranchId branch = getTeamArt().getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isFavorite = user.isFavoriteBranch(branch);
         String message = String.format("Working branch is currently [%s]\n\nToggle favorite?",
            isFavorite ? "Favorite" : "NOT Favorite");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch as Favorite", message)) {
            user.toggleFavoriteBranch(branch);
            OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.FavoritesUpdated, branch));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
