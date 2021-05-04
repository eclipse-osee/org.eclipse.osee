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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class PurgeAction extends Action {

   private final Collection<Artifact> artifacts;
   private final ISelectedArtifacts selected;

   public PurgeAction(Collection<Artifact> artifacts) {
      super("&Purge Artifact(s)", IAction.AS_PUSH_BUTTON);
      this.artifacts = artifacts;
      this.selected = null;
   }

   public PurgeAction(ISelectedArtifacts selected) {
      super("&Purge Artifact(s)", IAction.AS_PUSH_BUTTON);
      this.selected = selected;
      this.artifacts = null;
   }

   @Override
   public void run() {
      if (artifacts != null) {
         purgeArtifactsMethod(artifacts);
      } else {
         purgeArtifactsMethod(selected.getSelectedArtifacts());
      }
   }

   public static MenuItem createPurgeMenuItem(Menu parentMenu, TreeViewer treeViewer) {
      MenuItem purgeMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      purgeMenuItem.setImage(ImageManager.getImage(FrameworkImage.TRASH));
      purgeMenuItem.setText("&Purge Artifact(s)");
      purgeMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            List<Artifact> arts = new ArrayList<>();
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            if (selection != null) {
               Iterator<?> iterator = selection.iterator();
               while (iterator.hasNext()) {
                  Object obj = iterator.next();
                  if (obj instanceof Artifact) {
                     arts.add((Artifact) obj);
                  }
               }
            }
            purgeArtifactsMethod(arts);
         };
      });
      return purgeMenuItem;
   }

   public static void purgeArtifactsMethod(Collection<Artifact> artifactsToBePurged) {

      final MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Confirm Artifact Purge ",
         " Are you sure you want to purge this artifact and all history associated from the database? (cannot be undone)",
         "Purge selected artifact's children?", false, null, null);

      if (dialog.open() == Window.OK) {
         final boolean recusivePurge =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Recursive Purge", "Recurse and purge from child branches?");
         Job job = new Job("Purge artifact") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;

               monitor.beginTask("Purge artifact", artifactsToBePurged.size());
               try {
                  boolean recurseChildren = dialog.getToggleState();
                  Collection<Artifact> toPurge = new LinkedHashSet<>();
                  for (Artifact artifactToPurge : artifactsToBePurged) {
                     if (!artifactToPurge.isDeleted()) {
                        toPurge.add(artifactToPurge);
                        if (recurseChildren) {
                           toPurge.addAll(artifactToPurge.getDescendants());
                        }
                     }
                  }
                  monitor.setTaskName("Purging " + toPurge.size() + " artifacts");
                  Operations.executeWorkAndCheckStatus(new PurgeArtifacts(toPurge, recusivePurge));
                  monitor.worked(toPurge.size());
                  toReturn = Status.OK_STATUS;
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
                  toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
               } finally {
                  monitor.done();
               }

               return toReturn;
            }
         };

         Jobs.startJob(job);
      }
   }

};
