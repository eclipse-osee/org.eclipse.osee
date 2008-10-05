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
package org.eclipse.osee.framework.ui.skynet.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.AccessObject;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Displays an <Code>Artifact</Code> access control list, used by the <Code>PolicyDialog</Code>.
 * 
 * @author Jeff C. Phillips
 */
public class PolicyTableViewer {

   private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   private Table table;
   private TableViewer tableViewer;
   private Map<String, AccessControlData> accessControlList;
   private Object object;
   public static enum Columns {
      Delete, Person, Total, Branch, Artifact_Type, Artifact
   };
   private final static int[] columnWidths = new int[] {25, 125, 75, 75, 75, 75};
   private String[] columnNames;

   /**
    * @param table -
    */
   public PolicyTableViewer(Table table, Object object) {
      this.table = table;
      this.createControl();
      this.accessControlList = new HashMap<String, AccessControlData>();
      this.object = object;

      tableViewer.setContentProvider(new PolicyContentProvider());
      tableViewer.setLabelProvider(new PolicyLabelProvider());
      tableViewer.setInput(accessControlList.values());
   }

   /**
    * Disables the cell modifiers, not the entire table so user can still scroll
    * 
    * @param enabled
    */
   public void setEnabled(boolean enabled) {
      ((PolicyTableCellModifier) tableViewer.getCellModifier()).setEnabled(enabled);
   }

   public void addItem(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = accessManager.getAccessObject(object);
      AccessControlData data = new AccessControlData(subject, accessObject, permission, true);
      accessControlList.put(data.getSubject().getGuid(), data);
      tableViewer.refresh();
   }

   private void createControl() {
      createColumns();
      createTableViewer();
   }

   /**
    * Create the TableViewer
    */
   private void createTableViewer() {

      tableViewer = new TableViewer(table);
      tableViewer.setUseHashlookup(true);
      columnNames = new String[Columns.values().length];
      for (Columns col : Columns.values())
         columnNames[col.ordinal()] = col.name();
      tableViewer.setColumnProperties(columnNames);

      CellEditor[] validEditors = new CellEditor[Columns.values().length];
      validEditors[Columns.Delete.ordinal()] = new CheckboxCellEditor(table, SWT.NONE);
      validEditors[Columns.Artifact.ordinal()] =
            new ComboBoxCellEditor(table, PermissionEnum.getPermissionNames(), SWT.READ_ONLY);

      // Assign the cell editors to the viewer
      tableViewer.setCellEditors(validEditors);
      // Assign the cell modifier to the viewer
      tableViewer.setCellModifier(new PolicyTableCellModifier(this));
   }

   /**
    * Create the Columns
    */
   private void createColumns() {
      table.setLinesVisible(true);
      table.setHeaderVisible(true);

      for (Columns col : Columns.values()) {
         TableColumn column = new TableColumn(table, SWT.LEFT, col.ordinal());
         column.setText(col.name());
         column.setWidth(columnWidths[col.ordinal()]);
      }

   }

   public Map<String, AccessControlData> getAccessControlList() {
      return this.accessControlList;
   }

   public void refresh() {
      tableViewer.refresh();
   }

   /**
    * @return Returns the table.
    */
   public Table getTable() {
      return table;
   }

   public void removeData(AccessControlData data) {
      try {
         accessManager.removeAccessControlData(data);
      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      accessControlList.remove(data.getSubject().getGuid());
   }

   public void modifyPermissionLevel(AccessControlData data, PermissionEnum permission) {
      data.setPermission(permission);
   }

   public int getCount() {
      return accessControlList.size();
   }
   /**
    * InnerClass that acts as a proxy for the FilterModelList providing content for the Table. It implements the
    * IFilterListViewer interface since it must register changeListeners with the FilterModelList
    */
   class PolicyContentProvider implements IStructuredContentProvider {

      public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      }

      public void dispose() {
      }

      public Object[] getElements(Object object) {
         populateSubjectsFromDb();
         Object[] accessControlListArray = accessControlList.values().toArray();
         Arrays.sort(accessControlListArray);
         return accessControlListArray;
      }

      private void populateSubjectsFromDb() {
         Collection<AccessControlData> datas = accessManager.getAccessControlList(object);

         for (AccessControlData data : datas) {
            if (!accessControlList.containsKey(data.getSubject().getGuid()) && data.getPermission() != PermissionEnum.LOCK) accessControlList.put(
                  data.getSubject().getGuid(), data);
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see IFilterListViewer#addFilter(FilterModel)
       */
      public void addFilter(AccessControlData data) {
         tableViewer.add(data);
      }

      /*
       * (non-Javadoc)
       * 
       * @see IFilterListViewer#removeFilter(FilterModel)
       */
      public void removeFilter(AccessControlData data) {
         tableViewer.remove(data);
      }
   }

   /**
    * @return the columnNames
    */
   public String[] getColumnNames() {
      return columnNames;
   }
}
