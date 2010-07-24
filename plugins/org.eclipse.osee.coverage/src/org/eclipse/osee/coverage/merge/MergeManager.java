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
package org.eclipse.osee.coverage.merge;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class MergeManager {
   private final CoveragePackage coveragePackage;
   private final CoverageImport coverageImport;
   private Set<ICoverage> processedImportCoverages = new HashSet<ICoverage>(1000);

   public MergeManager(CoveragePackage coveragePackage, CoverageImport coverageImport) {
      this.coveragePackage = coveragePackage;
      this.coverageImport = coverageImport;
   }

   public List<IMergeItem> getMergeItems() throws OseeCoreException {
      List<IMergeItem> mergeItems = new ArrayList<IMergeItem>();
      processedImportCoverages.clear();
      for (ICoverage importCoverage : coverageImport.getChildren()) {
         processImportCoverage(importCoverage, mergeItems, null);
      }
      if (mergeItems.isEmpty()) {
         mergeItems.add(new MessageMergeItem("Nothing to Import"));
      }
      return mergeItems;
   }

   public XResultData getMergeDetails(ICoverage importCoverageItem, XResultData resultData) throws OseeCoreException {
      List<IMergeItem> mergeItems = new ArrayList<IMergeItem>();
      processImportCoverage(importCoverageItem, mergeItems, resultData);
      return resultData;
   }

   private void processImportCoverage(ICoverage importCoverage, List<IMergeItem> mergeItems, XResultData resultData) throws OseeCoreException {
      boolean debug = false;
      if (debug) {
         System.err.println("Merging check " + importCoverage);
      }
      if (resultData != null) {
         resultData.log("\n\nMerging check " + importCoverage);
      }

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
         boolean moreImportChildrenThanPackageChildren = importItemChildren.size() > packageItemChildren.size();
         boolean morePackageChildrenThanImportChildren = packageItemChildren.size() > importItemChildren.size();
         boolean sameNumberChildren = importItemChildren.size() == packageItemChildren.size();
         if (resultData != null) {
            resultData.log(String.format("Num Import Items: %d - Num Package Items: %d", importItemChildren.size(),
                  packageItemChildren.size()));
            resultData.log(AHTML.getLabelValueStr("moreImportChildrenThanPackageChildren",
                  String.valueOf(moreImportChildrenThanPackageChildren)));
            resultData.log(AHTML.getLabelValueStr("morePackageChildrenThanImportChildren",
                  String.valueOf(morePackageChildrenThanImportChildren)));
            resultData.log(AHTML.getLabelValueStr("sameNumberChildren", String.valueOf(sameNumberChildren)));

            resultData.log(AHTML.bold("Package Children:"));
            for (ICoverage coverage : packageItemChildren) {
               resultData.addRaw(AHTML.blockQuote(String.valueOf(coverage).replaceAll(" ", "&nbsp")));
            }
            resultData.log(AHTML.bold("Import Children:"));
            for (ICoverage coverage : importItemChildren) {
               resultData.addRaw(AHTML.blockQuote(String.valueOf(coverage).replaceAll(" ", "&nbsp")));
            }
         }

         if (debug) {
            if (importCoverage.getGuid().equals("AAte3i2bH3L1MvsFMqAA")) {
               System.out.println("here");
            }
            if (importCoverage.getName().equals("NavigationButton2.java")) {
               System.out.println("here");
            }
            if (importCoverage.getName().equals("getImage")) {
               System.out.println("here");
            }
         }

         // Determine match for all import item children
         for (ICoverage childCoverage : importItemChildren) {
            MatchItem childMatchItem = getPackageCoverageItemRecurse(packageICoverage, childCoverage);
            importItemToMatchItem.put(childCoverage, childMatchItem);
         }

         if (resultData != null) {
            resultData.log(AHTML.bold("Match Items:"));
            for (Entry<ICoverage, MatchItem> entry : importItemToMatchItem.entrySet()) {
               resultData.addRaw(AHTML.blockQuote(String.valueOf(entry.getValue()).replaceAll(" ", "&nbsp")));
            }
         }
         if (debug) {
            for (Entry<ICoverage, MatchItem> entry : importItemToMatchItem.entrySet()) {
               System.out.println(String.format("MatchItem[%s]", entry.getValue()));
            }
         }

         // Case: All children all CoverageItems
         // Action: process them separately
         if (CoverageUtil.isAllCoverageItems(importItemChildren)) {
            handleChildrenCoverageItems(mergeItems, packageItemChildren, importCoverage, importItemChildren,
                  importItemToMatchItem, resultData);
         }

         // Case: All match and package # children == import # children
         // Action: continue and check children's children
         else if (isAllMatchCase(sameNumberChildren, importItemToMatchItem)) {
            handleAllMatchCase(mergeItems, importItemChildren, resultData);
         }

         // Case: All same except package item has more; possible deletion, attempt to handle
         // Action: Attempt to determine deleted package item
         else if (isPackageItemDeleted(morePackageChildrenThanImportChildren)) {
            handlePackageItemDeleted(mergeItems, importCoverage, packageItemChildren, importItemChildren, resultData);
         }

         // Case: Import children all full match except Import has more that don't match, items added and moved
         // Action: Process as Add / Move
         else if (isImportItemsAddedOrMoved(moreImportChildrenThanPackageChildren, importItemToMatchItem)) {

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

            // Case: All match, some added, and none moved (name-only)
            // Action: just add new ones and process all children
            if (isOnlyAddNewOnes(nameOnlyImportToPackageCoverage, packageItemChildren, importItemToMatchItem)) {
               handleOnlyAddNewOnes(mergeItems, importCoverage, importItemChildren, importItemToMatchItem, resultData);
            }

            // Case: All match, some added, and some moved
            // Action: Process children of Matches; Add ones that were not Name-Only; Move ones that were
            else {
               handleSomeAddedSomeMoved(mergeItems, nameOnlyImportToPackageCoverage, packageItemChildren,
                     importCoverage, importItemChildren, importItemToMatchItem, resultData);
            }

         }
         // Case: Unhandled
         // Action: Mark as UnMergeable
         else {
            mergeItems.add(new MergeItem(MergeType.Error__UnMergable, null, importCoverage, false));
         }
      }
      return;
   }

   private Entry<List<ICoverage>, List<ICoverage>> getMatchedAndUnMatchedImportCoverageItems(Map<ICoverage, MatchItem> importItemToMatchItem) throws OseeCoreException {
      List<ICoverage> unMatchedCoverageItems = new ArrayList<ICoverage>();
      List<ICoverage> matchedCoverageItems = new ArrayList<ICoverage>();
      for (Entry<ICoverage, MatchItem> coverageToMatchItem : importItemToMatchItem.entrySet()) {
         MatchItem childMatchItem = coverageToMatchItem.getValue();
         ICoverage childICoverage = coverageToMatchItem.getKey();
         if (!childMatchItem.isMatch()) {
            unMatchedCoverageItems.add(childICoverage);
         } else {
            matchedCoverageItems.add(childICoverage);
         }
      }
      return new SimpleEntry<List<ICoverage>, List<ICoverage>>(matchedCoverageItems, unMatchedCoverageItems);
   }

   private List<ICoverage> getMatchedPackageCoverageItems(Map<ICoverage, MatchItem> importItemToMatchItem) throws OseeCoreException {
      List<ICoverage> matchedPackageCoverageItems = new ArrayList<ICoverage>();
      for (Entry<ICoverage, MatchItem> coverageToMatchItem : importItemToMatchItem.entrySet()) {
         MatchItem childMatchItem = coverageToMatchItem.getValue();
         if (childMatchItem.isMatch()) {
            matchedPackageCoverageItems.add(childMatchItem.getPackageItem());
         }
      }
      return matchedPackageCoverageItems;
   }

   private void handleChildrenCoverageItems(List<IMergeItem> mergeItems, Collection<? extends ICoverage> packageItemChildren, ICoverage importCoverage, Collection<? extends ICoverage> importItemChildren, Map<ICoverage, MatchItem> importItemToMatchItem, XResultData resultData) throws OseeCoreException {
      List<IMergeItem> groupMergeItems = new ArrayList<IMergeItem>();
      boolean unMergeableExists = false;
      Entry<List<ICoverage>, List<ICoverage>> matchedUnMatchedEntry =
            getMatchedAndUnMatchedImportCoverageItems(importItemToMatchItem);
      // Get all Import CoverageItems that do not match package CoverageItems
      List<ICoverage> unMatchedImportCoverageItems = matchedUnMatchedEntry.getValue();
      // Get all Import CoverageItems that DO match package CoverageItems
      List<ICoverage> matchedImportCoverageItems = matchedUnMatchedEntry.getKey();
      // List package coverageItems that have been processed; list should be empty at end
      List<ICoverage> packageItemsProcessed = getMatchedPackageCoverageItems(importItemToMatchItem);

      for (ICoverage childICoverage : new CopyOnWriteArrayList<ICoverage>(unMatchedImportCoverageItems)) {
         MatchItem childMatchItem = importItemToMatchItem.get(childICoverage);
         if (childMatchItem.isMatch()) {
            throw new OseeStateException("unMatchedCoverageItems shouldn't contain matched items");
         }

         // Check for rename
         ICoverage packageMatch = isCoverageItemRenamed(packageItemChildren, childICoverage);
         if (packageMatch != null) {
            groupMergeItems.add(new MergeItem(MergeType.CI_Renamed, packageMatch, childICoverage, false));
            packageItemsProcessed.add(packageMatch);
            unMatchedImportCoverageItems.remove(childICoverage);
         }

         // Check for an add
         else if (isCoverageItemAdded(packageItemChildren, childICoverage)) {
            groupMergeItems.add(new MergeItem(MergeType.CI_Add, null, childICoverage, false));
            unMatchedImportCoverageItems.remove(childICoverage);
         }
      }
      for (ICoverage childICoverage : new CopyOnWriteArrayList<ICoverage>(matchedImportCoverageItems)) {
         MatchItem childMatchItem = importItemToMatchItem.get(childICoverage);

         // Check for method change
         if (childMatchItem != null && isCoverageItemMethodUpdate(childMatchItem)) {
            groupMergeItems.add(new MergeItem(MergeType.CI_Method_Update, childMatchItem.getPackageItem(),
                  childICoverage, false));
            unMatchedImportCoverageItems.remove(childICoverage);
         }
      }

      // Check for moves in any items left unhandled by above renames and adds
      for (ICoverage childICoverage : new CopyOnWriteArrayList<ICoverage>(unMatchedImportCoverageItems)) {
         for (ICoverage packageItemChild : packageItemChildren) {
            // name equals package item not yet processed
            if (!packageItemsProcessed.contains(packageItemChild) && packageItemChild.getName().equals(
                  childICoverage.getName())) {
               groupMergeItems.add(new MergeItem(MergeType.CI_Moved, packageItemChild, childICoverage, false));
               packageItemsProcessed.add(packageItemChild);
               unMatchedImportCoverageItems.add(childICoverage);
            }
         }
      }

      // Check for deletions
      for (ICoverage packageItemChild : packageItemChildren) {
         if (!packageItemsProcessed.contains(packageItemChild)) {
            groupMergeItems.add(new MergeItem(MergeType.CI_Delete, packageItemChild, null, false));
            packageItemsProcessed.add(packageItemChild);
         }
      }

      // Error on any remaining import CoverageItems
      for (ICoverage coverage : unMatchedImportCoverageItems) {
         groupMergeItems.add(new MergeItem(MergeType.Error__UnMergable, null, coverage, false));
      }
      // Error on any un-handled package CoverageItems
      for (ICoverage packageItemChild : packageItemChildren) {
         if (!packageItemsProcessed.contains(packageItemChild)) {
            groupMergeItems.add(new MergeItem(MergeType.Error__UnMergable, packageItemChild, null, false));
         }
      }

      if (groupMergeItems.size() > 0) {
         mergeItems.add(new MergeItemGroup(MergeType.CI_Changes, importCoverage, groupMergeItems, !unMergeableExists));
      }

   }

   private boolean isCoverageItemMethodUpdate(MatchItem childMatchItem) {
      ICoverage importItem = childMatchItem.getImportItem();
      ICoverage packageItem = childMatchItem.getPackageItem();
      // Only valid for coverage items
      if (!(importItem instanceof CoverageItem)) return false;
      if (!((CoverageItem) importItem).getCoverageMethod().equals(((CoverageItem) packageItem).getCoverageMethod())) {
         return true;
      }
      return false;
   }

   private boolean isCoverageItemAdded(Collection<? extends ICoverage> packageItemChildren, ICoverage importItemChild) {
      // Only valid for coverage items
      if (!(importItemChild instanceof CoverageItem)) return false;
      // Make sure there is no package item with same order number
      for (ICoverage packageItemChild : packageItemChildren) {
         if (packageItemChild.getOrderNumber().equals(importItemChild.getOrderNumber())) {
            return false;
         }
      }
      return true;
   }

   private ICoverage isCoverageItemRenamed(Collection<? extends ICoverage> packageItemChildren, ICoverage importItemChild) {
      // Only valid for coverage items
      if (!(importItemChild instanceof CoverageItem)) return null;
      // Make sure there is a package item with same order number
      ICoverage packageItemChild =
            CoverageUtil.getCoverageItemMatchingOrder(packageItemChildren, (CoverageItem) importItemChild);
      return packageItemChild;
   }

   private void handleSomeAddedSomeMoved(List<IMergeItem> mergeItems, Map<ICoverage, ICoverage> nameOnlyImportToPackageCoverage, Collection<? extends ICoverage> packageItemChildren, ICoverage importCoverage, Collection<? extends ICoverage> importItemChildren, Map<ICoverage, MatchItem> importItemToMatchItem, XResultData resultData) throws OseeCoreException {
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
                  nameOnlyImportToPackageCoverage.get(childMatchItem.getImportItem()), childMatchItem.getImportItem(),
                  false));
            processedImportCoverages.add(childMatchItem.getImportItem());
            processChildrenItems.add(childCoverage);
         }
         // This child is new, mark as added; no need to process children cause their new
         else {
            groupMergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem(), false));
            processedImportCoverages.add(childMatchItem.getImportItem());
         }
      }
      mergeItems.add(new MergeItemGroup(MergeType.Add_With_Moves, importCoverage, groupMergeItems, true));
      // Process children that should be processed
      for (ICoverage childCoverage : processChildrenItems) {
         processImportCoverage(childCoverage, mergeItems, resultData);
      }

   }

   private boolean isOnlyAddNewOnes(Map<ICoverage, ICoverage> nameOnlyImportToPackageCoverage, Collection<? extends ICoverage> packageItemChildren, Map<ICoverage, MatchItem> importItemToMatchItem) {
      // If no items match name-only then potential add
      if (nameOnlyImportToPackageCoverage.size() != 0) return false;
      // If any of the No_Match import order number match package number, than not an addOnly
      for (Entry<ICoverage, MatchItem> pair : importItemToMatchItem.entrySet()) {
         ICoverage importChild = pair.getKey();
         if (!Strings.isValid(importChild.getOrderNumber())) continue;
         if (pair.getValue().getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
            for (ICoverage packageItem : packageItemChildren) {
               if (packageItem.getOrderNumber().equals(importChild.getOrderNumber())) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   /**
    * Only add new ones to end and process all children
    */
   private void handleOnlyAddNewOnes(List<IMergeItem> mergeItems, ICoverage importCoverage, Collection<? extends ICoverage> importItemChildren, Map<ICoverage, MatchItem> importItemToMatchItem, XResultData resultData) throws OseeCoreException {
      List<IMergeItem> groupMergeItems = new ArrayList<IMergeItem>();
      for (ICoverage childCoverage : importItemChildren) {
         MatchItem childMatchItem = importItemToMatchItem.get(childCoverage);
         // This child matches, just process children
         if (childMatchItem.getMatchType() == MatchType.Match__Name_And_Order_Num) {
            processImportCoverage(childCoverage, mergeItems, resultData);
         }
         // This child is new, mark as added; no need to process children cause their new
         if (childMatchItem.getMatchType() == MatchType.No_Match__Name_Or_Order_Num) {
            if (childMatchItem.getImportItem() instanceof CoverageItem) {
               groupMergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem(), false));
            } else {
               mergeItems.add(new MergeItem(MergeType.Add, null, childMatchItem.getImportItem(), true));
            }
            processedImportCoverages.add(childMatchItem.getImportItem());
         }
      }
      if (groupMergeItems.size() > 0) {
         mergeItems.add(new MergeItemGroup(MergeType.Add, importCoverage, groupMergeItems, true));
      }

   }

   /**
    * If import has more all existing match and all new ones don't have same method number
    */
   private boolean isImportItemsAddedOrMoved(boolean moreImportChildrenThanPackageChildren, Map<ICoverage, MatchItem> importItemToMatchItem) {
      boolean result =
            moreImportChildrenThanPackageChildren && MatchItem.isAllMatchType(Arrays.asList(
                  MatchType.Match__Name_And_Order_Num, MatchType.No_Match__Name_Or_Order_Num, MatchType.Match__Folder),
                  importItemToMatchItem.values());
      return result;
   }

   /**
    * All same except package item has more; possible deletion, attempt to handle
    */
   private boolean isPackageItemDeleted(boolean morePackageChildrenThanImportChildren) {
      return morePackageChildrenThanImportChildren;
   }

   /**
    * Attempt to determine deleted package item
    */
   private void handlePackageItemDeleted(List<IMergeItem> mergeItems, ICoverage importCoverage, Collection<? extends ICoverage> packageItemChildren, Collection<? extends ICoverage> importItemChildren, XResultData resultData) {
      // If all import match package items, but there are more package items, delete package items
      List<IMergeItem> groupMergeItems = new ArrayList<IMergeItem>();
      boolean unmergeable = false;
      // Discover which packageItems don't have matches
      for (ICoverage packageItem : packageItemChildren) {
         Collection<ICoverage> matches = getNameMatchItems(packageItem, importItemChildren);
         // If matches > 1 can't perform merge
         if (matches.size() > 1) {
            // Case Else - unhandled cases
            unmergeable = true;
            mergeItems.add(new MergeItem(MergeType.Error__UnMergable, null, importCoverage, false));
            break;
         }
         // If matches == 0, this is a deletion
         else if (matches.isEmpty()) {
            groupMergeItems.add(new MergeItem(MergeType.Delete, packageItem, null, false));
         }
         // Else matches == 1, check the order; if different order, this is a Move_Due_To_Delete
         else {
            if (!packageItem.getOrderNumber().equals(matches.iterator().next().getOrderNumber())) {
               groupMergeItems.add(new MergeItem(MergeType.Moved_Due_To_Delete, packageItem, matches.iterator().next(),
                     false));
            }
         }
      }
      if (!unmergeable) {
         mergeItems.add(new MergeItemGroup(MergeType.Delete_And_Reorder, importCoverage, groupMergeItems, true));
      }
   }

   /**
    * All match and package # children == import # children<br>
    * continue and check children's children
    */
   private boolean isAllMatchCase(boolean sameNumberChildren, Map<ICoverage, MatchItem> importItemToMatchItem) {
      return (sameNumberChildren && MatchItem.isAllMatchType(MatchType.FullMatches, importItemToMatchItem.values()));
   }

   /**
    * Continue and check children's children
    */
   private void handleAllMatchCase(List<IMergeItem> mergeItems, Collection<? extends ICoverage> importItemChildren, XResultData resultData) throws OseeCoreException {
      // process all children
      for (ICoverage childCoverage : importItemChildren) {
         processImportCoverage(childCoverage, mergeItems, resultData);
      }
   }

   private List<ICoverage> getNameMatchItems(ICoverage coverage, Collection<? extends ICoverage> toMatch) {
      List<ICoverage> matches = new ArrayList<ICoverage>();
      for (ICoverage item : toMatch) {
         if (item.getName().equals(coverage.getName())) {
            matches.add(item);
         }
      }
      return matches;
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
