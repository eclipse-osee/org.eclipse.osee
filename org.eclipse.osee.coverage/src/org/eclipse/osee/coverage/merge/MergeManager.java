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
import java.util.List;
import java.util.Map;
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

   public MergeManager(CoveragePackage coveragePackage, CoverageImport coverageImport) {
      this.coveragePackage = coveragePackage;
      this.coverageImport = coverageImport;
   }

   public List<IMergeItem> getMergeItems() throws OseeStateException {
      if (mergeItems == null) {
         mergeItems = new ArrayList<IMergeItem>();
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

         if (importCoverage.getName().startsWith("AuxPowerUnit2")) {
            System.out.println("here");
         }

         // Determine match for all import item children
         for (ICoverage childCoverage : importItemChildren) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(packageICoverage, childCoverage);
            importItemToMatchItem.put(childCoverage, childMatchItem);
         }

         // Case A - All match and package # children == import # children; continue and check children's children
         if (packageItemChildren.size() == importItemChildren.size() && MatchItem.isAllMatchType(MatchType.FullMatches,
               importItemToMatchItem.values())) {
            // process all children
            for (ICoverage childCoverage : importItemChildren) {
               processImportCoverage(childCoverage);
            }
         }

         // Case B - Addition - Import children all full match except Import has more that don't match, items added to end
         else if (MatchItem.isAllMatchType(Arrays.asList(MatchType.Match__Name_And_Order_Num,
               MatchType.No_Match__Name_Or_Order_Num), importItemToMatchItem.values()) && importItemChildren.size() > packageItemChildren.size()) {
            for (ICoverage childCoverage : importItemChildren) {
               MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
               // This child matches, just process children
               if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Order_Num) {
                  processImportCoverage(childCoverage);
               }
               // This child is new, mark as added; no need to process children cause their new
               if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
                  mergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem()));
               }
            }
         }

         // TODO This won't work cause add/move are all Match and No_Match since took out
         // name only match.  Merge this case into Case B above
         // Case C - Addition/Move - Import children all full or partial match except import has more; item added/items moved
         else if (MatchItem.isAllMatchType(MatchType.FullMatches, importItemToMatchItem.values()) && importItemChildren.size() > packageItemChildren.size()) {
            List<IMergeItem> groupMergeItems = new ArrayList<IMergeItem>();
            for (ICoverage childCoverage : importItemChildren) {
               MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
               // This child matches fully, just process children
               if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Order_Num) {
                  processImportCoverage(childCoverage);
               }
               // This child is new, mark as added; no need to process children cause their new
               if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
                  groupMergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem()));
               }
               // This child is moved, mark as modified; process children cause they existed before
               if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
                  groupMergeItems.add(new MergeItem(MergeType.Moved_Due_To_Add, childMatchItem.getPackageItem(),
                        childMatchItem.getImportItem()));
                  processImportCoverage(childCoverage);
               }
            }
            mergeItems.add(new MergeItemGroup(MergeType.Add_With_Moves, groupMergeItems));
         }

         // Case Else - unhandled case
         else {
            mergeItems.add(new MergeItem(MergeType.Error__UnMergable, null, importCoverage));
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
