/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class MergeManager {
   private final CoveragePackage coveragePackage;
   private final CoverageImport coverageImport;
   private List<IMergeItem> mergeItems = null;
   private Set<ICoverage> processedImportCoverages = new HashSet<ICoverage>(1000);

   public MergeManager(CoveragePackage coveragePackage, CoverageImport coverageImport) {
      this.coveragePackage = coveragePackage;
      this.coverageImport = coverageImport;
   }

   public List<IMergeItem> getMergeItems() throws OseeStateException {
      if (mergeItems == null) {
         mergeItems = new ArrayList<IMergeItem>();
         processedImportCoverages.clear();
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
         if (!processedImportCoverages.contains(importCoverage)) {
            mergeItems.add(new MergeItem(MergeType.Add, null, importCoverage, true));
         }
      }
      // Import item matched Package item, check children
      else {
         ICoverage packageICoverage = matchItem.getPackageItem();
         ICoverage importICoverage = matchItem.getImportItem();

         Collection<? extends ICoverage> packageItemChildren = packageICoverage.getChildren();
         Collection<? extends ICoverage> importItemChildren = importICoverage.getChildren();
         Map<ICoverage, MatchItem> importItemToMatchItem = new HashMap<ICoverage, MatchItem>(10);

         if (importCoverage.getName().startsWith("AuxPowerUnit2")) {
            System.out.println("here");
         }

         // Determine match for all import item children
         for (ICoverage childCoverage : importItemChildren) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(packageICoverage, childCoverage);
            importItemToMatchItem.put(childCoverage, childMatchItem);
         }

         // Case A - All match and package # children == import # children
         // Action: continue and check children's children
         if (packageItemChildren.size() == importItemChildren.size() && MatchItem.isAllMatchType(MatchType.FullMatches,
               importItemToMatchItem.values())) {
            // process all children
            for (ICoverage childCoverage : importItemChildren) {
               processImportCoverage(childCoverage);
            }
         }

         // Addition/Move - Import children all full match except Import has more that don't match, items added and moved
         else if (MatchItem.isAllMatchType(Arrays.asList(MatchType.Match__Name_And_Order_Num,
               MatchType.No_Match__Name_Or_Order_Num), importItemToMatchItem.values()) && importItemChildren.size() > packageItemChildren.size()) {

            // Determine number of No_Match items that are name-only match
            Map<ICoverage, ICoverage> nameOnlyImportToPackageCoverage = new HashMap<ICoverage, ICoverage>();
            for (Entry<ICoverage, MatchItem> pair : importItemToMatchItem.entrySet()) {
               ICoverage importChild = pair.getKey();
               if (pair.getValue().getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
                  // Try to find child that matches name
                  for (ICoverage packageChild : packageItemChildren) {
                     if (packageChild.getName().equals(importChild.getName())) {
                        nameOnlyImportToPackageCoverage.put(importChild, packageChild);
                     }
                  }
               }
            }

            // Case B - All match, some added, and none moved (name-only)
            // Action: just add new ones and process all children
            if (nameOnlyImportToPackageCoverage.size() == 0) {
               for (ICoverage childCoverage : importItemChildren) {
                  MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
                  // This child matches, just process children
                  if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Order_Num) {
                     processImportCoverage(childCoverage);
                  }
                  // This child is new, mark as added; no need to process children cause their new
                  if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
                     mergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem(), true));
                     processedImportCoverages.add(childMatchItem.getImportItem());
                  }
               }
            }

            // Case C - All match, some added, and some moved
            // Action: Process children of Matches; Add ones that were not Name-Only; Move ones that were
            else {
               List<IMergeItem> groupMergeItems = new ArrayList<IMergeItem>();
               List<ICoverage> processChildrenItems = new ArrayList<ICoverage>();
               for (ICoverage childCoverage : importItemChildren) {
                  MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
                  // This child matches fully, just process children
                  if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Order_Num) {
                     processChildrenItems.add(childCoverage);
                  }
                  // This child is moved, mark as modified; process children cause they existed before
                  else if (nameOnlyImportToPackageCoverage.keySet().contains(childCoverage)) {
                     groupMergeItems.add(new MergeItem(MergeType.Moved_Due_To_Add,
                           nameOnlyImportToPackageCoverage.get(childMatchItem.getImportItem()),
                           childMatchItem.getImportItem(), false));
                     processedImportCoverages.add(childMatchItem.getImportItem());
                     processChildrenItems.add(childCoverage);
                  }
                  // This child is new, mark as added; no need to process children cause their new
                  else {
                     groupMergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem(), false));
                     processedImportCoverages.add(childMatchItem.getImportItem());
                  }
               }
               mergeItems.add(new MergeItemGroup(MergeType.Add_With_Moves, groupMergeItems, true));
               // Process children that should be processed
               for (ICoverage childCoverage : processChildrenItems) {
                  processImportCoverage(childCoverage);
               }
            }
         }

         // Case Else - unhandled case
         else {
            mergeItems.add(new MergeItem(MergeType.Error__UnMergable, null, importCoverage, false));
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
         if (matchItem.isMatch()) {
            return matchItem;
         }
      }
      return new MatchItem(MatchType.No_Match__Name_Or_Order_Num, null, importItem);
   }

   /**
    * Recurse through package item and children to find importItem equivalent
    */
   public static MatchItem getPackageCoverageItemRecurse(ICoverage packageItem, ICoverage importItem) throws OseeStateException {
      MatchType matchType = MatchType.getMatchType(packageItem, importItem);
      if (MatchType.isMatch(matchType)) {
         return new MatchItem(matchType, packageItem, importItem);
      }
      // Only check children if importItem should be child of packageItem by namespace
      if (importItem.getNamespace().startsWith(packageItem.getNamespace())) {
         for (ICoverage childPackageItem : packageItem.getChildren(false)) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(childPackageItem, importItem);
            if (childMatchItem != null && (MatchType.isMatch(childMatchItem.getMatchType()))) {
               return childMatchItem;
            }
         }
      }
      return new MatchItem(MatchType.No_Match__Name_Or_Order_Num, null, importItem);
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

}
