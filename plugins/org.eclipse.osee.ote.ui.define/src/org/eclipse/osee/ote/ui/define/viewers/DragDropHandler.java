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
package org.eclipse.osee.ote.ui.define.viewers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.ote.define.jobs.OutfileToArtifactJob;
import org.eclipse.osee.ote.define.utilities.OutfileParserExtensionManager;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.osee.ote.ui.define.dialogs.BranchComboDialog;
import org.eclipse.osee.ote.ui.define.jobs.AddArtifactsToViewerJob;
import org.eclipse.osee.ote.ui.define.jobs.ReportErrorsJob;
import org.eclipse.osee.ote.ui.define.views.TestRunView;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Roberto E. Escobar
 */
public class DragDropHandler {

   private final XViewerDataManager viewerDataManager;

   public DragDropHandler(XViewerDataManager viewerDataManager) {
      this.viewerDataManager = viewerDataManager;
      setupDropSupport();
      setupDragSupport();
   }

   private void setupDropSupport() {
      DropTarget dropTarget = new DropTarget(viewerDataManager.getControl(), DND.DROP_COPY);
      dropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer(), ArtifactTransfer.getInstance(),});
      dropTarget.addDropListener(new DropTargetAdapter() {

         @Override
         public void drop(DropTargetEvent event) {
            try {
               performDrop(event);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }
      });
   }

   private void setupDragSupport() {
      DragSource dragSource = new DragSource(viewerDataManager.getControl(), DND.DROP_COPY);
      dragSource.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      dragSource.addDragListener(new DragSourceListener() {

         @Override
         public void dragFinished(DragSourceEvent event) {
            // do nothing
         }

         @Override
         public void dragSetData(DragSourceEvent event) {
            List<Artifact> artifacts = viewerDataManager.getSelectedArtifacts();
            if (artifacts.size() > 0) {
               event.data =
                  new ArtifactData(artifacts.toArray(new Artifact[artifacts.size()]), "", TestRunView.VIEW_ID);
            }
         }

         @Override
         public void dragStart(DragSourceEvent event) {
            event.doit = false;
            List<Artifact> artifacts = viewerDataManager.getSelectedArtifacts();
            if (artifacts.size() > 0) {
               event.doit = true;
            }
         }
      });

   }

   private void performDrop(DropTargetEvent e) {
      Object object = e.data;
      if (object instanceof ArtifactData) {
         handleArtifactDrops((ArtifactData) object);
      } else if (object instanceof TreeSelection) {
         StructuredSelection selection = (StructuredSelection) object;
         if (selection.size() > 0) {
            URI[] iFiles = toResourceArray(selection.toArray());
            if (iFiles.length > 0) {
               try {
                  handleResourceDrops(iFiles);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            } else {
               OseeLog.log(Activator.class, Level.WARNING, "No valid files dropped");
            }
         }
      }
   }

   private URI[] toResourceArray(Object[] objects) {
      List<URI> toReturn = new ArrayList<>();
      for (Object object : objects) {
         if (object instanceof IAdaptable) {
            Object resource = ((IAdaptable) object).getAdapter(IResource.class);
            if (resource != null) {
               IResource iResource = (IResource) resource;
               if (isResourceAllowed(iResource)) {
                  toReturn.add(iResource.getLocationURI());
               }
            }
         }
      }
      return toReturn.toArray(new URI[toReturn.size()]);
   }

   private boolean isResourceAllowed(IResource resource) {
      if (resource.getType() == IResource.FILE && resource.isAccessible()) {
         String toCheck = resource.getFileExtension();
         try {
            for (String extension : OutfileParserExtensionManager.getInstance().getSupportedExtensions()) {
               if (toCheck.equalsIgnoreCase(extension)) {
                  return true;
               }
            }
         } catch (OseeCoreException ex) {
            // Do Nothing
         }
      }
      return false;

   }

   private void handleArtifactDrops(ArtifactData artifactData) {
      Artifact[] artifactsDropped = artifactData.getArtifacts();
      Set<Artifact> artifactsToAdd = new HashSet<>();
      for (Artifact artifact : artifactsDropped) {
         if (artifact.isOfType(CoreArtifactTypes.TestRun)) {
            artifactsToAdd.add(artifact);
         }
      }
      addArtifactsToTable(new ArrayList<Artifact>(artifactsToAdd));
   }

   private void handleResourceDrops(URI[] iFiles) {
      BranchId branch = BranchComboDialog.getBranchFromUser();
      if (branch.isValid()) {
         OutfileToArtifactJob artifactJob = new OutfileToArtifactJob(branch, iFiles);
         artifactJob.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               IStatus status = event.getResult();
               if (status.equals(Status.OK_STATUS)) {
                  OutfileToArtifactJob job = (OutfileToArtifactJob) event.getJob();
                  Artifact[] results = job.getResults();
                  URI[] unparseable = job.getUnparseableFiles();
                  reportUnparseableItems(unparseable);
                  addArtifactsToTable(Arrays.asList(results));
               }
            }
         });
         artifactJob.schedule();
      }
   }

   private void addArtifactsToTable(final List<Artifact> artifacts) {
      Job job = new AddArtifactsToViewerJob(viewerDataManager, artifacts);
      job.schedule();
   }

   private void reportUnparseableItems(final URI[] unparseable) {
      if (unparseable.length > 0) {
         String title = "Artifact Drop Error";
         String message = "The following file(s) had errors during the parsing operation: ";
         ReportErrorsJob.openError(title, message, (Object[]) unparseable);
      }
   }
}
