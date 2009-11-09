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
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoveragePackageImportManager;

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
      ICoverage packageCoverage =
            CoveragePackageImportManager.getPackageCoverageItem(coveragePackage, importCoverage, true);
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

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

}
