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
import org.eclipse.osee.framework.access.AccessControlData;
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
      boolean toReturn =
         property.equals(PolicyTableColumns.delete.toString()) || property.equals(PolicyTableColumns.totalAccess.toString());
      return toReturn;
   }

   @Override
   public Object getValue(Object element, String property) {
      if (property.equals(PolicyTableColumns.delete.toString())) {
         return new Boolean(false);
      } else if (property.equals(PolicyTableColumns.artifact.toString()) || property.equals(PolicyTableColumns.totalAccess.toString()) || property.equals(PolicyTableColumns.branchAccess.toString())) {
         return ((AccessControlData) element).getPermission().ordinal();
      }
      return "";
   }

   @Override
   public void modify(Object element, String property, Object value) {
      TreeItem item = (TreeItem) element;
      AccessControlData data = (AccessControlData) item.getData();

      if (canDelete() && property.equals(PolicyTableColumns.delete.toString())) {
         policyTableViewer.removeData(data);
      } else if (property.equals(PolicyTableColumns.totalAccess.toString())) {
         item.setData(value);
      }

      policyTableViewer.refresh();
   }

   public boolean canDelete() {
      return enabled;
   }

   /**
    * Don't disable entire viewer, just delete button
    * 
    * @param enabled the enabled to set
    */
   public void setDeleteEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
