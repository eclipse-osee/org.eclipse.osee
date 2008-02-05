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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.ui.skynet.access.PolicyTableViewer.Columns;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Jeff C. Phillips
 */
public class PolicyTableCellModifier implements ICellModifier {

   private PolicyTableViewer policyTableViewer;
   private boolean enabled = true;

   public PolicyTableCellModifier(PolicyTableViewer policyTableViewer) {
      super();
      this.policyTableViewer = policyTableViewer;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      // Find the index of the column
      int columnIndex = Columns.valueOf(property).ordinal();
      if (columnIndex == Columns.Delete.ordinal() && isEnabled()) return true;
      return false;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {
      // Find the index of the column
      int columnIndex = Columns.valueOf(property).ordinal();
      if (columnIndex == Columns.Delete.ordinal()) {
         return new Boolean(false);
      } else if (columnIndex == Columns.Artifact.ordinal()) {
         return ((AccessControlData) element).getPermission().ordinal();
      }
      return "";
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {

      // Find the index of the column
      int columnIndex = Columns.valueOf(property).ordinal();

      TableItem item = (TableItem) element;
      AccessControlData data = (AccessControlData) item.getData();

      if (columnIndex == Columns.Delete.ordinal()) {
         policyTableViewer.removeData(data);
      } else if (columnIndex == Columns.Delete.ordinal()) {
         int index = (Integer) value;
         if (index != -1) policyTableViewer.modifyPermissionLevel(data, PermissionEnum.values()[index]);
      }
      policyTableViewer.refresh();
   }

   /**
    * @return the enabled
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * Don't disable entire viewer, just delete button
    * 
    * @param enabled the enabled to set
    */
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
