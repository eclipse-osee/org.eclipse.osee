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
package org.eclipse.osee.coverage.editor.xcover;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class CoverageLabelProvider extends XViewerLabelProvider {

   private final CoverageXViewer xViewer;

   public CoverageLabelProvider(CoverageXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) throws OseeCoreException {
      ICoverageEditorItem coverageItem = (ICoverageEditorItem) element;
      if (dCol.equals(CoverageXViewerFactory.User_Col)) {
         return ImageManager.getImage(coverageItem.getUser());
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException {
      ICoverageEditorItem coverageItem = (ICoverageEditorItem) element;
      if (aCol.equals(CoverageXViewerFactory.User_Col)) return coverageItem.getUser().getName();
      return "Unhandled Column";
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public CoverageXViewer getTreeViewer() {
      return xViewer;
   }
}
