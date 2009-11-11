/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public class MergeManager {
   private final CoveragePackage coveragePackage;
   private final CoverageImport coverageImport;
   private List<MergeItem> mergeItems = null;

   public MergeManager(CoveragePackage coveragePackage, CoverageImport coverageImport) {
      this.coveragePackage = coveragePackage;
      this.coverageImport = coverageImport;
   }

   public List<MergeItem> getMergeItems() {
      if (mergeItems == null) {
         mergeItems = new ArrayList<MergeItem>();
         for (ICoverage importCoverage : coverageImport.getChildren()) {
            processImportCoverage(importCoverage);
         }
      }
      if (mergeItems.size() == 0) {
         mergeItems.add(new MessageMergeItem("Nothing to Import"));
      }
      return mergeItems;
   }

   private void processImportCoverage(ICoverage importCoverage) {
      System.err.println("Merging check " + importCoverage);
      ICoverage packageCoverage = getPackageCoverageItem(importCoverage);
      // if no corresponding package coverage, add this and all children
      if (packageCoverage == null) {
         mergeItems.add(new MergeItem(MergeType.Add, null, importCoverage));
      } else {
         // process all children
         for (ICoverage childCoverage : importCoverage.getChildren()) {
            processImportCoverage(childCoverage);
         }
      }
      return;
   }

   /**
    * Recurse through coverage package to find importItem equivalent
    */
   public ICoverage getPackageCoverageItem(ICoverage importItem) {
      for (ICoverage childCoverage : coveragePackage.getChildren(false)) {
         ICoverage result = getPackageCoverageItem(childCoverage, importItem);
         if (result != null) return result;
      }
      return null;
   }

   /**
    * Recurse through package item and children to find importItem equivalent
    */
   public ICoverage getPackageCoverageItem(ICoverage packageItem, ICoverage importItem) {
      boolean equal = isConceptuallyEqual(packageItem, importItem);
      if (equal) return packageItem;
      // Only check children if importItem should be child of packageItem by namespace
      if (importItem.getNamespace().startsWith(packageItem.getNamespace())) {
         for (ICoverage childPackageItem : packageItem.getChildren(false)) {
            ICoverage result = getPackageCoverageItem(childPackageItem, importItem);
            if (result != null) return result;
         }
      }
      return null;
   }

   public static boolean isConceptuallyEqual(ICoverage packageItem, ICoverage importItem) {
      if (packageItem.equals(importItem)) return true;
      if (packageItem.getNamespace() == null && importItem.getNamespace() == null) return true;
      if (packageItem.getNamespace() == null) return false;
      if (importItem.getNamespace() == null) return false;
      if (!packageItem.getNamespace().equals(importItem.getNamespace())) return false;
      if (packageItem instanceof CoverageUnit && importItem instanceof CoverageUnit) {
         if (!((CoverageUnit) packageItem).getMethodNumber().equals(((CoverageUnit) importItem).getMethodNumber())) {
            return false;
         }
      }
      if (packageItem.getName().equals(importItem.getName())) {
         if (packageItem.getParent() instanceof CoveragePackage && importItem.getParent() instanceof CoverageImport) {
            return true;
         } else {
            if (isConceptuallyEqual(packageItem.getParent(), importItem.getParent())) {
               return true;
            } else {
               return false;
            }
         }
      }
      return false;
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

}
