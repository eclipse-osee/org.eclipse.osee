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
import org.eclipse.osee.coverage.editor.xcover.CoverageLabelProvider;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
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
      if (element instanceof MessageMergeItem && xCol.equals(CoverageMergeXViewerFactory.Name)) return ImageManager.getImage(FrameworkImage.X_RED);
      if (element instanceof MessageMergeItem) return null;
      ICoverage coverageItem = (ICoverage) element;
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         return getCoverageItemUserImage(coverageItem);
      }
      if (xCol.equals(CoverageMergeXViewerFactory.Name)) return ImageManager.getImage(coverageItem.getOseeImage());
      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import) && element instanceof IMergeItem) {
         if (!((IMergeItem) element).isImportAllowed() || !((IMergeItem) element).isCheckable()) {
            return null;
         }
         if (((IMergeItem) element).isChecked()) {
            return ImageManager.getImage(FrameworkImage.CHECKBOX_ENABLED);
         }
         return ImageManager.getImage(FrameworkImage.CHECKBOX_DISABLED);
      }
      return super.getColumnImage(element, xCol, columnIndex);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICoverage coverage = (ICoverage) element;
      if (xCol.equals(CoverageMergeXViewerFactory.Name)) return coverage.getName();
      if (element instanceof MessageMergeItem) return "";
      if (xCol.equals(CoverageXViewerFactory.Guid)) return coverage.getGuid();
      if (xCol.equals(CoverageXViewerFactory.Location)) return coverage.getLocation();
      if (xCol.equals(CoverageXViewerFactory.File_Contents)) return coverage.getFileContents();
      if (xCol.equals(CoverageXViewerFactory.Namespace)) return coverage.getNamespace();
      if (xCol.equals(CoverageXViewerFactory.Notes_Col)) return coverage.getNotes();
      if (xCol.equals(CoverageXViewerFactory.Coverage_Percent)) {
         return coverage.getCoveragePercentStr();
      }
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         if (coverage instanceof CoverageUnit) {
            return Artifacts.toString("; ", OseeCoverageUnitStore.getAssignees((CoverageUnit) coverage));
         }
         return "";
      }

      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import) && element instanceof IMergeItem) {
         if (!((IMergeItem) element).isImportAllowed()) {
            return "";
         }
         return ((IMergeItem) element).getMergeType().toString();
      }
      if (coverage instanceof MergeItemGroup) {
         if (xCol.equals(CoverageXViewerFactory.Parent_Coverage_Unit)) {
            ICoverage cov = ((MergeItemGroup) coverage).getParent();
            return super.getColumnText(cov, xCol, columnIndex);
         }
      }

      return super.getColumnText(element, xCol, columnIndex);
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

   @Override
   public int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (element instanceof MessageMergeItem) return 0;
      ICoverage coverageItem = (ICoverage) element;
      if (xCol.equals(CoverageXViewerFactory.Coverage_Percent)) {
         return coverageItem.getCoveragePercent();
      }
      return 0;
   }

}
