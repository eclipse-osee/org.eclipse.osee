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

package org.eclipse.osee.framework.ui.skynet.search.filter;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class FilterModelLabelProvider implements ITableLabelProvider {

   public FilterModelLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   @Override
   public String getColumnText(Object element, int columnIndex) {
      String result = "";
      FilterModel model = (FilterModel) element;

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            // This only has an image
            break;
         case FilterTableViewer.SEARCH_NUM:
            result = model.getSearch();
            break;
         case FilterTableViewer.TYPE_NUM:
            result = model.getType();
            break;
         case FilterTableViewer.VALUE_NUM:
            result = model.getValue();
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
      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            return ImageManager.getImage(FrameworkImage.REMOVE);
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
