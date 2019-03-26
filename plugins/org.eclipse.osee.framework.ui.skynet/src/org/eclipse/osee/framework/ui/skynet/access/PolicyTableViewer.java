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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

   private final Map<String, AccessControlData> accessControlList = new HashMap<>();
   private final Collection<AccessControlData> deleteControlList = new ArrayList<>();
   private final Object object;
   private final Composite parent;
   private PermissionEnum maxModificationLevel = PermissionEnum.FULLACCESS;

   private PolicyTableXviewer tableXViewer;

   public PolicyTableViewer(Composite parent, Object object) {
      this.parent = parent;
      this.object = object;

      createControl();
   }

   public void allowTableModification(boolean allow) {
      ((PolicyTableCellModifier) tableXViewer.getCellModifier()).setDeleteEnabled(allow);
   }

   public void addOrModifyItem(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = AccessControlManager.getAccessObject(object);
      AccessControlData data = accessControlList.get(subject.getGuid());
      if (data == null) {
         data = new AccessControlData(subject, accessObject, permission, true);
      } else {
         modifyPermissionLevel(data, permission);
      }
      accessControlList.put(data.getSubject().getGuid(), data);
      tableXViewer.refresh();
   }

   private void createControl() {
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 150;
      gd.widthHint = 500;

      tableXViewer = new PolicyTableXviewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
         new PolicyTableXViewerFactory(), true, true);
      tableXViewer.setUseHashlookup(true);
      tableXViewer.setColumnProperties(PolicyTableColumns.getNames());
      tableXViewer.getTree().setLayoutData(gd);
      tableXViewer.setColumnMultiEditEnabled(true);
      tableXViewer.setMaxPermission(maxModificationLevel);

      CellEditor[] validEditors = new CellEditor[PolicyTableColumns.values().length];
      validEditors[1] = new CheckboxCellEditor(parent, SWT.NONE);
      String[] names = PolicyTableColumns.getNames();
      for (int i = 0; i < names.length; i++) {
         if (names[i].equals("totalAccess")) {
            validEditors[i - 1] =
               new ComboBoxCellEditor(parent, PermissionEnum.getPermissionNames(), SWT.BORDER | SWT.READ_ONLY);
         }
      }
      tableXViewer.setCellEditors(validEditors);
      tableXViewer.setCellModifier(new PolicyTableCellModifier(this));
      tableXViewer.setContentProvider(new PolicyContentProvider(accessControlList, object, deleteControlList));
      tableXViewer.setLabelProvider(new PolicyLabelProvider(tableXViewer));
      tableXViewer.setInput(accessControlList.values());
      tableXViewer.setTableViewer(this);
   }

   public Map<String, AccessControlData> getAccessControlList() {
      return accessControlList;
   }

   public void refresh() {
      tableXViewer.refresh();
   }

   public void removeData(AccessControlData data) {
      try {
         deleteControlList.add(data);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      accessControlList.remove(data.getSubject().getGuid());
   }

   public void removeDataFromDB() {
      try {
         for (AccessControlData data : deleteControlList) {
            AccessControlManager.removeAccessControlDataIf(true, data);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void modifyPermissionLevel(AccessControlData data, PermissionEnum permission) {
      boolean canModify = data.getPermission().getRank() <= maxModificationLevel.getRank();
      canModify = canModify && permission.getRank() <= maxModificationLevel.getRank();
      canModify = canModify || permission.equals(PermissionEnum.DENY);
      if (canModify) {
         data.setPermission(permission);
      } else {
         AWorkbench.popup("ERROR",
            "Cannot promote user to higher status then yourself or cannot demote user with higher status");
      }
   }

   public int getCount() {
      return accessControlList.size();
   }

   public void setMaxModificationLevel(PermissionEnum newValue) {
      maxModificationLevel = newValue;
      tableXViewer.setMaxPermission(maxModificationLevel);
   }

   public boolean currentUserCanModifyLock() {
      if (isArtifact()) {
         Artifact artifact = (Artifact) object;

         IUserGroup oseeAccessGroup = UserGroupService.getOseeAccessAdmin();
         boolean isOseeAccessAdmin = oseeAccessGroup.isCurrentUserMember();

         boolean canUnlockObject = AccessControlManager.canUnlockObject(artifact, UserManager.getUser());
         boolean isUserFullAccess = AccessControlManager.getPermission(artifact).matches(PermissionEnum.FULLACCESS);
         return isOseeAccessAdmin || canUnlockObject || isUserFullAccess;
      }
      return false;
   }

   public boolean isArtifact() {
      return object instanceof Artifact;
   }

   public Artifact getArtifact() {
      Artifact result = null;
      if (isArtifact()) {
         result = (Artifact) object;
      }
      return result;
   }

}
