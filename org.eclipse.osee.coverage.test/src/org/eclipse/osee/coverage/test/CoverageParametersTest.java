/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test;

import java.util.Collections;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.coverage.editor.params.CoverageParameters;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageParametersTest {

   public static CoverageImport coverageImport = null;

   @Test
   public void testIsAssigneeMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause assignee search not specified
      Assert.assertTrue(coverageParameters.isAssigneeMatch(item));
      Assert.assertTrue(coverageParameters.isAssigneeMatch(unit));

      coverageParameters.setAssignee(UserManager.getUser(SystemUser.Guest));
      // Won't match unit cause assignees don't match
      Assert.assertFalse(coverageParameters.isAssigneeMatch(unit));
      // Will match item cause item doesn't store assignee
      Assert.assertTrue(coverageParameters.isAssigneeMatch(item));

      unit.setAssignees(UsersByIds.getStorageString(Collections.singleton(UserManager.getUser(SystemUser.Guest))));
      // Will match unit cause assignees match
      Assert.assertTrue(coverageParameters.isAssigneeMatch(unit));
   }

   @Test
   public void testIsRationaleMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause Rationale search not specified
      Assert.assertTrue(coverageParameters.isRationaleMatch(item));
      Assert.assertTrue(coverageParameters.isRationaleMatch(unit));

      coverageParameters.setRationale("test");
      // Won't match unit cause Rationales don't match
      Assert.assertFalse(coverageParameters.isRationaleMatch(item));
      // Will match unit cause unit doesn't store Rationale
      Assert.assertTrue(coverageParameters.isRationaleMatch(unit));

      item.setRationale("this is a test");
      // Will match item cause Rationales match
      Assert.assertTrue(coverageParameters.isRationaleMatch(item));
   }

   @Test
   public void testIsNotesMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause notes search not specified
      Assert.assertTrue(coverageParameters.isNotesMatch(item));
      Assert.assertTrue(coverageParameters.isNotesMatch(unit));

      coverageParameters.setNotes("test");
      // Won't match unit cause unit notes is null
      Assert.assertFalse(coverageParameters.isNotesMatch(unit));
      // Will match item cause item doesn't store Notes
      Assert.assertTrue(coverageParameters.isNotesMatch(item));

      unit.setNotes("this is a test");
      // Will match unit cause unit notes contains word test
      Assert.assertTrue(coverageParameters.isNotesMatch(unit));

      unit.setNotes("alpha beta gamma");
      // Won't match unit cause unit notes does not contain test
      Assert.assertFalse(coverageParameters.isNotesMatch(unit));
   }

   @Test
   public void testIsCoverageMethodMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause coverageMethod search not specified
      Assert.assertTrue(coverageParameters.isCoverageMethodMatch(item));
      Assert.assertTrue(coverageParameters.isCoverageMethodMatch(unit));

      coverageParameters.setCoverageMethods(Collections.singleton(CoverageMethodEnum.Exception_Handling));
      // Won't match item cause item coverageMethod is null
      Assert.assertFalse(coverageParameters.isCoverageMethodMatch(item));
      // Will match unit cause unit doesn't store CoverageMethods
      Assert.assertTrue(coverageParameters.isCoverageMethodMatch(unit));

      item.setCoverageMethod(CoverageMethodEnum.Exception_Handling);
      // Will match unit cause unit coverageMethod contains word test
      Assert.assertTrue(coverageParameters.isCoverageMethodMatch(item));

      item.setCoverageMethod(CoverageMethodEnum.Test_Unit);
      // Won't match unit cause unit coverageMethod does not contain test
      Assert.assertFalse(coverageParameters.isCoverageMethodMatch(item));
   }

   @Test
   public void testIsNameMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause name search not specified
      Assert.assertTrue(coverageParameters.isNameMatch(item));
      Assert.assertTrue(coverageParameters.isNameMatch(unit));

      coverageParameters.setName("test");
      // Won't match unit cause unit name is null
      Assert.assertFalse(coverageParameters.isNameMatch(unit));
      // Won't match unit cause item name is null
      Assert.assertFalse(coverageParameters.isNameMatch(item));

      unit.setName("this is a test");
      // Will match unit cause unit name contains word test
      Assert.assertTrue(coverageParameters.isNameMatch(unit));
      // Will match item cause item's getName is made up of text value
      item.setText("this is a test");
      Assert.assertTrue(coverageParameters.isNameMatch(item));

      unit.setName("alpha beta gamma");
      // Won't match unit cause unit name does not contain test
      Assert.assertFalse(coverageParameters.isNameMatch(unit));
      // Won't match item cause item's getName is made up of text value
      item.setText("alpha beta gamma");
      Assert.assertFalse(coverageParameters.isNameMatch(item));
   }

   @Test
   public void testIsNamespaceMatch() throws Exception {
      CoverageParameters coverageParameters = new CoverageParameters(new CoverageImport("test"));
      CoverageUnit unit = new CoverageUnit(null, "Quark", "C:/UserData");
      CoverageItem item = new CoverageItem(unit, CoverageMethodEnum.Test_Unit, "1");

      // Will match both unit and item cause Namespace search not specified
      Assert.assertTrue(coverageParameters.isNamespaceMatch(item));
      Assert.assertTrue(coverageParameters.isNamespaceMatch(unit));

      coverageParameters.setNamespace("test");
      // Won't match unit cause unit Namespace is null
      Assert.assertFalse(coverageParameters.isNamespaceMatch(unit));
      // Won't match unit cause item Namespace is null
      Assert.assertFalse(coverageParameters.isNamespaceMatch(item));

      unit.setNamespace("this is a test");
      // Will match unit cause unit Namespace contains word test
      Assert.assertTrue(coverageParameters.isNamespaceMatch(unit));
      // Will match item cause item's getNamespace is made up of text value
      item.setText("this is a test");
      Assert.assertTrue(coverageParameters.isNamespaceMatch(item));

      unit.setNamespace("alpha beta gamma");
      // Won't match unit cause unit Namespace does not contain test
      Assert.assertFalse(coverageParameters.isNamespaceMatch(unit));
      // Won't match item cause item's getNamespace is made up of text value
      item.setText("alpha beta gamma");
      Assert.assertFalse(coverageParameters.isNamespaceMatch(item));
   }

   @Test
   public void testCoverageParameters() throws Exception {
      CoverageImport1TestBlam coverageImport1TestBlam = new CoverageImport1TestBlam();
      coverageImport = coverageImport1TestBlam.run();
      Assert.assertNotNull(coverageImport);

      // Check import results
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered().size());
      Assert.assertEquals(121, coverageImport.getCoverageItems().size());
      Assert.assertEquals(49, coverageImport.getCoveragePercent());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Deactivated_Code).size());
      Assert.assertEquals(0, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Exception_Handling).size());
      Assert.assertEquals(60, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Test_Unit).size());
      Assert.assertEquals(61, coverageImport.getCoverageItemsCovered(CoverageMethodEnum.Not_Covered).size());

      CoverageParameters coverageParameters = new CoverageParameters(coverageImport);
      Result result = coverageParameters.isParameterSelectionValid();
      Assert.assertTrue(result.isTrue());

      // Test Show All
      coverageParameters.clearAll();
      Pair<Set<ICoverage>, Set<ICoverage>> itemsAndParents = coverageParameters.performSearchGetResults();
      Assert.assertEquals(121, itemsAndParents.getFirst().size());
      Assert.assertEquals(4, itemsAndParents.getSecond().size());

      // Exception_Handling
      coverageParameters.setCoverageMethods(Collections.singleton(CoverageMethodEnum.Exception_Handling));
      itemsAndParents = coverageParameters.performSearchGetResults();
      Assert.assertEquals(0, itemsAndParents.getFirst().size());
      Assert.assertEquals(0, itemsAndParents.getSecond().size());

      // Test_Unit
      coverageParameters.setCoverageMethods(Collections.singleton(CoverageMethodEnum.Test_Unit));
      itemsAndParents = coverageParameters.performSearchGetResults();
      Assert.assertEquals(60, itemsAndParents.getFirst().size());
      Assert.assertEquals(4, itemsAndParents.getSecond().size());
      Assert.assertEquals(12, CoverageUtil.getFirstNonFolderCoverageUnits(itemsAndParents.getFirst()).size());

      // Not_Covered
      coverageParameters.setCoverageMethods(Collections.singleton(CoverageMethodEnum.Not_Covered));
      itemsAndParents = coverageParameters.performSearchGetResults();
      Assert.assertEquals(61, itemsAndParents.getFirst().size());
      Assert.assertEquals(4, itemsAndParents.getSecond().size());
      Assert.assertEquals(12, CoverageUtil.getFirstNonFolderCoverageUnits(itemsAndParents.getFirst()).size());
   }
}
