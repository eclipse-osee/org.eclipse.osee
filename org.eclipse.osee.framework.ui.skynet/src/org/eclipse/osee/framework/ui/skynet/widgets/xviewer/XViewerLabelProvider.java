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
      if (viewer.getXTreeColumn(columnIndex) != null) return getColumnImage(element, viewer.getXTreeColumn(columnIndex));
      return null;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (viewer.getXTreeColumn(columnIndex) != null) return getColumnText(element, viewer.getXTreeColumn(columnIndex));
      return "";
   }

   protected abstract Image getColumnImage(Object element, XViewerColumn column);

   protected abstract String getColumnText(Object element, XViewerColumn column);
}
