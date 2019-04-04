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
package org.eclipse.osee.framework.ui.skynet.group;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorerDragAndDrop extends SkynetDragAndDrop {

   boolean isFeedbackAfter = false;
   private final TreeViewer treeViewer;
   private final String viewId;
   private boolean isCtrlPressed = false;
   private BranchId branch;

   public GroupExplorerDragAndDrop(TreeViewer treeViewer, String viewId, BranchId branch) {
      super(treeViewer.getTree(), viewId);
      this.treeViewer = treeViewer;
      this.viewId = viewId;
      this.branch = branch;
      treeViewer.getTree().addKeyListener(new keySelectedListener());
   }
   private class keySelectedListener implements KeyListener {
      @Override
      public void keyPressed(KeyEvent e) {
         isCtrlPressed = e.keyCode == SWT.CONTROL;
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            treeViewer.getTree().selectAll();
         }
         if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
         isCtrlPressed = !(e.keyCode == SWT.CONTROL);
      }
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
      }
   }

   @Override
   public Artifact[] getArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> i = selection.iterator();
      List<Artifact> artifacts = new ArrayList<>();
      while (i.hasNext()) {
         Object object = i.next();
         if (object instanceof GroupExplorerItem) {
            artifacts.add(((GroupExplorerItem) object).getArtifact());
         }
      }
      return artifacts.toArray(new Artifact[artifacts.size()]);
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      if (!ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_NONE;
         return;
      }
      final ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
      if (artData == null) {
         event.detail = DND.DROP_NONE;
         return;
      }
      for (Artifact art : artData.getArtifacts()) {
         if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
            event.detail = DND.DROP_NONE;
            return;
         }
      }
      Tree tree = treeViewer.getTree();
      TreeItem dragOverTreeItem = tree.getItem(treeViewer.getTree().toControl(event.x, event.y));

      event.feedback = DND.FEEDBACK_EXPAND;
      event.detail = DND.DROP_NONE;

      // Set as COPY if drag item over group (copy versus move will be determined on drop
      if (dragOverTreeItem != null && ((GroupExplorerItem) dragOverTreeItem.getData()).isUniversalGroup()) {
         event.detail = DND.DROP_COPY;
         tree.setInsertMark(null, false);
      }
      // Handle re-ordering within same group
      else if (dragOverTreeItem != null && !((GroupExplorerItem) dragOverTreeItem.getData()).isUniversalGroup()) {
         GroupExplorerItem dragOverGroupItem = (GroupExplorerItem) dragOverTreeItem.getData();
         IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
         Object obj = selectedItem.getFirstElement();
         if (obj instanceof GroupExplorerItem) {
            GroupExplorerItem droppingGroupItem = (GroupExplorerItem) obj;

            // the group to move must belong to the same group as the member to insert before/after
            if (dragOverGroupItem.getParentItem().equals(droppingGroupItem.getParentItem())) {
               if (isFeedbackAfter) {
                  event.feedback = DND.FEEDBACK_INSERT_AFTER;
               } else {
                  event.feedback = DND.FEEDBACK_INSERT_BEFORE;
               }
               event.detail = DND.DROP_MOVE;
            }
         } else {
            if (isFeedbackAfter) {
               event.feedback = DND.FEEDBACK_INSERT_AFTER;
            } else {
               event.feedback = DND.FEEDBACK_INSERT_BEFORE;
            }
            event.detail = DND.DROP_COPY;
         }
      } else {
         tree.setInsertMark(null, false);
      }
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
      try {
         TreeItem dragOverTreeITem = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));

         // This should always be true as all items are Group Explorer Items
         if (dragOverTreeITem.getData() instanceof GroupExplorerItem) {
            final GroupExplorerItem dragOverExplorerItem = (GroupExplorerItem) dragOverTreeITem.getData();

            // Drag item dropped ON universal group item
            if (dragOverExplorerItem.isUniversalGroup()) {

               // Drag item came from inside Group Explorer
               if (event.data instanceof ArtifactData) {
                  // If event originated outside, it's a copy event;
                  // OR if event is inside and ctrl is down, this is a copy; add items to group
                  if (!((ArtifactData) event.data).getSource().equals(
                     viewId) || ((ArtifactData) event.data).getSource().equals(viewId) && isCtrlPressed) {
                     copyArtifactsToGroup(event, dragOverExplorerItem);
                  }
                  // Else this is a move
                  else {
                     IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
                     Iterator<?> iterator = selectedItem.iterator();
                     final Set<Artifact> insertArts = new HashSet<>();
                     while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        if (obj instanceof GroupExplorerItem) {
                           insertArts.add(((GroupExplorerItem) obj).getArtifact());
                        }
                     }
                     GroupExplorerItem parentUnivGroupItem =
                        ((GroupExplorerItem) selectedItem.getFirstElement()).getParentItem();
                     final Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                     final Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                     for (Artifact artifact : insertArts) {
                        // Remove item from old group
                        parentArtifact.deleteRelation(CoreRelationTypes.Universal_Grouping__Members, artifact);
                        // Add items to new group
                        targetArtifact.addRelation(CoreRelationTypes.Universal_Grouping__Members, artifact);
                     }
                     TransactionManager.persistInTransaction("Group Explorer - Drag/Drop", parentArtifact,
                        targetArtifact);
                  }
               }
            }
            // Drag item dropped before or after group member
            else if (!dragOverExplorerItem.isUniversalGroup()) {

               if (event.data instanceof ArtifactData) {

                  GroupExplorerItem parentUnivGroupItem = null;
                  // Drag item came from inside Group Explorer
                  if (((ArtifactData) event.data).getSource().equals(viewId)) {
                     IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
                     Iterator<?> iterator = selectedItem.iterator();
                     Set<Artifact> insertArts = new HashSet<>();
                     while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        if (obj instanceof GroupExplorerItem) {
                           insertArts.add(((GroupExplorerItem) obj).getArtifact());
                        }
                     }
                     parentUnivGroupItem = ((GroupExplorerItem) selectedItem.getFirstElement()).getParentItem();
                     insertArts.toArray(new Artifact[insertArts.size()]);

                     Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                     Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                     for (Artifact art : insertArts) {
                        parentArtifact.setRelationOrder(CoreRelationTypes.Universal_Grouping__Members, targetArtifact,
                           isFeedbackAfter, art);
                        targetArtifact = art;
                     }
                     parentArtifact.persist(getClass().getSimpleName());
                  }
                  // Drag item came from outside Group Explorer
                  else {
                     List<Artifact> insertArts = Arrays.asList(((ArtifactData) event.data).getArtifacts());
                     parentUnivGroupItem = dragOverExplorerItem.getParentItem();
                     insertArts.toArray(new Artifact[insertArts.size()]);

                     Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                     Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                     for (Artifact art : insertArts) {
                        parentArtifact.addRelation(USER_DEFINED, CoreRelationTypes.Universal_Grouping__Members,
                           targetArtifact, isFeedbackAfter, art, "");
                     }
                     parentArtifact.persist(getClass().getSimpleName());
                  }
               }
            }
            treeViewer.refresh(dragOverExplorerItem);
         }

         isFeedbackAfter = false;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void copyArtifactsToGroup(DropTargetEvent event, final GroupExplorerItem dragOverExplorerItem) {
      // Items dropped on Group; simply add items to group
      final Artifact[] artsToRelate = ((ArtifactData) event.data).getArtifacts();
      for (Artifact artifact : artsToRelate) {
         if (!artifact.isOnBranch(branch)) {
            AWorkbench.popup("ERROR",
               "Cross-branch grouping not supported.\n\nGroup and Artifacts must belong to same branch.");
            return;
         }
      }
      boolean alreadyRelated = true;
      for (Artifact artifact : artsToRelate) {
         if (!dragOverExplorerItem.contains(artifact)) {
            alreadyRelated = false;
            break;
         }
      }
      if (alreadyRelated) {
         AWorkbench.popup("ERROR", "Artifact(s) already related.");
         return;
      }
      try {
         for (Artifact art : artsToRelate) {
            if (!dragOverExplorerItem.contains(art)) {
               dragOverExplorerItem.getArtifact().addRelation(CoreRelationTypes.Universal_Grouping__Members, art);
            }
         }
         dragOverExplorerItem.getArtifact().persist("Drag and drop: copy artifacts to group");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }
}
