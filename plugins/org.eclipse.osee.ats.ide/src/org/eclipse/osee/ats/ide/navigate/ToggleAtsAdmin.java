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

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.core.access.UserGroupService;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
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
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;

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
            AtsArtifactToken.AtsTempAdmin).isCurrentUserMember()) {
            AWorkbench.popup("Current User not configured for Temporary Admin");
            return;
         }
         boolean isAdmin = AtsClientService.get().getUserService().isAtsAdmin();
         String message = "Currently " + (isAdmin ? "ADMIN" : "NOT ADMIN") + " - Toggle?";
         CheckBoxDialog diag = new CheckBoxDialog("Toggle Admin", message, "Persist Admin?");
         if (diag.open() == Window.OK) {
            boolean persist = diag.isChecked();
            if (!isAdmin) {
               if (persist) {
                  IUserGroup atsAdminGroup = UserGroupService.getAtsAdmin();
                  atsAdminGroup.addMember(UserManager.getUser());
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
                  atsAdminGroup.setTemporaryOverride(true);

                  IUserGroup oseeAdminGroup =
                     org.eclipse.osee.framework.skynet.core.access.UserGroupService.getOseeAdmin();
                  oseeAdminGroup.addMember(UserManager.getUser());
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
                  oseeAdminGroup.setTemporaryOverride(true);
               }
            } else {
               if (persist) {
                  IUserGroup atsAdminGroup =
                     AtsClientService.get().getUserGroupService().getUserGroup(AtsArtifactToken.AtsAdmin);
                  atsAdminGroup.removeMember(UserManager.getUser());
                  Conditions.assertTrue(atsAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) atsAdminGroup.getArtifact()).persist("Toggle Admin");
                  atsAdminGroup.removeTemporaryOverride();

                  IUserGroup oseeAdminGroup =
                     AtsClientService.get().getUserGroupService().getUserGroup(CoreArtifactTokens.OseeAdmin);
                  oseeAdminGroup.removeMember(UserManager.getUser());
                  Conditions.assertTrue(oseeAdminGroup.getArtifact() instanceof Artifact, "Must be artifact.");
                  ((Artifact) oseeAdminGroup.getArtifact()).persist("Toggle Admin");
                  oseeAdminGroup.removeTemporaryOverride();
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
