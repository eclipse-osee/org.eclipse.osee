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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerLabelProvider implements ITableLabelProvider {
   private final XViewer viewer;

   /**
    * @param viewer
    */
   public XViewerLabelProvider(final XViewer viewer) {
      super();
      this.viewer = viewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = viewer.getXTreeColumn(columnIndex);
         if (xViewerColumn != null) {
            if (xViewerColumn instanceof XViewerValueColumn) {
               return ((XViewerValueColumn) xViewerColumn).getColumnImage(element, xViewerColumn);
            }
            return getColumnImage(element, viewer.getXTreeColumn(columnIndex));
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      return null;
   }

   public String getColumnText(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = viewer.getXTreeColumn(columnIndex);
         if (xViewerColumn != null) {
            if (xViewerColumn instanceof XViewerValueColumn) {
               return ((XViewerValueColumn) xViewerColumn).getColumnText(element, xViewerColumn);
            }
            return getColumnText(element, viewer.getXTreeColumn(columnIndex));
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   public abstract Image getColumnImage(Object element, XViewerColumn column);

   public abstract String getColumnText(Object element, XViewerColumn column);
}
