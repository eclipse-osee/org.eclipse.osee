/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowMergeDetailsAction extends Action {
   private CoveragePackage coveragePackage;
   private CoverageImport coverageImport;
   private XCoverageMergeViewer importXViewer;

   public ShowMergeDetailsAction() {
      super("Show Merge Details", IAction.AS_PUSH_BUTTON);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      try {
         if (((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().size() == 1) {
            ICoverage importCoverageEditorItem =
               ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().iterator().next();
            // If mergeitemgroup, want to link with parent of one of the children items
            if (importCoverageEditorItem instanceof MergeItemGroup) {
               importCoverageEditorItem =
                  ((MergeItemGroup) importCoverageEditorItem).getMergeItems().iterator().next().getParent();
            } else if (importCoverageEditorItem instanceof MergeItem) {
               importCoverageEditorItem = ((MergeItem) importCoverageEditorItem).getImportItem().getParent();
            } else {
               AWorkbench.popup("Must select a Merge Item");
               return;
            }
            MatchItem matchItem = MergeManager.getPackageCoverageItem(coveragePackage, importCoverageEditorItem);
            if (matchItem != null && matchItem.getPackageItem() != null) {
               MergeManager mergeManager = new MergeManager(coveragePackage, coverageImport);
               XResultData resultData = mergeManager.getMergeDetails(importCoverageEditorItem, new XResultData(false));
               resultData.report("Merge Details - " + importCoverageEditorItem.getName());
            } else {
               AWorkbench.popup("Can't find match item");
               return;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void setPackageXViewer(XCoverageMergeViewer packageXViewer, CoveragePackage coveragePackage) {
      this.coveragePackage = coveragePackage;
   }

   public XCoverageMergeViewer getImportXViewer() {
      return importXViewer;
   }

   public void setImportXViewer(XCoverageMergeViewer importXViewer, CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
      this.importXViewer = importXViewer;
   }

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

}
