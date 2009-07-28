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
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class PolicyLabelProvider extends XViewerLabelProvider {

   public PolicyLabelProvider(XViewer viewer) {
      super(viewer);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws Exception {
      String ret;
      try {
         ret = getColumnText(col, (AccessControlData) element);
      } catch (Exception ex) {
         ret = "Error: " + ex.getLocalizedMessage();
      }

      return ret;
   }

   private String getColumnText(XViewerColumn col, AccessControlData data) {
      String colId = col.getId();
      if (colId.equals(PolicyTableColumns.userName.toString()))
         return data.getSubject().getName();
      else {
         PermissionEnum permissionEnum = null;
         if (colId.equals(PolicyTableColumns.totalAccess.toString())) {
            permissionEnum = data.getPermission();
         } else if (colId.equals(PolicyTableColumns.branchAccess.toString())) {
            permissionEnum = data.getBranchPermission();
         } else if (colId.equals(PolicyTableColumns.artifactType.toString())) {
            permissionEnum = data.getArtifactTypePermission();
         } else if (colId.equals(PolicyTableColumns.artifact.toString())) {
            permissionEnum = data.getArtifactPermission();
         }

         if (permissionEnum != null) return permissionEnum.getName();
      }

      return "";
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      String colId = col.getId();
      if (colId.equals(PolicyTableColumns.delete.toString()))
         return ImageManager.getImage(FrameworkImage.REMOVE);
      else
         return null;
   }

   public void dispose() {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public boolean isLabelProperty(Object element, String property) {
      return true;
   }

}