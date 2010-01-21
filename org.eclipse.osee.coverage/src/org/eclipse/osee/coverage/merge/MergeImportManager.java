/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.ICoverageItemProvider;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class MergeImportManager {

   private final CoveragePackage coveragePackage;
   private final MergeManager mergeManager;

   public MergeImportManager(MergeManager mergeManager) {
      this.mergeManager = mergeManager;
      this.coveragePackage = mergeManager.getCoveragePackage();
   }

   public XResultData importItems(ISaveable saveable, Collection<IMergeItem> mergeItems) throws OseeCoreException {
      XResultData rd = new XResultData(false);
      if (!validateEditable(rd, saveable)) return rd;
      if (!validateMergeTypes(rd, mergeItems)) return rd;
      if (!validateChildren(rd, mergeItems)) return rd;

      for (IMergeItem mergeItem : mergeItems) {
         if (mergeItem.getMergeType() == MergeType.Add) {
            if (mergeItem instanceof MergeItem) {
               CoverageUnit coverageUnit = (CoverageUnit) ((MergeItem) mergeItem).getImportItem();
               importCoverageUnitItem(rd, coverageUnit);
               // add all children items
               for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits(true)) {
                  importCoverageUnitItem(rd, childCoverageUnit);
               }
            } else if (mergeItem instanceof MergeItemGroup) {
               MergeItemGroup group = (MergeItemGroup) mergeItem;
               for (IMergeItem childMergeItem : group.getMergeItems()) {
                  if (childMergeItem instanceof MergeItem && ((MergeItem) childMergeItem).getImportItem() instanceof CoverageItem) {
                     CoverageItem coverageItem = (CoverageItem) ((MergeItem) childMergeItem).getImportItem();
                     MatchItem parentMatchItem = mergeManager.getPackageCoverageItem(coverageItem.getParent());
                     ICoverage parentPackageItem = parentMatchItem == null ? null : parentMatchItem.getPackageItem();
                     CoverageUnit parentPackageCoverageUnit = (CoverageUnit) parentPackageItem;
                     coverageItem.copy(parentPackageCoverageUnit);
                  } else {
                     rd.logError(String.format("Unsupported type [%s] for merge type [%s] merge item [%s]",
                           childMergeItem.getClass().getSimpleName(), MergeType.Add.toString(), mergeItem));
                  }
               }
            } else {
               rd.logError(String.format("Unsupported type [%s] for merge type [%s] merge item [%s]",
                     mergeItem.getClass().getSimpleName(), MergeType.Add.toString(), mergeItem));
            }
         } else if (mergeItem.getMergeType() == MergeType.Add_With_Moves) {
            if (mergeItem instanceof MergeItemGroup) {
               MergeItemGroup group = (MergeItemGroup) mergeItem;
               for (IMergeItem childMergeItem : group.getMergeItems()) {
                  if (childMergeItem.getMergeType() == MergeType.Add && childMergeItem instanceof MergeItem) {
                     CoverageUnit coverageUnit = (CoverageUnit) ((MergeItem) childMergeItem).getImportItem();
                     importCoverageUnitItem(rd, coverageUnit);
                     // add all children items
                     for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits(true)) {
                        importCoverageUnitItem(rd, childCoverageUnit);
                     }
                  }
                  // For moved items, Change order of package item to match import item
                  else if (childMergeItem.getMergeType() == MergeType.Moved_Due_To_Add && childMergeItem instanceof MergeItem) {
                     updateOrder(mergeItem, childMergeItem, rd);
                  } else {
                     rd.logError(String.format("[%s] doesn't support merge item [%s] (2)",
                           mergeItem.getClass().getSimpleName(), childMergeItem.getMergeType(), mergeItem));
                  }
               }
            } else {
               rd.logError(String.format("Unsupported Add_With_Moves type [%s] for merge type [%s] merge item [%s]",
                     mergeItem.getClass().getSimpleName(), MergeType.Add_With_Moves.toString(), mergeItem));
            }
         } else if (mergeItem.getMergeType() == MergeType.Delete_And_Reorder) {
            if (mergeItem instanceof MergeItemGroup) {
               MergeItemGroup group = (MergeItemGroup) mergeItem;
               for (IMergeItem childMergeItem : group.getMergeItems()) {
                  if (childMergeItem.getMergeType() == MergeType.Delete && childMergeItem instanceof MergeItem) {
                     ICoverage packageCoverage = ((MergeItem) childMergeItem).getPackageItem();
                     ICoverage parentPackageCoverage = packageCoverage.getParent();
                     if (packageCoverage instanceof CoverageUnit) {
                        ((ICoverageUnitProvider) parentPackageCoverage).removeCoverageUnit((CoverageUnit) packageCoverage);
                     } else if (packageCoverage instanceof CoverageItem) {
                        ((ICoverageItemProvider) parentPackageCoverage).removeCoverageItem((CoverageItem) packageCoverage);
                     }
                  }
                  // For moved items, Change order of package item to match import item
                  else if (childMergeItem.getMergeType() == MergeType.Moved_Due_To_Delete && childMergeItem instanceof MergeItem) {
                     updateOrder(mergeItem, childMergeItem, rd);
                  } else {
                     rd.logError(String.format("[%s] doesn't support merge item [%s] (2)",
                           mergeItem.getClass().getSimpleName(), childMergeItem.getMergeType(), mergeItem));
                  }
               }
            } else {
               rd.logError(String.format(
                     "Unsupported Delete_And_Reorder type [%s] for merge type [%s] merge item [%s]",
                     mergeItem.getClass().getSimpleName(), MergeType.Add_With_Moves.toString(), mergeItem));
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

   private void updateOrder(IMergeItem mergeItem, IMergeItem childMergeItem, XResultData rd) throws OseeCoreException {
      ICoverage packageCoverage = ((MergeItem) childMergeItem).getPackageItem();
      ICoverage importCoverage = ((MergeItem) childMergeItem).getImportItem();
      if (packageCoverage instanceof CoverageUnit) {
         ((CoverageUnit) packageCoverage).setOrderNumber(((CoverageUnit) importCoverage).getOrderNumber());
      } else if (packageCoverage instanceof CoverageItem) {
         ((CoverageItem) packageCoverage).setOrderNumber(((CoverageItem) importCoverage).getOrderNumber());
      } else {
         rd.logError(String.format("[%s] doesn't support merge item [%s] (1)", mergeItem.getClass().getSimpleName(),
               MergeType.Add_With_Moves.toString(), mergeItem));
      }
      // Since order has changed and items added, update parent coverage unit's file contents
      if (packageCoverage.getParent() != null && packageCoverage.getParent() instanceof CoverageUnit) {
         CoverageUnit parentPackageCoverage = (CoverageUnit) packageCoverage.getParent();
         CoverageUnit parentImportCoverage = (CoverageUnit) importCoverage.getParent();
         if (!parentPackageCoverage.getFileContents().equals(parentImportCoverage.getFileContents())) {
            parentPackageCoverage.setFileContents(parentImportCoverage.getFileContents());
         }
      }
   }

   /**
    * Takes import items from coverageImport and applies them to coveragePackage
    */
   private void importCoverageUnitItem(XResultData rd, CoverageUnit importItem) {
      System.out.println("importItemsRecurse => " + importItem + " path " + CoverageUtil.getFullPath(importItem));
      try {
         rd.log("Processing " + importItem.getName());
         //         if (!(importItem instanceof CoverageUnit)) {
         //            rd.logError(String.format("[%s] invalid for Import; Only import CoverageUnits",
         //                  importItem.getClass().getSimpleName()));
         //         }
         CoverageUnit importCoverageUnit = (CoverageUnit) importItem;

         MatchItem matchItem = mergeManager.getPackageCoverageItem(importItem);
         ICoverage packageItem = matchItem == null ? null : matchItem.getPackageItem();
         // Determine if item already exists first
         if (MatchType.isMatch(matchItem.getMatchType())) {
            // save assignees and notes and RATIONALE before child overwrites
            // ((CoverageUnit) importItem).updateAssigneesAndNotes((CoverageUnit) packageItem);
            System.out.println("FOUND MATCH type " + matchItem.getMatchType());
            System.out.println("FOUND MATCH pack " + matchItem.getPackageItem() + " path " + CoverageUtil.getFullPath(matchItem.getPackageItem()));
            System.out.println("FOUND MATCH impt " + matchItem.getImportItem() + " path " + CoverageUtil.getFullPath(matchItem.getImportItem()));
         }
         // This is new item
         else if (MatchType.isNoMatch(matchItem.getMatchType())) {
            System.err.println("NEW ITEM " + matchItem.getMatchType());
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

               // Since item was added, update parent coverage unit's file contents if necessary
               parentPackageItem.setFileContents(parentImportItem.getFileContents());

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

   private boolean validateMergeTypes(XResultData rd, Collection<IMergeItem> mergeItems) throws OseeCoreException {
      boolean valid = true;
      for (IMergeItem mergeItem : mergeItems) {
         if (mergeItem.getMergeType().isError()) {
            rd.log(String.format("Can't merge item [%s] with error", mergeItem));
            valid = false;
         }
      }
      return valid;
   }

   public boolean validateChildren(XResultData rd, Collection<IMergeItem> mergeItems) throws OseeStateException {
      boolean valid = true;
      for (IMergeItem mergeItem : mergeItems) {
         if (mergeItem instanceof MergeItem && ((MergeItem) mergeItem).getImportItem() != null) {
            boolean isValid = validateChildrenAreUniqueRecurse(rd, ((MergeItem) mergeItem).getImportItem());
            if (!isValid) valid = false;
            isValid = validateChildrenFieldsRecurse(rd, ((MergeItem) mergeItem).getImportItem());
            if (!isValid) valid = false;

         } else if (mergeItem instanceof MergeItemGroup) {
            boolean isValid = validateChildren(rd, ((MergeItemGroup) mergeItem).getMergeItems());
            if (!isValid) valid = false;
         }
      }
      return valid;
   }

   private boolean validateChildrenAreUniqueRecurse(XResultData rd, ICoverage coverage) throws OseeStateException {
      boolean valid = true;
      for (ICoverage importItem1 : coverage.getChildren()) {
         for (ICoverage importItem2 : coverage.getChildren()) {

            MatchType matchType = MatchType.getMatchType(importItem1, importItem2);
            if ((matchType == MatchType.Match__Name_And_Order_Num) && importItem1 != importItem2) {
               rd.logError(String.format("CoverageUnit [%s] has two equal children [%s][%s]; Can't import.", coverage,
                     importItem1, importItem2));
               valid = false;
            }
         }
      }
      for (ICoverage childItem : coverage.getChildren()) {
         if (childItem instanceof CoverageUnit) {
            boolean isValid = validateChildrenAreUniqueRecurse(rd, (CoverageUnit) childItem);
            if (!isValid) valid = false;
         }
      }
      return valid;
   }

   private boolean validateChildrenFieldsRecurse(XResultData rd, ICoverage coverage) throws OseeStateException {
      boolean valid = true;
      for (ICoverage childCoverage : coverage.getChildren()) {
         if (!Strings.isValid(childCoverage.getName())) {
            rd.logError(String.format("ICoverage [%s] has no valid name.  path [%s]; Can't import.", childCoverage,
                  CoverageUtil.getFullPath(childCoverage)));
            valid = false;
         }
         if (!Strings.isValid(childCoverage.getNamespace())) {
            rd.logError(String.format("ICoverage [%s] has no valid namespace.  path [%s]; Can't import.",
                  childCoverage, CoverageUtil.getFullPath(childCoverage)));
            valid = false;
         }
         if (childCoverage instanceof CoverageItem && !Strings.isValid(childCoverage.getOrderNumber())) {
            rd.logError(String.format("ICoverage [%s] has no valid orderNumber.  path [%s]; Can't import.",
                  childCoverage, CoverageUtil.getFullPath(childCoverage)));
            valid = false;
         }
      }
      for (ICoverage childItem : coverage.getChildren()) {
         if (childItem instanceof CoverageUnit) {
            boolean isValid = validateChildrenFieldsRecurse(rd, (CoverageUnit) childItem);
            if (!isValid) valid = false;
         }
      }
      return valid;
   }

}
