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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class RelationTableViewer {
   private final Table validTable;
   private TableViewer tableViewer;

   private final Table invalidTable;

   private ArtifactModelList artifactList;

   private String[] validColumnNames;
   private static int[] validColumnWidths;

   private String[] invalidColumnNames;
   private static int[] invalidColumnWidths;

   public static final int ADD_NUM = 0;
   public static final int ARTIFACT_NAME_NUM = 1;
   public static final int ARTIFACT_TYPE_NUM = 2;
   public static final int RATIONALE_NUM = 3;
   public static final int INVALID_NAME_NUM = 0;
   public static final int INVALID_REASON_NUM = 1;

   public ArrayList<ArtifactTypeToken> fullDescriptorList;

   public RelationTableViewer(Table validTable, Table invalidTable, BranchId branch) {
      try {
         fullDescriptorList = new ArrayList<>(ArtifactTypeManager.getValidArtifactTypes(branch));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      this.validTable = validTable;
      this.invalidTable = invalidTable;

      this.createControl();

      tableViewer.setContentProvider(new RelationContentProvider());
      tableViewer.setLabelProvider(new ArtifactModelLabelProvider());
      tableViewer.setInput(artifactList);
   }

   public void addValidItem(Artifact artifact) {
      ArtifactModel model = new ArtifactModel(artifact);
      artifactList.addArtifact(model, true);
   }

   public void addInvalidItem(String name, String reason) {
      String[] itemText = new String[] {name, reason};
      TableItem item = new TableItem(invalidTable, SWT.NONE);
      item.setText(itemText);
   }

   private void createControl() {
      artifactList = new ArtifactModelList();
      createColumns();
      createTableViewer();
   }

   /**
    * Create the TableViewer
    */
   private void createTableViewer() {

      tableViewer = new TableViewer(validTable);
      tableViewer.setUseHashlookup(true);
      tableViewer.setColumnProperties(validColumnNames);

      CellEditor[] validEditors = new CellEditor[validColumnNames.length];
      validEditors[ADD_NUM] = new CheckboxCellEditor(validTable, SWT.CENTER);
      validEditors[ARTIFACT_NAME_NUM] = new TextCellEditor(validTable);

      String[] items = new String[fullDescriptorList.size()];
      for (int i = 0; i < items.length; i++) {
         items[i] = fullDescriptorList.get(i).getName();
      }

      validEditors[ARTIFACT_TYPE_NUM] = new ComboBoxCellEditor(validTable, items);
      validEditors[RATIONALE_NUM] = new TextCellEditor(validTable);

      // Assign the cell editors to the viewer
      tableViewer.setCellEditors(validEditors);
      // Assign the cell modifier to the viewer
      tableViewer.setCellModifier(new RelationTableCellModifier(this));
   }

   /**
    * Create the Columns
    */
   private void createColumns() {
      validColumnNames = new String[] {"Add", "Artifact Name", "Artifact Type", "Rationale"};
      validColumnWidths = new int[] {40, 200, 100, 500};

      invalidColumnNames = new String[] {"Name", "Reason"};
      invalidColumnWidths = new int[] {200, 640};

      TableColumn column = new TableColumn(validTable, SWT.LEFT, ADD_NUM);
      column.setText(validColumnNames[ADD_NUM]);
      column.setWidth(validColumnWidths[ADD_NUM]);

      column = new TableColumn(validTable, SWT.LEFT, ARTIFACT_NAME_NUM);
      column.setText(validColumnNames[ARTIFACT_NAME_NUM]);
      column.setWidth(validColumnWidths[ARTIFACT_NAME_NUM]);
      column.addSelectionListener(new SelectionAdapter() {
         @SuppressWarnings("deprecation")
         @Override
         public void widgetSelected(SelectionEvent e) {
            tableViewer.setSorter(new RelationTableSorter(RelationTableSorter.ARTIFACT_NAME));
         }
      });

      column = new TableColumn(validTable, SWT.LEFT, ARTIFACT_TYPE_NUM);
      column.setText(validColumnNames[ARTIFACT_TYPE_NUM]);
      column.setWidth(validColumnWidths[ARTIFACT_TYPE_NUM]);
      column.addSelectionListener(new SelectionAdapter() {
         @SuppressWarnings("deprecation")
         @Override
         public void widgetSelected(SelectionEvent e) {
            tableViewer.setSorter(new RelationTableSorter(RelationTableSorter.ARTIFACT_TYPE));
         }
      });

      column = new TableColumn(validTable, SWT.LEFT, RATIONALE_NUM);
      column.setText(validColumnNames[RATIONALE_NUM]);
      column.setWidth(validColumnWidths[RATIONALE_NUM]);

      column = new TableColumn(invalidTable, SWT.LEFT, INVALID_NAME_NUM);
      column.setText(invalidColumnNames[INVALID_NAME_NUM]);
      column.setWidth(invalidColumnWidths[INVALID_NAME_NUM]);

      column = new TableColumn(invalidTable, SWT.LEFT, INVALID_REASON_NUM);
      column.setText(invalidColumnNames[INVALID_REASON_NUM]);
      column.setWidth(invalidColumnWidths[INVALID_REASON_NUM]);
   }

   public List<String> getColumnNames() {
      return Arrays.asList(validColumnNames);
   }

   public ArtifactModelList getArtifactList() {
      return this.artifactList;
   }

   public void refresh() {
      tableViewer.refresh();
   }

   /**
    * @return Returns the invalidTable.
    */
   public Table getInvalidTable() {
      return invalidTable;
   }

   /**
    * @return Returns the validTable.
    */
   public Table getValidTable() {
      return validTable;
   }

   public void resizeTable(int windowWidth) {
      int otherColumns = 15;
      for (int i = 0; i < validColumnWidths.length - 1; i++) {
         otherColumns += validColumnWidths[i];
      }

      validColumnWidths[RATIONALE_NUM] = windowWidth - otherColumns;
      validTable.getColumns()[RATIONALE_NUM].setWidth(validColumnWidths[RATIONALE_NUM]);

      otherColumns = 15;
      for (int i = 0; i < invalidColumnWidths.length - 1; i++) {
         otherColumns += invalidColumnWidths[i];
      }

      invalidColumnWidths[INVALID_REASON_NUM] = windowWidth - otherColumns;
      invalidTable.getColumns()[INVALID_REASON_NUM].setWidth(invalidColumnWidths[INVALID_REASON_NUM]);
   }

   /**
    * InnerClass that acts as a proxy for the ArtifactModelList providing content for the Table. It implements the
    * IArtifactListViewer interface since it must register changeListeners with the ArtifactModelList
    */
   class RelationContentProvider implements IStructuredContentProvider, IArtifactListViewer {

      @Override
      public void inputChanged(Viewer v, Object oldInput, Object newInput) {
         if (newInput != null) {
            artifactList.addChangeListener(this);
         }
         if (oldInput != null) {
            artifactList.removeChangeListener(this);
         }
      }

      @Override
      public void dispose() {
         artifactList.removeChangeListener(this);
      }

      // Return the tasks as an array of Objects
      @Override
      public Object[] getElements(Object parent) {
         return artifactList.getArtifactModel().toArray();
      }

      @Override
      public void addArtifact(ArtifactModel artifact) {
         tableViewer.add(artifact);
      }

      @Override
      public void removeArtifact(ArtifactModel artifact) {
         tableViewer.remove(artifact);
      }

      @Override
      public void updateArtifact(ArtifactModel artifact) {
         tableViewer.update(artifact, null);
      }
   }
}
