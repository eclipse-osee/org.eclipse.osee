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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
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
public class MassXViewer extends XViewer {

   private static String NAMESPACE = "org.eclipse.osee.framework.ui.skynet.massEditor.ArtifactXViewer";
   private String title;
   private Collection<? extends Artifact> artifacts;
   private final IDirtiableEditor editor;
   public static enum Extra_Columns {
      HRID, GUID, Artifact_Type
   };

   /**
    * @param parent
    * @param style
    */
   public MassXViewer(Composite parent, int style, IDirtiableEditor editor) {
      this(parent, style, NAMESPACE, new MassXViewerFactory(), editor);
   }

   public MassXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, IDirtiableEditor editor) {
      super(parent, style, nameSpace, xViewerFactory);
      this.editor = editor;
      this.addDoubleClickListener(new IDoubleClickListener() {
         public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
            handleDoubleClick();
         };
      });
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      String colName = treeColumn.getText();
      Set<Artifact> useArts = new HashSet<Artifact>();
      for (TreeItem item : treeItems) {
         useArts.add(((MassArtifactItem) item.getData()).getArtifact());
      }
      try {
         if (ArtifactPromptChange.promptChangeAttribute(colName, colName, useArts, false)) {
            refresh();
            editor.onDirtied();
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (treeColumn.getText().equals(Extra_Columns.Artifact_Type.name()) || treeColumn.getText().equals(
            Extra_Columns.HRID.name()) || treeColumn.getText().equals(Extra_Columns.GUID.name())) return false;
      return true;
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
      try {
         super.handleAltLeftClick(treeColumn, treeItem);
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         String colName = treeColumn.getText();
         if (colName.equals(Extra_Columns.Artifact_Type.name()) || colName.equals(Extra_Columns.HRID.name()) || colName.equals(Extra_Columns.GUID.name())) {
            AWorkbench.popup("ERROR", "Can't change the field " + colName);
         }
         Artifact useArt = ((MassArtifactItem) treeItem.getData()).getArtifact();
         if (ArtifactPromptChange.promptChangeAttribute(colName, colName, Arrays.asList(new Artifact[] {useArt}),
               persist)) {
            refresh();
            editor.onDirtied();
            return true;
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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
               if (artifact.getBranch() == BranchPersistenceManager.getInstance().getDefaultBranch()) event.data =
                     new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", MassArtifactEditor.EDITOR_ID);
            }
         }

         public void dragStart(DragSourceEvent event) {
            event.doit = false;
            Collection<Artifact> arts = getSelectedArtifacts();
            if (arts.size() > 0) {
               Artifact artifact = arts.iterator().next();
               if (artifact.getBranch() == BranchPersistenceManager.getInstance().getDefaultBranch()) event.doit = true;
            }
         }
      });

      // Do not allow drop if default branch is not same as artifacts that reside in this table
      DropTarget target = new DropTarget(getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(),
            ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         public void dragOver(DropTargetEvent event) {
            // if ((event.data instanceof ArtifactData) && ((ArtifactData)
            // event.data).getArtifacts().length > 0)
            event.detail = DND.DROP_COPY;
         }

         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      if (e.data instanceof ArtifactData) {
         Artifact[] artsToAdd = ((ArtifactData) e.data).getArtifacts();
         Set<Artifact> arts = new HashSet<Artifact>();
         arts.addAll(artifacts);
         for (Artifact art : artsToAdd)
            arts.add(art);
         set(arts);
      }
      refresh();
   }

   public void handleDoubleClick() {
      if (getSelectedArtifactItems().size() == 0) return;
      Artifact art = getSelectedArtifactItems().iterator().next().getArtifact();
      ArtifactEditor.editArtifact(art);
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getItems();
      if (items.length > 0) for (TreeItem item : items)
         arts.add(((MassArtifactItem) item.getData()).getArtifact());
      return arts;
   }

   /**
    * Release resources
    */
   public void dispose() {
      // Tell the label provider to release its ressources
      getLabelProvider().dispose();
   }

   public ArrayList<Artifact> getSelectedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) ((MassArtifactItem) item.getData()).getArtifact());
      return arts;
   }

   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }

   public void add(Collection<Artifact> artifacts) {
      Set<MassArtifactItem> items = new HashSet<MassArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new MassArtifactItem(this, art, null));
      resetColumns(artifacts);
      ((MassContentProvider) getContentProvider()).add(items);
   }

   public void set(Collection<? extends Artifact> artifacts) {
      Set<MassArtifactItem> items = new HashSet<MassArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new MassArtifactItem(this, art, null));
      resetColumns(artifacts);
      this.artifacts = artifacts;
      ((MassContentProvider) getContentProvider()).set(items);
   }

   public void resetColumns(Collection<? extends Artifact> artifacts) {
      CustomizeData custData = new CustomizeData();

      Set<DynamicAttributeManager> dams = new HashSet<DynamicAttributeManager>();

      try {
         for (Artifact art : artifacts) {
            dams.addAll(art.getAttributes());
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      Set<String> attrNames = new HashSet<String>();
      // Add Name first
      XViewerColumn newCol = new XViewerColumn(this, "Name", 150, 150, SWT.LEFT);

      int x = 0;
      newCol.setOrderNum(x++);
      newCol.setTreeViewer(this);
      cols.add(newCol);
      attrNames.add("Name");

      // Add other attributes
      for (DynamicAttributeManager dam : dams) {
         if (!attrNames.contains(dam.getDescriptor().getName())) {
            // System.out.println(dam.getDescriptor().getName());
            SortDataType sortType = SortDataType.String;
            if (dam.getDescriptor().getBaseAttributeClass().equals(DateAttribute.class))
               sortType = SortDataType.Date;
            else if (dam.getDescriptor().getBaseAttributeClass().equals(FloatingPointAttribute.class))
               sortType = SortDataType.Float;
            else if (dam.getDescriptor().getBaseAttributeClass().equals(IntegerAttribute.class))
               sortType = SortDataType.Integer;
            else if (dam.getDescriptor().getBaseAttributeClass().equals(BooleanAttribute.class)) sortType =
                  SortDataType.Boolean;
            newCol = new XViewerColumn(this, dam.getDescriptor().getName(), 75, 75, SWT.CENTER);
            newCol.setSortDataType(sortType);
            newCol.setOrderNum(x++);
            newCol.setTreeViewer(this);
            cols.add(newCol);
            attrNames.add(dam.getDescriptor().getName());
         }
      }

      // Add HRID and GUID
      for (Extra_Columns col : Extra_Columns.values()) {
         newCol = new XViewerColumn(this, col.name(), 75, 75, SWT.LEFT);
         newCol.setOrderNum(x++);
         newCol.setTreeViewer(this);
         cols.add(newCol);
      }

      custData.getColumnData().setColumns(cols);
      custData.getSortingData().setSortingNames(Arrays.asList(new String[] {"Name"}));
      getCustomize().setCustomization(custData);
      ((MassXViewerFactory) getXViewerFactory()).setDefaultCustData(custData);
   }

   public ArrayList<MassArtifactItem> getSelectedArtifactItems() {
      ArrayList<MassArtifactItem> arts = new ArrayList<MassArtifactItem>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((MassArtifactItem) item.getData());
      return arts;
   }

   public Object[] getSelectedArtifactItemsArray() {
      return getSelectedArtifactItems().toArray(new MassArtifactItem[getSelectedArtifactItems().size()]);
   }

   /**
    * @return the artifacts
    */
   public Collection<? extends Artifact> getArtifacts() {
      return artifacts;
   }

}
