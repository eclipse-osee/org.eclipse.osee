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
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

public class FilterModelLabelProvider implements ITableLabelProvider {

   private static final Image deleteImage = SkynetGuiPlugin.getInstance().getImage("remove.gif");
   private static final Image notImage = SkynetGuiPlugin.getInstance().getImage("not_equal.gif");

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
      Image result = null;

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            result = deleteImage;
            break;
         case FilterTableViewer.SEARCH_NUM:
            if (((FilterModel) element).getSearchPrimitive() instanceof NotSearch) result = notImage;
            break;
      }
      return result;
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
