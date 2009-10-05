/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackageImport {

   private final CoveragePackage coveragePackage;
   private final CoverageImport coverageImport;

   public CoveragePackageImport(CoveragePackage coveragePackage, CoverageImport coverageImport) {
      this.coveragePackage = coveragePackage;
      this.coverageImport = coverageImport;
   }

   /**
    * Takes import items from coverageImport and applies them to coveragePackage
    */
   public XResultData importItems(Collection<ICoverageEditorItem> importItems) {
      XResultData rd = new XResultData();
      for (ICoverageEditorItem importItem : importItems) {
         rd.log("Processing " + importItem.getName());
         if (!(importItem instanceof CoverageUnit)) {
            rd.logError(String.format("Invalid Item for Import; Don't import [%s]",
                  importItem.getClass().getSimpleName()));
            continue;
         }
         CoverageUnit importCoverageUnit = (CoverageUnit) importItem;

         ICoverageEditorItem packageItem = getPackageCoverageItem(importItem);
         // Determine if item already exists first
         if (packageItem != null) {
            rd.logError("Import Item matches Package Item - Not Implemented Yet");
            continue;
         }
         // This is new item

         // Check if parent item exists
         ICoverageEditorItem parentImportItem = importItem.getParent();
         // If null, this is top level item, just add to package
         if (parentImportItem == null) {
            coveragePackage.addCoverageUnit((CoverageUnit) importItem);
            rd.log(String.format("Added [%s] as top level CoverageUnit", importCoverageUnit));
            continue;
         }
         if (!(parentImportItem instanceof CoverageUnit)) {
            rd.logError(String.format("ParentImportItem not CoverageUnit - Unexpected type [%s]",
                  parentImportItem.getClass().getSimpleName()));
            continue;
         }
         // Else, want to add item to same parent
         CoverageUnit parentCoverageUnit = (CoverageUnit) importItem.getParent();
         parentCoverageUnit.addCoverageUnit(importCoverageUnit);
         rd.log(String.format("Added [%s] to parent [%s]", importCoverageUnit, parentCoverageUnit));
         rd.log("");
      }
      return rd;
   }

   public ICoverageEditorItem getPackageCoverageItem(ICoverageEditorItem importItem) {
      for (ICoverageEditorItem packageItem : coveragePackage.getCoverageEditorItems()) {
         if (isConceptuallyEqual(packageItem, importItem)) {
            return packageItem;
         }
      }
      return null;
   }

   public boolean isConceptuallyEqual(ICoverageEditorItem item1, ICoverageEditorItem item2) {
      if (item1.equals(item2)) return true;
      if (item1.getClass() != item2.getClass()) return false;
      if (item1.getName().equals(item2.getName())) return true;
      return false;
   }
}
