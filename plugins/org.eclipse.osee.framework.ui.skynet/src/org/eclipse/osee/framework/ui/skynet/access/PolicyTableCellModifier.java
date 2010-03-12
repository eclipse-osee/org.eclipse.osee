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
import org.eclipse.swt.widgets.TreeItem;

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

   public boolean canModify(Object element, String property) {
      if (property.equals(PolicyTableColumns.delete.toString())) return true;
      return false;
   }

   public Object getValue(Object element, String property) {
      if (property.equals(PolicyTableColumns.delete.toString()))
         return new Boolean(false);
      else if (property.equals(PolicyTableColumns.artifact.toString())) {
         return ((AccessControlData) element).getPermission().ordinal();
      }
      return "";
   }

   public void modify(Object element, String property, Object value) {
      TreeItem item = (TreeItem) element;
      AccessControlData data = (AccessControlData) item.getData();

      if (property.equals(PolicyTableColumns.delete.toString())) {
         policyTableViewer.removeData(data);
      }

      policyTableViewer.refresh();
   }

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
