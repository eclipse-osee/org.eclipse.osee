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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class DeleteArtifactHandler extends CommandHandler {
   private List<Artifact> artifacts;

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      if (!artifacts.isEmpty()) {
         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
            " Are you sure you want to delete this artifact and all of the default hierarchy children?",
            MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
         if (dialog.open() == 0) {
            Artifact[] artifactsArray = artifacts.toArray(new Artifact[artifacts.size()]);
            SkynetTransaction transaction =
               TransactionManager.createTransaction(artifactsArray[0].getBranch(), "Delete Artifact Handler");
            XResultData rd =
               ArtifactPersistenceManager.deleteArtifact(transaction, false, new XResultData(), artifactsArray);
            if (XResultDataUI.reportIfErrors(rd, "Deleate Artifact Handler")) {
               transaction.cancel();
            } else {
               transaction.execute();
            }
         }
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      boolean enabled = false;
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      if (!artifacts.isEmpty()) {
         enabled = OseeApiService.get().getAccessControlService().hasArtifactPermission(artifacts, PermissionEnum.WRITE,
            null).isSuccess();
      }
      return enabled;
   }
}