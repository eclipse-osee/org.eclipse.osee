/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MatchType;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackageImportManager {

   private final CoveragePackage coveragePackage;
   private final MergeManager mergeManager;

   public CoveragePackageImportManager(MergeManager mergeManager) {
      this.mergeManager = mergeManager;
      this.coveragePackage = mergeManager.getCoveragePackage();
   }

   public XResultData importItems(ISaveable saveable, Collection<MergeItem> mergeItems) throws OseeCoreException {
      XResultData rd = new XResultData(false);
      if (!validateEditable(rd, saveable)) return rd;
      if (!validateMergeTypes(rd, mergeItems)) return rd;
      if (!validateChildrenAreUnique(rd, mergeItems)) return rd;

      for (MergeItem mergeItem : mergeItems) {
         if (mergeItem.getMergeType() == MergeType.Add) {
            CoverageUnit coverageUnit = (CoverageUnit) mergeItem.getImportItem();
            importCoverageUnitItem(rd, coverageUnit);
            // add all children items
            for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits(true)) {
               importCoverageUnitItem(rd, childCoverageUnit);
            }
         } else {
            rd.logError(String.format("Unsupported merge type [%s] for merge item [%s]", mergeItem.getMergeType(),
                  mergeItem));
         }
      }

      if (rd.getNumErrors() > 0) {
         AWorkbench.popup(rd.getNumErrors() + " Errors Found; Not Persisting");
         rd.logError(rd.getNumErrors() + " Errors Found; Not Persisting");
      } else {
         Result result = saveable.save();
         if (result.isTrue()) {
            rd.log("\nChanges Persisted");
         } else {
            rd.logError("\n" + result.getText());
         }
      }
      return rd;
   }

   /**
    * Takes import items from coverageImport and applies them to coveragePackage
    */
   private void importCoverageUnitItem(XResultData rd, CoverageUnit importItem) {
      System.out.println("importItemsRecurse => " + importItem);
      try {
         rd.log("Processing " + importItem.getName());
         if (!(importItem instanceof CoverageUnit)) {
            rd.logError(String.format("[%s] invalid for Import; Only import CoverageUnits",
                  importItem.getClass().getSimpleName()));
         }
         CoverageUnit importCoverageUnit = (CoverageUnit) importItem;

         MatchItem matchItem = mergeManager.getPackageCoverageItem(importItem);
         ICoverage packageItem = matchItem == null ? null : matchItem.getPackageItem();
         // Determine if item already exists first
         if (packageItem != null) {
            // save assignees and notes and RATIONALE before child overwrites
            // ((CoverageUnit) importItem).updateAssigneesAndNotes((CoverageUnit) packageItem);
         }
         // This is new item
         if (packageItem == null) {
            // Check if parent item exists
            ICoverage parentImportItem = importItem.getParent();
            // If null, this is top level item, just add to package
            if (parentImportItem instanceof CoverageImport) {
               coveragePackage.addCoverageUnit(((CoverageUnit) importItem).copy(true));
               rd.log(String.format("Added [%s] as top level CoverageUnit", importCoverageUnit));
               rd.log("");
            } else {

               // Else, want to add item to same parent
               CoverageUnit parentCoverageUnit = (CoverageUnit) importItem.getParent();
               MatchItem parentMatchItem = mergeManager.getPackageCoverageItem(parentCoverageUnit);
               CoverageUnit parentPackageItem = (CoverageUnit) parentMatchItem.getPackageItem();
               parentPackageItem.addCoverageUnit(importCoverageUnit.copy(true));
               rd.log(String.format("Added [%s] to parent [%s]", importCoverageUnit, parentCoverageUnit));
               rd.log("");

            }
         }
      } catch (Exception ex) {
         rd.logError("Exception: " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private boolean validateEditable(XResultData rd, ISaveable saveable) throws OseeCoreException {
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         rd.logError(result.getText());
         return false;
      }
      return true;
   }

   private boolean validateMergeTypes(XResultData rd, Collection<MergeItem> mergeItems) throws OseeCoreException {
      boolean valid = true;
      for (MergeItem mergeItem : mergeItems) {
         if (mergeItem.getMergeType().isError()) {
            rd.log(String.format("Can't merge item [%s] with error", mergeItem));
            valid = false;
         }
      }
      return valid;
   }

   public boolean validateChildrenAreUnique(XResultData rd, Collection<MergeItem> mergeItems) throws OseeStateException {
      boolean valid = true;
      for (MergeItem mergeItem : mergeItems) {
         if (!(mergeItem.getImportItem() instanceof CoverageUnit)) {
            rd.logError(String.format("Invalid Item for Import; Don't import [%s]",
                  mergeItem.getImportItem().getClass().getSimpleName()));
            valid = false;
            continue;
         }
         boolean isValid = validateChildrenAreUniqueRecurse(rd, (CoverageUnit) mergeItem.getImportItem());
         if (!isValid) valid = false;
      }
      return valid;
   }

   private boolean validateChildrenAreUniqueRecurse(XResultData rd, CoverageUnit coverageUnit) throws OseeStateException {
      boolean valid = true;
      for (ICoverage importItem1 : coverageUnit.getChildren()) {
         for (ICoverage importItem2 : coverageUnit.getChildren()) {

            MatchType matchType = MergeManager.isConceptuallyEqual(importItem1, importItem2);
            if ((matchType == MatchType.Match__Name_And_Method) && importItem1 != importItem2) {
               rd.logError(String.format("CoverageUnit [%s] has two equal children [%s][%s]; Can't import.",
                     coverageUnit, importItem1, importItem2));
               valid = false;
            }
         }
      }
      for (ICoverage childItem : coverageUnit.getChildren()) {
         if (childItem instanceof CoverageUnit) {
            boolean isValid = validateChildrenAreUniqueRecurse(rd, (CoverageUnit) childItem);
            if (!isValid) valid = false;
         }
      }
      return valid;
   }

}
