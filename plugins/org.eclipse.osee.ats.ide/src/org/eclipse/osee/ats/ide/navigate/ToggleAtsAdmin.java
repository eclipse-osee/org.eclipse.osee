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
package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.core.access.UserGroupService;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.IUserGroup;
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

   public ToggleAtsAdmin(XNavigateItem parent) {
      super(parent, "Toggle ATS Admin - Temporary", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      run();
   }

   public static void run() {
      try {
         if (!AtsClientService.get().getUserGroupService().getUserGroup(
            AtsUserGroups.AtsTempAdmin).isCurrentUserMember()) {
            AWorkbench.popup("Current User not configured for Temporary Admin");
            return;
         }
         boolean isAdmin = AtsClientService.get().getUserService().isAtsAdmin();
         String message = "Currently " + (isAdmin ? "ADMIN" : "NOT ADMIN") + " - Toggle?";
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Toggle Admin", message)) {
            if (!isAdmin) {
               IUserGroup atsAdminGroup = UserGroupService.getAtsAdmin();
               if (!atsAdminGroup.isCurrentUserMember()) {
                  atsAdminGroup.addMember(UserManager.getUser());
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
               }

               IUserGroup oseeAdminGroup =
                  org.eclipse.osee.framework.skynet.core.access.UserGroupService.getOseeAdmin();
               if (!oseeAdminGroup.isCurrentUserMember()) {
                  oseeAdminGroup.addMember(UserManager.getUser());
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
               }
            } else {
               IUserGroup atsAdminGroup =
                  AtsClientService.get().getUserGroupService().getUserGroup(AtsUserGroups.AtsAdmin);
               if (atsAdminGroup.isCurrentUserMember()) {
                  atsAdminGroup.removeMember(UserManager.getUser());
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
               }

               IUserGroup oseeAdminGroup =
                  AtsClientService.get().getUserGroupService().getUserGroup(CoreUserGroups.OseeAdmin);
               if (oseeAdminGroup.isCurrentUserMember()) {
                  oseeAdminGroup.removeMember(UserManager.getUser());
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
               }
            }
            AtsClientService.get().getConfigService().getConfigurationsWithPend();
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
