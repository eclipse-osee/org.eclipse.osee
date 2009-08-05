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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class ArtifactModelLabelProvider implements ITableLabelProvider {
   public ArtifactModelLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      String result = "";
      ArtifactModel model = (ArtifactModel) element;

      switch (columnIndex) {
         case RelationTableViewer.ADD_NUM:
            // This only has an image
            break;
         case RelationTableViewer.ARTIFACT_NAME_NUM:
            result = model.getName();
            break;
         case RelationTableViewer.ARTIFACT_TYPE_NUM:
            result = model.getDescriptor().getName();
            break;
         case RelationTableViewer.RATIONALE_NUM:
            result = model.getRationale();
            break;
         default:
            break;
      }
      return result;
   }

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      ArtifactModel model = (ArtifactModel) element;
      switch (columnIndex) {
         case RelationTableViewer.ADD_NUM:
            if (model.isAdd()) {
               return ImageManager.getImage(FrameworkImage.CHECKBOX_ENABLED);
            } else {
               return ImageManager.getImage(FrameworkImage.CHECKBOX_DISABLED);
            }
      }
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
