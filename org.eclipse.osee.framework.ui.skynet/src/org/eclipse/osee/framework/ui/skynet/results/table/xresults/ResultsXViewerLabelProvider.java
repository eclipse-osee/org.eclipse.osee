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
package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ResultsXViewerLabelProvider extends XViewerLabelProvider {
   Font font = null;
   private final ResultsXViewer resultsXViewer;

   public ResultsXViewerLabelProvider(ResultsXViewer resultsXViewer) {
      super(resultsXViewer);
      this.resultsXViewer = resultsXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      IResultsXViewerRow task = ((IResultsXViewerRow) element);
      if (task == null) return "";
      return task.getValue(columnIndex);
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider#getColumnImage(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
    */
   @Override
   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

}
