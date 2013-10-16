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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditorMergeTab;
import org.eclipse.osee.coverage.editor.IMergeItemSelectionProvider;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OverwritePackageWithSelectedImportItems extends Action {

   private final CoveragePackage coveragePackage;
   private final XCoverageMergeViewer packageXViewer;
   private final XCoverageMergeViewer importXViewer;
   private final CoverageEditorMergeTab mergeTab;

   public OverwritePackageWithSelectedImportItems(CoverageEditorMergeTab mergeTab, CoveragePackage coveragePackage, XCoverageMergeViewer packageXViewer, CoverageImport coverageImport, XCoverageMergeViewer importXViewer) {
      super("Overwrite Package With Selected Import Items", IAction.AS_PUSH_BUTTON);
      this.mergeTab = mergeTab;
      this.coveragePackage = coveragePackage;
      this.packageXViewer = packageXViewer;
      this.importXViewer = importXViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.IMPORT);
   }

   @Override
   public void run() {
      try {
         if (((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().size() > 0) {
            Collection<MatchItem> matchItems = getSelectedMatchItems(importXViewer);
            if (matchItems.size() > 0) {
               if (userConfirmsOverwrite(matchItems)) {
                  // First, delete coverageUnit on other side
                  DeleteCoverUnitAction action =
                     new DeleteCoverUnitAction(new CoveragePackageMatches(matchItems), packageXViewer.getXViewer(),
                        packageXViewer.getXViewer());
                  action.run();
                  // TODO Next, import selected items over
                  ImportSelectedMergeItemsAction importMergeItems =
                     new ImportSelectedMergeItemsAction(new SelectedMergeItems(matchItems), mergeTab);
                  importMergeItems.run();
               }
            }
         }
         // If nothing selected, diff entire package/import
         else {
            AWorkbench.popup("Must select item to overwrite");
            return;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
   private class SelectedMergeItems implements IMergeItemSelectionProvider {
      private final Collection<MatchItem> matchItems;

      public SelectedMergeItems(Collection<MatchItem> matchItems) {
         this.matchItems = matchItems;
      }

      @Override
      public Collection<IMergeItem> getSelectedMergeItems() throws OseeCoreException {
         List<IMergeItem> mergeItems = new LinkedList<IMergeItem>();
         for (MatchItem matchItem : matchItems) {
            MergeItem mergeItem =
               new MergeItem(MergeType.Add, matchItem.getPackageItem(), matchItem.getImportItem(), true);
            mergeItem.setChecked(true);
            mergeItems.add(mergeItem);
         }
         return mergeItems;
      }
   }
   private class CoveragePackageMatches implements ISelectedCoverageEditorItem {

      private final Collection<MatchItem> mergeItems;

      public CoveragePackageMatches(Collection<MatchItem> mergeItems) {
         this.mergeItems = mergeItems;
      }

      @Override
      public Collection<ICoverage> getSelectedCoverageEditorItems() {
         List<ICoverage> packageCoverages = new LinkedList<ICoverage>();
         for (MatchItem item : mergeItems) {
            packageCoverages.add(item.getPackageItem());
         }
         return packageCoverages;
      }

      @Override
      public void setSelectedCoverageEditorItem(ICoverage item) {
         // do nothing
      }

   }

   private Collection<MatchItem> getSelectedMatchItems(XCoverageMergeViewer importXViewer2) throws OseeStateException {
      List<MatchItem> matchItems = new LinkedList<MatchItem>();
      for (ICoverage importCoverageEditorItem : ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems()) {
         // If mergeitemgroup, want to link with parent of one of the children items
         if (importCoverageEditorItem instanceof MergeItemGroup) {
            MergeItemGroup mergeItemGroup = (MergeItemGroup) importCoverageEditorItem;
            // if deleted, just show whole report cause nothing to start at
            if (mergeItemGroup.getMergeType() == MergeType.Delete_And_Reorder || mergeItemGroup.getMergeType() == MergeType.Delete) {
               AWorkbench.popup("Can't run on Delete items");
               return Collections.emptyList();
            } else {
               importCoverageEditorItem = mergeItemGroup.getMergeItems().iterator().next().getParent();
            }
         } else if (importCoverageEditorItem instanceof MergeItem) {
            importCoverageEditorItem = ((MergeItem) importCoverageEditorItem).getImportItem();
         } else {
            AWorkbench.popup("Must select Merge Item(s)");
            return Collections.emptyList();
         }
         if (importCoverageEditorItem instanceof CoverageItem) {
            importCoverageEditorItem = importCoverageEditorItem.getParent();
         }
         while (!importCoverageEditorItem.isFolder() && !ShowMergeReportAction.isFile(importCoverageEditorItem)) {
            importCoverageEditorItem = importCoverageEditorItem.getParent();
         }
         if (importCoverageEditorItem instanceof CoveragePackageBase) {
            AWorkbench.popup("Can't run on Package Base");
            return Collections.emptyList();
         }
         MatchItem matchItem = MergeManager.getPackageCoverageItem(coveragePackage, importCoverageEditorItem);
         if (matchItem.getPackageItem() == null) {
            AWorkbench.popup("Can't find package match for " + matchItem.getImportItem());
            return Collections.emptyList();
         }
         matchItems.add(matchItem);
      }
      return matchItems;
   }

   private boolean userConfirmsOverwrite(Collection<MatchItem> matchItems) {
      StringBuilder results = new StringBuilder(getText() + "\n\n");
      for (MatchItem item : matchItems) {
         results.append(String.format("\n Replace [%s] with [%s]", item.getPackageItem(), item.getImportItem()));
      }
      results.append("\n\nOk to confirm");
      String resultsStr = results.toString().replaceAll("\n", "<br/>");

      HtmlDialog dialog =
         new HtmlDialog("Overwrite Packge Item(s) with Import Item(s)", "", AHTML.simplePage(resultsStr));
      dialog.open();
      if (dialog.getReturnCode() == 0) {
         return true;
      }
      return false;
   }
}
