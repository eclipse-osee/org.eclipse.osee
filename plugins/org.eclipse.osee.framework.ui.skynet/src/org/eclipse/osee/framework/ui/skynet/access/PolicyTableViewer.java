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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
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

   private final Map<String, AccessControlData> accessControlList = new HashMap<String, AccessControlData>();
   private final Object object;
   private final Composite parent;

   private XViewer tableXViewer;

   public PolicyTableViewer(Composite parent, Object object) {
      this.parent = parent;
      this.object = object;

      createControl();
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

   private void createControl() {
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
      tableXViewer.setContentProvider(new PolicyContentProvider(accessControlList, object));
      tableXViewer.setLabelProvider(new PolicyLabelProvider(tableXViewer));
      tableXViewer.setInput(accessControlList.values());
   }

   public Map<String, AccessControlData> getAccessControlList() {
      return accessControlList;
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
}
