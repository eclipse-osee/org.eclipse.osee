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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class DeleteArtifactHandler extends CommandHandler {
   private List<Artifact> artifacts;

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      if (!artifacts.isEmpty()) {
         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
            " Are you sure you want to delete this artifact and all of the default hierarchy children?",
            MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
         if (dialog.open() == 0) {
            Artifact[] artifactsArray = artifacts.toArray(new Artifact[artifacts.size()]);
            SkynetTransaction transaction =
               TransactionManager.createTransaction(artifactsArray[0].getBranch(), "Delete artifact handler");
            ArtifactPersistenceManager.deleteArtifact(transaction, false, artifactsArray);
            transaction.execute();
         }
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      boolean enabled = false;
      artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      if (!artifacts.isEmpty()) {
         enabled = AccessControlManager.hasPermission(artifacts, PermissionEnum.WRITE);
      }
      return enabled;
   }
}