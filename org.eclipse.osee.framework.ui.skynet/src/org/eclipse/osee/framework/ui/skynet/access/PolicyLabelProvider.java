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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.access.PolicyTableViewer.Columns;
import org.eclipse.swt.graphics.Image;

public class PolicyLabelProvider implements ITableLabelProvider {

   public PolicyLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      try {
         if (element instanceof AccessControlData) {
            AccessControlData data = (AccessControlData) element;
            if (columnIndex == Columns.Person.ordinal()) {
               return data.getSubject().getDescriptiveName();
            } else if (columnIndex == Columns.Branch.ordinal()) {
               PermissionEnum permissionEnum = data.getBranchPermission();
               if (permissionEnum != null) return permissionEnum.getName();
            } else if (columnIndex == Columns.Artifact.ordinal()) {
               PermissionEnum permissionEnum = data.getArtifactPermission();
               if (permissionEnum != null) return permissionEnum.getName();
            } else if (columnIndex == Columns.Artifact_Type.ordinal()) {
               PermissionEnum permissionEnum = data.getArtifactTypePermission();
               if (permissionEnum != null) return permissionEnum.getName();
            } else if (columnIndex == Columns.Total.ordinal()) {
               PermissionEnum permissionEnum = data.getPermission();
               if (permissionEnum != null) return permissionEnum.getName();
            }
         }
      } catch (Exception ex) {
         return "Error: " + ex.getLocalizedMessage();
      }
      return "";
   }

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      if (columnIndex == Columns.Delete.ordinal()) return ImageManager.getImage(FrameworkImage.REMOVE);
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
    *      java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }
}