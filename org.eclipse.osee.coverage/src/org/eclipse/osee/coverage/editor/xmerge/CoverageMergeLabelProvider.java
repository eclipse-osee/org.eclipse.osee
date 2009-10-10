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
package org.eclipse.osee.coverage.editor.xmerge;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageLabelProvider;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class CoverageMergeLabelProvider extends CoverageLabelProvider {

   private final CoverageMergeXViewer mergeXViewer;

   public CoverageMergeLabelProvider(CoverageMergeXViewer mergeXViewer) {
      super(mergeXViewer);
      this.mergeXViewer = mergeXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICoverageEditorItem coverageItem = (ICoverageEditorItem) element;
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         return getCoverageItemUserImage(coverageItem);
      }
      if (xCol.equals(CoverageMergeXViewerFactory.Name)) return ImageManager.getImage(coverageItem.getOseeImage());
      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import)) {
         if (!mergeXViewer.isImportAllowed(coverageItem)) {
            return null;
         }
         if (mergeXViewer.isImportChecked(coverageItem)) {
            return ImageManager.getImage(FrameworkImage.CHECKBOX_ENABLED);
         }
         return ImageManager.getImage(FrameworkImage.CHECKBOX_DISABLED);
      }

      return coverageItem.getCoverageEditorImage(xCol);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICoverageEditorItem coverageItem = (ICoverageEditorItem) element;
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         return CoverageLabelProvider.getCoverageItemUsers(coverageItem);
      }
      if (xCol.equals(CoverageMergeXViewerFactory.Name)) return coverageItem.getName();
      if (xCol.equals(CoverageXViewerFactory.Guid)) return coverageItem.getGuid();
      if (xCol.equals(CoverageXViewerFactory.Location)) return coverageItem.getLocation();
      if (xCol.equals(CoverageXViewerFactory.Text)) return coverageItem.getText();
      if (xCol.equals(CoverageXViewerFactory.Namespace)) return coverageItem.getNamespace();
      if (xCol.equals(CoverageXViewerFactory.Notes_Col)) return coverageItem.getNotes();

      return coverageItem.getCoverageEditorValue(xCol);
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

   @Override
   public CoverageMergeXViewer getTreeViewer() {
      return mergeXViewer;
   }
}
