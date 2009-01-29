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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class MassXViewer extends XViewer implements IFrameworkTransactionEventListener, IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener {

   private String title;
   private final Set<Artifact> artifacts = new HashSet<Artifact>(50);
   private final IDirtiableEditor editor;
   private final List<String> EXTRA_COLUMNS = Arrays.asList(new String[] {"GUID", "HRID", "Artifact Type"});

   /**
    * @param parent
    * @param style
    */
   public MassXViewer(Composite parent, int style, MassArtifactEditor editor) {
      super(parent, style, ((MassArtifactEditorInput) editor.getEditorInput()).getXViewerFactory());
      this.editor = editor;
      OseeEventManager.addListener(this);
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      String colName = treeColumn.getText();
      Set<Artifact> useArts = new HashSet<Artifact>();
      for (TreeItem item : treeItems) {
         useArts.add((Artifact) item.getData());
      }
      if (ArtifactPromptChange.promptChangeAttribute(colName, colName, useArts, false)) {
         refresh();
         editor.onDirtied();
      }
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (EXTRA_COLUMNS.contains(treeColumn.getText())) return false;
      return super.isColumnMultiEditable(treeColumn, treeItems);
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem, false);
   }

   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem, boolean persist) {
      super.handleAltLeftClick(treeColumn, treeItem);
      // System.out.println("Column " + treeColumn.getText() + " item " +
      // treeItem);
      String colName = treeColumn.getText();
      if (EXTRA_COLUMNS.contains(colName)) {
         AWorkbench.popup("ERROR", "Can't change the field " + colName);
      }
      Artifact useArt = ((Artifact) treeItem.getData());
      if (ArtifactPromptChange.promptChangeAttribute(colName, colName, Arrays.asList(useArt), persist)) {
         refresh();
         editor.onDirtied();
         return true;
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#createSupportWidgets(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      setupDragAndDropSupport();
   }

   private void setupDragAndDropSupport() {

      // Do not allow drag if artifacts in this table are not on same branch as default branch
      DragSource source = new DragSource(getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         public void dragFinished(DragSourceEvent event) {
            refresh();
         }

         public void dragSetData(DragSourceEvent event) {
            Collection<Artifact> arts = getSelectedArtifacts();
            if (arts.size() > 0) {
               Artifact artifact = arts.iterator().next();
               if (artifact.getBranch() == BranchManager.getDefaultBranch()) event.data =
                     new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", MassArtifactEditor.EDITOR_ID);
            }
         }

         public void dragStart(DragSourceEvent event) {
            event.doit = false;
            Collection<Artifact> arts = getSelectedArtifacts();
            if (arts.size() > 0) {
               Artifact artifact = arts.iterator().next();
               if (artifact.getBranch() == BranchManager.getDefaultBranch()) event.doit = true;
            }
         }
      });

      // Do not allow drop if default branch is not same as artifacts that reside in this table
      DropTarget target = new DropTarget(getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(),
            ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         @Override
         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            // if ((event.data instanceof ArtifactData) && ((ArtifactData)
            // event.data).getArtifacts().length > 0)
            event.detail = DND.DROP_COPY;
         }

         @Override
         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      try {
         if (e.data instanceof ArtifactData) {
            Artifact[] artsToAdd = ((ArtifactData) e.data).getArtifacts();
            Set<Artifact> arts = new HashSet<Artifact>();
            arts.addAll(artifacts);
            for (Artifact art : artsToAdd)
               arts.add(art);
            set(arts);
         }
         refresh();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedArtifacts().size() == 0) return;
      Artifact art = getSelectedArtifacts().iterator().next();
      RendererManager.openInJob(art, PresentationType.GENERALIZED_EDIT);
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getItems();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) item.getData());
      return arts;
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   public ArrayList<Artifact> getSelectedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) item.getData());
      return arts;
   }

   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }

   public void add(Collection<? extends Artifact> artifacts) {
      if (xViewerFactory instanceof MassXViewerFactory) {
         ((MassXViewerFactory) xViewerFactory).registerAllAttributeColumnsForArtifacts(artifacts, true);
      }
      for (Artifact art : artifacts) {
         this.artifacts.add(art);
      }
      ((MassContentProvider) getContentProvider()).add(artifacts);
   }

   public void set(Collection<? extends Artifact> artifacts) {
      if (xViewerFactory instanceof MassXViewerFactory) {
         ((MassXViewerFactory) xViewerFactory).registerAllAttributeColumnsForArtifacts(artifacts, true);
      }
      this.artifacts.clear();
      for (Artifact art : artifacts) {
         this.artifacts.add(art);
      }
      ((MassContentProvider) getContentProvider()).set(artifacts);
   }

   /**
    * @return the artifacts
    */
   public Collection<? extends Artifact> getArtifacts() {
      return artifacts;
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (getTree() == null || getTree().isDisposed()) {
               dispose();
               return;
            }
            if (transData.cacheDeletedArtifacts.size() > 0) {
               ((MassContentProvider) getContentProvider()).removeAll(transData.cacheDeletedArtifacts);
            }
            if (transData.cacheChangedArtifacts.size() > 0) {
               ((MassContentProvider) getContentProvider()).updateAll(transData.cacheChangedArtifacts);
            }
            refresh(transData.cacheRelationAddedArtifacts);
            refresh(transData.cacheRelationChangedArtifacts);
            refresh(transData.cacheRelationDeletedArtifacts);
         }
      });
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               if (getTree() != null && !getTree().isDisposed()) {
                  remove(loadedArtifacts.getLoadedArtifacts().toArray());
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               remove(loadedArtifacts.getLoadedArtifacts().toArray());
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

}
