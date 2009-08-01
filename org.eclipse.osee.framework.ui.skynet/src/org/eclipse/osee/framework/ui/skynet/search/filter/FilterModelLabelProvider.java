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
package org.eclipse.osee.framework.ui.skynet.search.filter;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class FilterModelLabelProvider implements ITableLabelProvider {

   public FilterModelLabelProvider() {
      super();
   };

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
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
   public Image getColumnImage(Object element, int columnIndex) {
      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            return ImageManager.getImage(FrameworkImage.REMOVE);
         case FilterTableViewer.SEARCH_NUM:
            if (((FilterModel) element).getSearchPrimitive() instanceof NotSearch) {
               return ImageManager.getImage(FrameworkImage.NOT_EQUAL);
            }
            break;
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
