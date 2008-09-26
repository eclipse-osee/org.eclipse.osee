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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class XViewerTextFilter extends ViewerFilter {

   private final XViewer xViewer;
   private ITableLabelProvider labelProv;
   private Pattern pattern;
   private Matcher matcher;

   public XViewerTextFilter(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public void setFilterText(String text) {
      pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (pattern == null) return true;
      if (labelProv == null) labelProv = (ITableLabelProvider) xViewer.getLabelProvider();
      for (XViewerColumn xCol : xViewer.getCustomizeMgr().getCurrentTableColumns()) {
         if (xCol.isShow()) {
            String cellStr =
                  labelProv.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
            if (cellStr != null) {
               matcher = pattern.matcher(cellStr);
               if (matcher.find()) return true;
            }
         }
      }
      return false;
   }

}
