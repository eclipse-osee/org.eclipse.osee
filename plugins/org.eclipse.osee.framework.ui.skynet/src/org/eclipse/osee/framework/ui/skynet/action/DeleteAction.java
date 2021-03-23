/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class DeleteAction extends Action {

   private final Collection<Artifact> artifacts;

   public DeleteAction(Collection<Artifact> artifacts) {
      super("&Delete Artifact(s)", IAction.AS_PUSH_BUTTON);
      this.artifacts = artifacts;
   }

   @Override
   public void run() {
      deleteArtifactsMethod(artifacts);
   }

   public static MenuItem createDeleteMenuItem(Menu parentMenu) {
      MenuItem purgeMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      purgeMenuItem.setImage(ImageManager.getImage(FrameworkImage.X_RED));
      purgeMenuItem.setText("&Delete Artifact(s)");
      purgeMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            List<Artifact> arts = new ArrayList<>();
            IStructuredSelection selection = (IStructuredSelection) e;
            if (selection != null) {
               while (selection.iterator().hasNext()) {
                  Object obj = selection.iterator().next();
                  if (obj instanceof Artifact) {
                     arts.add((Artifact) obj);
                  }
               }
            }
            deleteArtifactsMethod(arts);
         };
      });
      return purgeMenuItem;
   }

   public static void deleteArtifactsMethod(Collection<Artifact> artifactsToBeDeleted) {

      MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
         " Are you sure you want to delete this artifact and all of the default hierarchy children?",
         MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);

      if (dialog.getReturnCode() == Window.OK) {
         Job job = new Job("Purge artifact") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;

               monitor.beginTask("Delete artifact", artifactsToBeDeleted.size());

               Artifact[] artifactsArray = artifactsToBeDeleted.toArray(new Artifact[artifactsToBeDeleted.size()]);
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(artifactsArray[0].getBranch(), "Delete Artifact Action");
               ArtifactPersistenceManager.deleteArtifact(transaction, false, artifactsArray);
               transaction.execute();

               return toReturn;
            }
         };

         Jobs.startJob(job);
      }
   }

};
