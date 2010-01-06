/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import org.eclipse.osee.coverage.merge.MatchType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class MatchTypeTest {

   public static ICoverageUnitFileContentsProvider fileContentsProvider = new SimpleCoverageUnitFileContentsProvider();

   public CoveragePackage getCoveragePackage() {
      CoveragePackage coveragePackage = new CoveragePackage("Package", CoverageOptionManagerDefault.instance());
      CoverageUnit packageCu = new CoverageUnit(coveragePackage, "Top", "C:/UserData/", fileContentsProvider);
      packageCu.setNamespace("org.this");
      CoverageItem packageCi = new CoverageItem(packageCu, CoverageOptionManager.Deactivated_Code, "1");
      packageCi.setName("this is the text");
      CoverageUnit packageFolderCu = new CoverageUnit(coveragePackage, "folder", "", fileContentsProvider);
      packageFolderCu.setFolder(true);
      return coveragePackage;
   }

   public CoverageImport getCoverageImport() {
      CoverageImport coverageImport = new CoverageImport("Import");
      CoverageUnit importCu = new CoverageUnit(coverageImport, "Top", "C:/UserData/", fileContentsProvider);
      importCu.setNamespace("org.this");
      CoverageItem importCi = new CoverageItem(importCu, CoverageOptionManager.Deactivated_Code, "1");
      importCi.setName("this is the text");
      CoverageUnit importFolderCu = new CoverageUnit(coverageImport, "folder", "", fileContentsProvider);
      importFolderCu.setFolder(true);
      return coverageImport;
   }

   public CoverageUnit getCoverageUnit(CoveragePackageBase coveragePackageBase, boolean folder) {
      for (CoverageUnit coverageUnit : coveragePackageBase.getCoverageUnits()) {
         if (coverageUnit.isFolder() == folder) return coverageUnit;
      }
      return null;
   }

   public CoverageItem getCoverageItem(CoverageUnit coverageUnit, String orderNum) {
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
         if (coverageItem.getOrderNumber().equals(orderNum)) return coverageItem;
      }
      return null;
   }

   @Test
   public void testIsConceptuallyEqual() throws OseeCoreException {

      CoveragePackage coveragePackage = getCoveragePackage();
      CoverageImport coverageImport = getCoverageImport();

      // CoveragePackage and CoverageImport should return match
      Assert.assertEquals(MatchType.Match__Coverage_Base, MatchType.getMatchType(coveragePackage, coverageImport));

      CoverageUnit packageCoverageUnit = getCoverageUnit(coveragePackage, false);

      CoverageUnit importCoverageUnit = getCoverageUnit(coverageImport, false);
      CoverageItem importCoverageItem = getCoverageItem(importCoverageUnit, "1");

      // Test Coverage Units
      Assert.assertEquals(MatchType.No_Match__Class, MatchType.getMatchType(packageCoverageUnit, importCoverageItem));

      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageUnit,
            importCoverageUnit));

      packageCoverageUnit.setName("hello");
      Assert.assertEquals(MatchType.No_Match__Name_Or_Order_Num, MatchType.getMatchType(packageCoverageUnit,
            importCoverageUnit));
      packageCoverageUnit.setName("Top");
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageUnit,
            importCoverageUnit));

      packageCoverageUnit.setNamespace("org.that");
      Assert.assertEquals(MatchType.No_Match__Namespace,
            MatchType.getMatchType(packageCoverageUnit, importCoverageUnit));
      packageCoverageUnit.setNamespace("org.this");
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageUnit,
            importCoverageUnit));

      // Test coverage items
      CoverageItem packageCoverageItem = getCoverageItem(packageCoverageUnit, "1");
      Assert.assertEquals(MatchType.No_Match__Class, MatchType.getMatchType(packageCoverageUnit, packageCoverageItem));

      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

      importCoverageItem.setName("this isn't the text");
      Assert.assertEquals(MatchType.No_Match__Name_Or_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));
      importCoverageItem.setName("this is the text");
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

      importCoverageItem.setOrderNumber("2");
      Assert.assertEquals(MatchType.No_Match__Name_Or_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));
      importCoverageItem.setOrderNumber("1");
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

      // Test folders
      CoverageUnit packageCoverageUnitFolder = getCoverageUnit(coveragePackage, true);
      CoverageUnit importCoverageUnitFolder = getCoverageUnit(coverageImport, true);
      Assert.assertTrue(packageCoverageUnitFolder.isFolder());
      Assert.assertTrue(importCoverageUnitFolder.isFolder());
      Assert.assertEquals(MatchType.Match__Folder, MatchType.getMatchType(packageCoverageUnitFolder,
            importCoverageUnitFolder));

      importCoverageUnitFolder.setFolder(false);
      Assert.assertFalse(importCoverageUnitFolder.isFolder());
      Assert.assertEquals(MatchType.No_Match__Class, MatchType.getMatchType(packageCoverageUnitFolder,
            importCoverageUnitFolder));

   }

   @Test
   public void testIsConceptuallyEqual2() throws OseeCoreException {

      CoveragePackage coveragePackage = getCoveragePackage();
      CoverageUnit packageCoverageUnit = getCoverageUnit(coveragePackage, false);
      CoverageItem packageCoverageItem = getCoverageItem(packageCoverageUnit, "1");

      CoverageImport coverageImport = getCoverageImport();
      CoverageUnit importCoverageUnit = getCoverageUnit(coverageImport, false);
      CoverageItem importCoverageItem = getCoverageItem(importCoverageUnit, "1");

      // Items with same name/order with same parents match
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

      // Items with same name/order but parents with different names don't match
      packageCoverageUnit.setName("Not Same");
      Assert.assertEquals(MatchType.No_Match__Name_Or_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

      // Reset
      packageCoverageUnit.setName(importCoverageUnit.getName());
      Assert.assertEquals(MatchType.Match__Name_And_Order_Num, MatchType.getMatchType(packageCoverageItem,
            importCoverageItem));

   }
}
