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

package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class ArtifactModelLabelProvider implements ITableLabelProvider {
   public ArtifactModelLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   @Override
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
   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      ArtifactModel model = (ArtifactModel) element;
      switch (columnIndex) {
         case RelationTableViewer.ADD_NUM:
            if (model.isAdd()) {
               return ImageManager.getImage(PluginUiImage.CHECKBOX_ENABLED);
            } else {
               return ImageManager.getImage(PluginUiImage.CHECKBOX_DISABLED);
            }
      }
      return null;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }
}
