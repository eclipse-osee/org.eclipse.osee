/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   public ToggleAtsAdmin() {
      super("Toggle ATS Admin - Temporary", PluginUiImage.ADMIN, XNavigateItem.DEMO);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      run();
   }

   public static void run() {
      try {
         UserService userService = AtsApiService.get().userService();
         if (!userService.getUserGroup(AtsUserGroups.AtsTempAdmin).isCurrentUserMember()) {
            AWorkbench.popup("Current User not configured for Temporary Admin");
            return;
         }
         boolean isAdmin = AtsApiService.get().getUserService().isAtsAdmin();
         String message = "Currently " + (isAdmin ? "ADMIN" : "NOT ADMIN") + " - Toggle?";
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Toggle Admin", message)) {
            if (!isAdmin) {
               IUserGroup atsAdminGroup = userService.getUserGroup(AtsUserGroups.AtsAdmin);
               if (!atsAdminGroup.isCurrentUserMember()) {
                  atsAdminGroup.addMember(UserManager.getUser(), true);
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
               }

               IUserGroup oseeAdminGroup = userService.getOseeAdmin();
               if (!oseeAdminGroup.isCurrentUserMember()) {
                  oseeAdminGroup.addMember(UserManager.getUser(), true);
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
               }
            } else {
               IUserGroup atsAdminGroup = userService.getUserGroup(AtsUserGroups.AtsAdmin);
               if (atsAdminGroup.isCurrentUserMember()) {
                  atsAdminGroup.removeMember(UserManager.getUser(), true);
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
               }

               IUserGroup oseeAdminGroup = userService.getUserGroup(CoreUserGroups.OseeAdmin);
               if (oseeAdminGroup.isCurrentUserMember()) {
                  oseeAdminGroup.removeMember(UserManager.getUser(), true);
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
               }
            }
            AtsApiService.get().clearCaches();

            for (WorkflowEditor editor : WorkflowEditor.getWorkflowEditors()) {
               editor.refresh();
            }
            if (NavigateView.getNavigateView() != null && NavigateView.isAccessible()) {
               NavigateView.getNavigateView().refreshData();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
