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
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Jeff C. Phillips
 */
public class PolicyTableCellModifier implements ICellModifier {

   private PolicyTableViewer policyTableViewer;

   public PolicyTableCellModifier(PolicyTableViewer policyTableViewer) {
      super();
      this.policyTableViewer = policyTableViewer;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      // Find the index of the column
      int columnIndex = policyTableViewer.getColumnNames().indexOf(property);

      switch (columnIndex) {
         case PolicyTableViewer.DELETE_NUM:
            return true;
      }
      return true;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {
      // Find the index of the column
      int columnIndex = policyTableViewer.getColumnNames().indexOf(property);

      switch (columnIndex) {
         case PolicyTableViewer.DELETE_NUM:
            return new Boolean(false);
         case PolicyTableViewer.ARTIFACT_POLICY_LEVEL_NUM:
            return ((AccessControlData) element).getPermission().ordinal();
      }
      return "";
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {

      // Find the index of the column
      int columnIndex = policyTableViewer.getColumnNames().indexOf(property);

      TableItem item = (TableItem) element;
      AccessControlData data = (AccessControlData) item.getData();

      switch (columnIndex) {
         case PolicyTableViewer.DELETE_NUM:
            policyTableViewer.removeData(data);
            break;
         case PolicyTableViewer.ARTIFACT_POLICY_LEVEL_NUM:
            int index = (Integer) value;

            if (index != -1) policyTableViewer.modifyPermissionLevel(data, PermissionEnum.values()[index]);
            break;

         default:
      }
      policyTableViewer.refresh();
   }
}
