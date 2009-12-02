/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeStateException;

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

   public List<MergeItem> getMergeItems() throws OseeStateException {
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

   private void processImportCoverage(ICoverage importCoverage) throws OseeStateException {
      System.err.println("Merging check " + importCoverage);
      MatchItem matchItem = getPackageCoverageItem(importCoverage);
      // No matching coverage package item, Add this and all children
      if (MatchType.isNoMatch(matchItem.getMatchType())) {
         mergeItems.add(new MergeItem(MergeType.Add, null, importCoverage));
      }
      // Import item matched Package item, check children
      else {
         ICoverage packageICoverage = matchItem.getPackageItem();
         ICoverage importICoverage = matchItem.getImportItem();

         Collection<? extends ICoverage> packageItemChildren = packageICoverage.getChildren();
         Collection<? extends ICoverage> importItemChildren = importICoverage.getChildren();
         Map<ICoverage, MatchItem> importItemToMatchItem = new HashMap<ICoverage, MatchItem>(10);

         // Determine match for all import item children
         for (ICoverage childCoverage : importItemChildren) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(packageICoverage, childCoverage);
            importItemToMatchItem.put(childCoverage, childMatchItem);
         }

         // Case 1 - All match and package # children == import # children; continue and check children's children
         if (packageItemChildren.size() == importItemChildren.size() && MatchItem.isAllMatchType(
               Collections.singleton(MatchType.Match__Name_And_Method), importItemToMatchItem.values())) {
            // process all children
            for (ICoverage childCoverage : importItemChildren) {
               processImportCoverage(childCoverage);
            }
         }

         // Case 2 - Import children all full match except Import has more that don't match, items added to end
         else if (MatchItem.isAllMatchType(Arrays.asList(MatchType.Match__Name_And_Method,
               MatchType.No_Match__Name_Or_Method_Num), importItemToMatchItem.values()) && importItemChildren.size() > packageItemChildren.size()) {
            for (ICoverage childCoverage : importItemChildren) {
               MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
               // This child matches, just process children
               if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Method) {
                  processImportCoverage(childCoverage);
               }
               // This child is new, mark as added; no need to process children cause their new
               if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Method_Num) {
                  mergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem()));
               }
            }
         }

         // Case 3 - Import children all full or partial match except import has more; item added/items moved

         // Else, just process children
         else {
            for (ICoverage childCoverage : importItemChildren) {
               MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
               // This child matches, just process children
               if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Method) {
                  processImportCoverage(childCoverage);
               }
            }
         }
      }
      return;
   }

   public MatchItem getPackageCoverageItem(ICoverage importItem) throws OseeStateException {
      return getPackageCoverageItem(coveragePackage, importItem);
   }

   /**
    * Recurse through coverage package to find importItem equivalent
    */
   public static MatchItem getPackageCoverageItem(CoveragePackageBase coveragePackageBase, ICoverage importItem) throws OseeStateException {
      for (ICoverage childCoverage : coveragePackageBase.getChildren(false)) {
         MatchItem matchItem = getPackageCoverageItemRecurse(childCoverage, importItem);
         if (matchItem.isMatchType(Arrays.asList(MatchType.Match__Name_And_Method, MatchType.Match__Name_Only))) {
            return matchItem;
         }
      }
      return new MatchItem(MatchType.No_Match__Name_Or_Method_Num, null, importItem);
   }

   /**
    * Recurse through package item and children to find importItem equivalent
    */
   public static MatchItem getPackageCoverageItemRecurse(ICoverage packageItem, ICoverage importItem) throws OseeStateException {
      MatchType matchType = isConceptuallyEqual(packageItem, importItem);
      if (matchType == MatchType.Match__Coverage_Base || matchType == MatchType.Match__Name_And_Method) {
         return new MatchItem(matchType, packageItem, importItem);
      }
      // Only check children if importItem should be child of packageItem by namespace
      if (importItem.getNamespace().startsWith(packageItem.getNamespace())) {
         for (ICoverage childPackageItem : packageItem.getChildren(false)) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(childPackageItem, importItem);
            if (childMatchItem != null && (childMatchItem.getMatchType() == MatchType.Match__Coverage_Base || childMatchItem.getMatchType() == MatchType.Match__Name_And_Method)) {
               return childMatchItem;
            }
         }
      }
      return new MatchItem(MatchType.No_Match__Name_Or_Method_Num, null, importItem);
   }

   public static MatchType isConceptuallyEqual(ICoverage packageItem, ICoverage importItem) throws OseeStateException {
      if (packageItem instanceof CoveragePackage && importItem instanceof CoverageImport) {
         return MatchType.Match__Coverage_Base;
      }
      if (packageItem.getNamespace() == null || importItem.getNamespace() == null) throw new OseeStateException(
            "Namespaces can't be null");
      if (!packageItem.getNamespace().equals(importItem.getNamespace())) return MatchType.No_Match__Namespace;
      if (packageItem instanceof CoverageUnit && importItem instanceof CoverageUnit) {
         if (!((CoverageUnit) packageItem).getMethodNumber().equals(((CoverageUnit) importItem).getMethodNumber())) {
            if (packageItem.getName().equals(importItem.getName())) {
               return MatchType.Match__Name_Only;
            } else {
               return MatchType.No_Match__Name_Or_Method_Num;
            }
         }
      }
      if (packageItem.getName().equals(importItem.getName())) {
         if (packageItem.getParent() instanceof CoveragePackage && importItem.getParent() instanceof CoverageImport) {
            return MatchType.Match__Name_And_Method;
         } else {
            if (packageItem.getParent() == null && importItem.getParent() == null) {
               return MatchType.Match__Coverage_Base;
            }
            MatchType matchType = isConceptuallyEqual(packageItem.getParent(), importItem.getParent());
            return matchType;
         }
      }
      return MatchType.No_Match__Name_Or_Method_Num;
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

}
