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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.xcover.CoverageLabelProvider;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class CoverageMergeLabelProvider extends CoverageLabelProvider {

   private final CoverageMergeXViewer mergeXViewer;

   public CoverageMergeLabelProvider(CoverageMergeXViewer mergeXViewer) {
      super(mergeXViewer);
      this.mergeXViewer = mergeXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof MessageMergeItem && xCol.equals(CoverageXViewerFactory.Name)) {
         return ImageManager.getImage(FrameworkImage.X_RED);
      }
      if (element instanceof MessageMergeItem) {
         return null;
      }
      ICoverage coverageItem = (ICoverage) element;
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         return getCoverageItemUserImage(coverageItem);
      }
      if (xCol.equals(CoverageXViewerFactory.Name)) {
         return ImageManager.getImage(coverageItem.getOseeImage());
      }
      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import) && element instanceof IMergeItem) {
         if (!((IMergeItem) element).isImportAllowed() || !((IMergeItem) element).isCheckable()) {
            return null;
         }
         if (((IMergeItem) element).isChecked()) {
            return ImageManager.getImage(PluginUiImage.CHECKBOX_ENABLED);
         }
         return ImageManager.getImage(PluginUiImage.CHECKBOX_DISABLED);
      }
      return super.getColumnImage(element, xCol, columnIndex);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      ICoverage coverage = (ICoverage) element;
      if (xCol.equals(CoverageXViewerFactory.Name)) {
         return coverage.getName();
      }
      if (element instanceof MessageMergeItem) {
         return "";
      }
      if (xCol.equals(CoverageXViewerFactory.Guid)) {
         return coverage.getGuid();
      }
      if (xCol.equals(CoverageXViewerFactory.Location)) {
         return coverage.getLocation();
      }
      if (xCol.equals(CoverageXViewerFactory.Namespace)) {
         return coverage.getNamespace();
      }
      if (xCol.equals(CoverageXViewerFactory.Notes_Col)) {
         return coverage.getNotes();
      }
      if (xCol.equals(CoverageXViewerFactory.Coverage_Percent)) {
         return coverage.getCoveragePercentStr();
      }
      if (xCol.equals(CoverageXViewerFactory.Assignees_Col)) {
         if (coverage instanceof CoverageUnit) {
            return Artifacts.toString("; ", OseeCoverageUnitStore.getAssignees((CoverageUnit) coverage));
         }
         return "";
      }
      if (xCol.equals(CoverageXViewerFactory.Unit)) {
         String unit = "";
         if (element instanceof CoverageUnit) {
            unit = coverage.getName();
         } else {
            unit = coverage.getParent().getName();
         }

         return unit;
      }
      if (xCol.equals(CoverageXViewerFactory.Lines_Covered)) {
         if (element instanceof CoverageUnit) {
            return String.valueOf(((CoverageUnit) coverage).getCoverageItemsCoveredCount(true));
         }
      }
      if (xCol.equals(CoverageXViewerFactory.Total_Lines)) {
         if (element instanceof CoverageUnit) {
            return String.valueOf(((CoverageUnit) coverage).getCoverageItems(true).size());
         }
      }

      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import) && element instanceof IMergeItem) {
         return getMergeItemImportColumnText((IMergeItem) element);
      }
      if (coverage instanceof MergeItemGroup) {
         if (xCol.equals(CoverageXViewerFactory.Parent_Coverage_Unit)) {
            ICoverage cov = ((MergeItemGroup) coverage).getParent();
            return cov.getName();
         }
      }

      return super.getColumnText(element, xCol, columnIndex);
   }

   public String getMergeItemImportColumnText(IMergeItem mergeItem) {
      String result = "";
      if (!mergeItem.isImportAllowed()) {
         return "";
      }
      if (mergeItem instanceof MergeItem && mergeItem.getMergeType() == MergeType.CI_Method_Update) {
         MergeItem fullMergeItem = (MergeItem) mergeItem;
         if (fullMergeItem.getPackageItem() instanceof CoverageItem) {
            return String.format("%s from [%s] to [%s]", fullMergeItem.getMergeType().toString(),
               ((CoverageItem) fullMergeItem.getPackageItem()).getCoverageMethod().getName(),
               ((CoverageItem) fullMergeItem.getImportItem()).getCoverageMethod().getName());
         }
      }
      // Show all the children's import column so user can see what changes without having to expand
      if (mergeItem instanceof MergeItemGroup && mergeItem.getMergeType() == MergeType.CI_Changes) {
         MergeItemGroup group = (MergeItemGroup) mergeItem;
         Set<String> childrenStrs = new HashSet<String>();
         for (IMergeItem child : group.getMergeItems()) {
            if (child instanceof MergeItem) {
               String childImportStr = getMergeItemImportColumnText(child);
               if (Strings.isValid(childImportStr)) {
                  childrenStrs.add(childImportStr);
               }
            }
         }
         result += group.getMergeType().name() + " - " + Collections.toString("; ", childrenStrs);
      } else {
         result += mergeItem.getMergeType().toString();
      }
      if (Strings.isValid(mergeItem.getDetails())) {
         result = String.format("%s  Details: [%s]", result, mergeItem.getDetails());
      }
      return result;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public CoverageMergeXViewer getTreeViewer() {
      return mergeXViewer;
   }

   @Override
   public int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (element instanceof MessageMergeItem) {
         return 0;
      }
      ICoverage coverageItem = (ICoverage) element;
      if (xCol.equals(CoverageXViewerFactory.Coverage_Percent)) {
         return coverageItem.getCoveragePercent().intValue();
      }
      return 0;
   }

}
