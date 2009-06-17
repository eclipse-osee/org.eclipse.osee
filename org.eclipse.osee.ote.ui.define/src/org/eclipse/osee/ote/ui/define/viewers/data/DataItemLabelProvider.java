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
package org.eclipse.osee.ote.ui.define.viewers.data;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class DataItemLabelProvider extends XViewerLabelProvider {

   /**
    * @param viewer
    */
   public DataItemLabelProvider(XViewer viewer) {
      super(viewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      Image toReturn = null;
      if (columnIndex == 0) {
         if (element instanceof IXViewerItem) {
            toReturn = ((IXViewerItem) element).getImage();
         }
      }
      return toReturn;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) {
      String toReturn = "";
      if (element instanceof String && columnIndex == 1) {
         toReturn = (String) element;
      } else if (element instanceof IXViewerItem) {
         toReturn = ((IXViewerItem) element).getLabel(columnIndex);
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

}
