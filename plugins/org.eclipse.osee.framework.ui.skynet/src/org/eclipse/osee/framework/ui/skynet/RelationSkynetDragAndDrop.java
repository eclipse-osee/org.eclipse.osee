/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.importing.parsers.HandleImport;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

public final class RelationSkynetDragAndDrop extends SkynetDragAndDrop {
   boolean isFeedbackAfter = false;
   private final TreeViewer treeViewer;
   private final Artifact artifact;
   private ToolTip errorToolTip;
   private final IDirtiableEditor editor;

   public RelationSkynetDragAndDrop(String viewId, TreeViewer treeViewer, Artifact artifact, IDirtiableEditor editor) {
      super(treeViewer.getTree(), viewId);
      this.treeViewer = treeViewer;
      this.artifact = artifact;
      this.editor = editor;
   }

   private ToolTip getErrorToolTip() {
      if (errorToolTip == null) {
         errorToolTip = new ToolTip(Displays.getActiveShell(), SWT.ICON_ERROR);
      }
      return errorToolTip;
   }

   @Override
   public Artifact[] getArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Object[] objects = selection.toArray();
      Artifact[] artifacts = null;

      if (objects.length > 0 && objects[0] instanceof WrapperForRelationLink) {
         artifacts = new Artifact[objects.length];

         for (int index = 0; index < objects.length; index++) {
            WrapperForRelationLink link = (WrapperForRelationLink) objects[index];
            artifacts[index] = link.getOther();
         }
      }
      return artifacts;
   }

   private boolean ensureRelationCanBeAdded(RelationTypeToken relationType, Artifact artifactA, Artifact artifactB) {
      try {
         RelationManager.ensureRelationCanBeAdded(relationType, artifactA, artifactB);
      } catch (OseeCoreException ex) {
         return false;
      }
      return true;
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      Tree tree = treeViewer.getTree();
      TreeItem selected = tree.getItem(treeViewer.getTree().toControl(event.x, event.y));

      event.feedback = DND.FEEDBACK_EXPAND;
      event.detail = DND.DROP_NONE;

      if (errorToolTip != null) {
         errorToolTip.setVisible(false);
      }

      if (selected != null && selected.getData() instanceof RelationTypeSideSorter) {
         ArtifactTransfer artTransfer = ArtifactTransfer.getInstance();
         FileTransfer fileTransfer = FileTransfer.getInstance();
         RelationTypeSideSorter relTypeSorter = (RelationTypeSideSorter) selected.getData();
         if (artTransfer.isSupportedType(event.currentDataType)) {
            try {
               ArtifactData artData = artTransfer.nativeToJava(event.currentDataType);
               List<Artifact> relatedArts = new ArrayList<Artifact>();
               for (Artifact relatedArt : artData.getArtifacts()) {
                  relatedArts.add(relatedArt);
               }

               String toolTipText = "";
               Artifact relationArtifact = relTypeSorter.getArtifact();

               boolean canRelate = false;
               for (Artifact art : relatedArts) {
                  Artifact sideA = art;
                  Artifact sideB = relationArtifact;
                  if (relTypeSorter.getSide() == RelationSide.SIDE_B) {
                     sideA = relationArtifact;
                     sideB = art;
                  }
                  canRelate = ensureRelationCanBeAdded(relTypeSorter.getRelationType(), sideA, sideB);
                  if (!canRelate) {
                     toolTipText += String.format("Relation: [%s] \n\tcannot be added to [%s]\n\tof [%s]\n",
                        art.getName(), relTypeSorter, relTypeSorter.getRelationType().getName());

                  }
               }

               boolean hadPermission = ServiceUtil.accessControlService().hasRelationTypePermission(artifact,
                  relTypeSorter, relatedArts, PermissionEnum.WRITE, null).isSuccess();

               if (canRelate) {
                  event.detail = DND.DROP_COPY;
                  tree.setInsertMark(null, false);
               } else {
                  toolTipText += toolTipText.length() == 0 ? "" : " \n";
                  toolTipText += "Access: Access Control has prevented this relation";

               }
               if (!hadPermission || !canRelate) {
                  getErrorToolTip().setText("RELATION ERROR");
                  getErrorToolTip().setMessage(toolTipText);
                  getErrorToolTip().setVisible(true);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }

         } else if (fileTransfer.isSupportedType(event.currentDataType)) {
            RelationTypeToken relationType = relTypeSorter.getRelationType();
            if (relationType.equals(CoreRelationTypes.Verification_Verifier) || relationType.equals(
               CoreRelationTypes.Uses_TestUnit)) {
               RelationTypeSide relTypeSide = null;

               relTypeSide = CoreRelationTypes.Verification_Verifier;

               ArtifactData artData = artTransfer.nativeToJava(event.currentDataType);
               List<Artifact> relatedArts = new ArrayList<Artifact>();
               for (Artifact relatedArt : artData.getArtifacts()) {
                  relatedArts.add(relatedArt);
               }
               Artifact artifact = relTypeSorter.getArtifact();

               boolean hasPermission = false;
               try {
                  hasPermission = ServiceUtil.accessControlService().hasRelationTypePermission(artifact, relTypeSide,
                     relatedArts, PermissionEnum.WRITE, null).isSuccess();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               if (hasPermission) {
                  event.detail = DND.DROP_COPY;
               }
            }
         }

      } else if (selected != null && selected.getData() instanceof WrapperForRelationLink) {
         WrapperForRelationLink targetLink = (WrapperForRelationLink) selected.getData();
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Object obj = selection.getFirstElement();
         if (obj instanceof WrapperForRelationLink) {
            WrapperForRelationLink dropTarget = (WrapperForRelationLink) obj;
            boolean hasPermission = false;
            try {
               RelationTypeSide rts = new RelationTypeSide(dropTarget.getRelationType(), dropTarget.getRelationSide());
               List<Artifact> related = Arrays.asList(
                  artifact.equals(dropTarget.getArtifactA()) ? dropTarget.getArtifactB() : dropTarget.getArtifactA());

               hasPermission = ServiceUtil.accessControlService().hasRelationTypePermission(artifact, rts, related,
                  PermissionEnum.WRITE, null).isSuccess();

            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            if (!hasPermission) {
               event.detail = DND.DROP_NONE;
               getErrorToolTip().setText("MOVE ERROR");
               getErrorToolTip().setMessage("Access Control has restricted this action.");
               getErrorToolTip().setVisible(true);
               return;
            }
            // the links must be in the same group
            if (relationLinkIsInSameGroup(targetLink, dropTarget)) {
               if (isFeedbackAfter) {
                  event.feedback = DND.FEEDBACK_INSERT_AFTER;
               } else {
                  event.feedback = DND.FEEDBACK_INSERT_BEFORE;
               }
               event.detail = DND.DROP_MOVE;
            }
         }
      } else {
         tree.setInsertMark(null, false);
      }
   }

   private boolean relationLinkIsInSameGroup(WrapperForRelationLink targetLink, WrapperForRelationLink dropTarget) {
      return targetLink.getRelationType().equals(dropTarget.getRelationType()) && //same type
         (targetLink.getArtifactA().equals(dropTarget.getArtifactA()) || //either the A or B side is equal, meaning they are on the same side
            targetLink.getArtifactB().equals(dropTarget.getArtifactB()));
   }

   @Override
   public void operationChanged(DropTargetEvent event) {
      if (!isCtrlPressed(event)) {
         isFeedbackAfter = false;
      }
   }

   private boolean isCtrlPressed(DropTargetEvent event) {
      boolean ctrPressed = event.detail == 1;

      if (ctrPressed) {
         isFeedbackAfter = true;
      }
      return ctrPressed;
   }

   @Override
   public void performDrop(DropTargetEvent event) {
      TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));
      if (selected == null) {
         return;
      }
      final Object object = selected.getData();
      try {
         if (RelationsComposite.hasWriteRelationTypePermission(artifact, object)) {
            if (object instanceof WrapperForRelationLink) {//used for ordering
               WrapperForRelationLink targetLink = (WrapperForRelationLink) object;
               Artifact[] artifactsToMove = ((ArtifactData) event.data).getArtifacts();
               for (Artifact artifactToMove : artifactsToMove) {
                  RelationTypeSide typeSide =
                     new RelationTypeSide(targetLink.getRelationType(), targetLink.getRelationSide());
                  artifact.setRelationOrder(typeSide, targetLink.getOther(), isFeedbackAfter, artifactToMove);
               }
               treeViewer.refresh();
               editor.onDirtied();
            } else if (object instanceof RelationTypeSideSorter) {
               if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
                  RelationTypeSideSorter group = (RelationTypeSideSorter) object;

                  RelationExplorerWindow window = new RelationExplorerWindow(treeViewer, group);

                  ArtifactDragDropSupport.performDragDrop(event, window,
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
                  window.createArtifactInformationBox();
                  treeViewer.refresh();
                  editor.onDirtied();
               } else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                  Object toJava = FileTransfer.getInstance().nativeToJava(event.currentDataType);
                  final Collection<String> input;
                  if (toJava instanceof String) {
                     input = java.util.Collections.singletonList((String) toJava);
                  } else if (toJava instanceof String[]) {
                     input = Arrays.asList((String[]) toJava);
                  } else {
                     input = null;
                  }
                  if (input != null) {
                     IOperation importOp = new AbstractOperation("Import Test Case", Activator.PLUGIN_ID) {

                        @Override
                        protected void doWork(IProgressMonitor monitor) throws Exception {
                           Collection<URI> resources = new LinkedList<>();
                           for (String path : input) {
                              File file = new File(path);
                              if (file.exists()) {
                                 resources.add(file.toURI());
                              }
                           }
                           HandleImport.handleImport(resources, object, false);
                        }
                     };

                     Operations.executeAsJob(importOp, true, Job.LONG, new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                           super.done(event);
                           if (event.getResult().isOK()) {
                              Displays.ensureInDisplayThread(new Runnable() {
                                 @Override
                                 public void run() {
                                    treeViewer.refresh();
                                    editor.onDirtied();
                                 }
                              });
                           }
                        }
                     });
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      isFeedbackAfter = false;
   }
}