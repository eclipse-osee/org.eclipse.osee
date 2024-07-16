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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public class OpenAssociatedArtifactHandler extends CommandHandler {

   @SuppressWarnings("unlikely-arg-type")
   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      BranchId selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      Artifact associatedArtifact = BranchManager.getAssociatedArtifact(selectedBranch);
      if (associatedArtifact.isInvalid()) {
         AWorkbench.popup("Open Associated Artifact", "No artifact associated with branch " + selectedBranch);
         return null;
      }
      if (SystemUser.OseeSystem.equals(associatedArtifact)) {
         AWorkbench.popup("ERROR", "No Associated Artifact");
         return null;
      }
      if (ServiceUtil.accessControlService().hasArtifactPermission(associatedArtifact, PermissionEnum.READ,
         null).isSuccess()) {
         IOseeCmService cmService = ServiceUtil.getOseeCmService();
         if (cmService.isPcrArtifact(associatedArtifact)) {
            cmService.openArtifact(associatedArtifact, OseeCmEditor.CmPcrEditor);
         } else {
            RendererManager.open(associatedArtifact, PresentationType.DEFAULT_OPEN);
         }
      } else {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            "The user " + UserManager.getUser() + " does not have read access to " + associatedArtifact);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1;
   }
}