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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.AccessObject;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.PolicyTableXViewerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Displays an <Code>Artifact</Code> access control list, used by the <Code>PolicyDialog</Code>.
 * 
 * @author Jeff C. Phillips
 */
public class PolicyTableViewer {

   private XViewer tableXViewer;
   private Map<String, AccessControlData> accessControlList;
   private Object object;
   private Composite parent;

   public PolicyTableViewer(Composite parent, Object object) {
      this.parent = parent;
      this.createTableViewer();
      this.accessControlList = new HashMap<String, AccessControlData>();
      this.object = object;

      tableXViewer.setContentProvider(new PolicyContentProvider());
      tableXViewer.setLabelProvider(new PolicyLabelProvider(tableXViewer));
      tableXViewer.setInput(accessControlList.values());
   }

   public void allowTableModification(boolean allow) {
      ((PolicyTableCellModifier) tableXViewer.getCellModifier()).setEnabled(allow);
   }

   public void addItem(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = AccessControlManager.getAccessObject(object);
      AccessControlData data = new AccessControlData(subject, accessObject, permission, true);
      accessControlList.put(data.getSubject().getGuid(), data);
      tableXViewer.refresh();
   }

   private void createTableViewer() {
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 150;
      gd.widthHint = 500;

      tableXViewer = new XViewer(parent, SWT.BORDER | SWT.FULL_SELECTION, new PolicyTableXViewerFactory(), true, true);
      tableXViewer.setUseHashlookup(true);
      tableXViewer.setColumnProperties(PolicyTableColumns.getNames());
      tableXViewer.getTree().setLayoutData(gd);

      CellEditor[] validEditors = new CellEditor[PolicyTableColumns.values().length];
      validEditors[1] = new CheckboxCellEditor(parent, SWT.NONE);
      //      validEditors[Columns.Artifact.ordinal()] =
      //            new ComboBoxCellEditor(table, PermissionEnum.getPermissionNames(), SWT.READ_ONLY);
      tableXViewer.setCellEditors(validEditors);
      tableXViewer.setCellModifier(new PolicyTableCellModifier(this));
   }

   public Map<String, AccessControlData> getAccessControlList() {
      return this.accessControlList;
   }

   public void refresh() {
      tableXViewer.refresh();
   }

   public void removeData(AccessControlData data) {
      try {
         AccessControlManager.removeAccessControlDataIf(true, data);
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

   class PolicyContentProvider implements ITreeContentProvider {

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
         Collection<AccessControlData> data = AccessControlManager.getAccessControlList(object);

         for (AccessControlData entry : data) {
            if (isUniqueUnlockedEntry(entry)) accessControlList.put(entry.getSubject().getGuid(), entry);
         }
      }

      private boolean isUniqueUnlockedEntry(AccessControlData entry) {
         String subjectGuid = entry.getSubject().getGuid();
         boolean isUnique = !accessControlList.containsKey(subjectGuid);
         boolean isUnlocked = entry.getPermission() != PermissionEnum.LOCK;
         return isUnique && isUnlocked;
      }

      @Override
      public Object[] getChildren(Object parentElement) {
         return getElements(parentElement);
      }

      @Override
      public Object getParent(Object element) {
         return null;
      }

      @Override
      public boolean hasChildren(Object element) {
         return false;
      }
   }
}
