/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.access;

import java.util.Collections;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 */
public class PolicyTableCellModifier implements ICellModifier {

   private final PolicyTableViewer policyTableViewer;
   private boolean enabled = true;

   public PolicyTableCellModifier(PolicyTableViewer policyTableViewer) {
      super();
      this.policyTableViewer = policyTableViewer;
   }

   @Override
   public boolean canModify(Object element, String property) {
      if (policyTableViewer.isArtifact() && element instanceof AccessControlData && ((AccessControlData) element).getPermission() == PermissionEnum.USER_LOCK) {
         return policyTableViewer.currentUserCanModifyLock();
      }
      boolean toReturn = property.equals(PolicyTableColumns.delete.toString()) || property.equals(
         PolicyTableColumns.totalAccess.toString());
      return toReturn;
   }

   @Override
   public Object getValue(Object element, String property) {
      if (property.equals(PolicyTableColumns.delete.toString())) {
         return new Boolean(false);
      } else if (property.equals(PolicyTableColumns.artifact.toString()) || property.equals(
         PolicyTableColumns.totalAccess.toString()) || property.equals(PolicyTableColumns.branchAccess.toString())) {
         return ((AccessControlData) element).getPermission().ordinal();
      }
      return "";
   }

   @Override
   public void modify(Object element, String property, Object value) {
      TreeItem item = (TreeItem) element;
      AccessControlData data = (AccessControlData) item.getData();
      if (policyTableViewer.isArtifact() && data.getPermission() == PermissionEnum.USER_LOCK) {
         AccessControlManager.unLockObjects(Collections.singleton(policyTableViewer.getArtifact()),
            UserManager.getUser());
         policyTableViewer.removeData(data);
      } else {
         if (canDelete() && property.equals(PolicyTableColumns.delete.toString())) {
            policyTableViewer.removeData(data);
         } else if (property.equals(PolicyTableColumns.totalAccess.toString())) {
            item.setData(value);
         }
      }
      policyTableViewer.refresh();
   }

   public boolean canDelete() {
      return enabled;
   }

   /**
    * Don't disable entire viewer, just delete button
    */
   public void setDeleteEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
