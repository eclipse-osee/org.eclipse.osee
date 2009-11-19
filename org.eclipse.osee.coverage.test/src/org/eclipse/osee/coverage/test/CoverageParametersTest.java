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
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageParametersTest {

   public static CoverageImport coverageImport = null;

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
      Assert.assertTrue(result.getText().contains("must select at least one"));

      // Test Show All
      coverageParameters.setShowAll(true);
      Pair<Set<ICoverage>, Set<ICoverage>> itemsAndParents = coverageParameters.performSearchGetResults();
      Assert.assertEquals(180, itemsAndParents.getFirst().size());
      Assert.assertEquals(4, itemsAndParents.getSecond().size());

      // Exception_Handling
      coverageParameters.setShowAll(false);
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
