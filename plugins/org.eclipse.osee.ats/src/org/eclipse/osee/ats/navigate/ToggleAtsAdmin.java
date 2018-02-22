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
package org.eclipse.osee.ats.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ToggleAtsAdmin extends XNavigateItemAction {

   public ToggleAtsAdmin(XNavigateItem parent) {
      super(parent, "Toggle ATS Admin - Temporary", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      run();
   }

   public static void run() {
      try {
         if (!AtsGroup.AtsTempAdmin.isCurrentUserMember()) {
            AWorkbench.popup("Current User not configured for Temporary Admin");
            return;
         }
         boolean isAdmin = AtsClientService.get().getUserService().isAtsAdmin();
         String message = "Currently " + (isAdmin ? "ADMIN" : "NOT ADMIN") + " - Toggle?";
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Toggle ATS Admin", message)) {

            if (!isAdmin) {
               AtsGroup.AtsAdmin.setTemporaryOverride(true);
               SystemGroup.OseeAdmin.setTemporaryOverride(true);
            } else {
               AtsGroup.AtsAdmin.removeTemporaryOverride();
               SystemGroup.OseeAdmin.removeTemporaryOverride();
            }
            AtsClientService.get().getUserService().isAtsAdmin(false);
            NavigateViewItems.getInstance().clearCaches();
            for (WorkflowEditor editor : WorkflowEditor.getWorkflowEditors()) {
               editor.refreshPages();
            }
            NavigateView.getNavigateView().refreshData();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
