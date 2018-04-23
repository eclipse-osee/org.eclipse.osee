/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.ev.EarnedValueReportOperation;
import org.eclipse.osee.ats.ev.EarnedValueReportResult;
import org.eclipse.osee.ats.ev.SearchWorkPackageOperation;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.Operations;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link EarnedValueReportOperation}
 *
 * @author Donald G. Dunne
 */
public class EarnedValueReportOperationTest {

   @Test
   public void testReport() {
      List<IAtsTeamDefinition> teamDefs = new ArrayList<>();
      IAtsTeamDefinition teamDef = AtsClientService.get().getCache().getAtsObject(DemoArtifactToken.SAW_SW);
      teamDefs.add(teamDef);
      SearchWorkPackageOperation srch = new SearchWorkPackageOperation("srch", teamDefs, true,
         new ArrayList<IAtsActionableItem>(), false, Active.Both);
      Operations.executeWorkAndCheckStatus(srch);
      Set<IAtsWorkPackage> workPackages = srch.getResults();
      Assert.assertEquals(3, workPackages.size());

      // Run report and validate results
      EarnedValueReportOperation earnedValueOperation2 = new EarnedValueReportOperation("report2", workPackages);
      Operations.executeWorkAndCheckStatus(earnedValueOperation2);
      Assert.assertEquals(3, earnedValueOperation2.getResults().size());
      int num01 = 0, num03 = 0;
      for (EarnedValueReportResult result : earnedValueOperation2.getResults()) {
         String id = result.getValue(EarnedValueReportOperation.Work_Package_Id);
         if (id.endsWith("01")) {
            num01++;
         } else if (id.endsWith("03")) {
            num03++;
         } else {
            Assert.fail(String.format("Unexpected result [%s]", id));
         }
      }
      Assert.assertEquals("Should be 2 items with WP_01, was %d", 2, num01);
      Assert.assertEquals("Should be 1 items with WP_03, was %d", 1, num03);
   }

   @Test
   public void testEmptyReport() {
      List<IAtsWorkPackage> workPackages = Arrays.asList(AtsClientService.get().getEarnedValueService().getWorkPackage(
         AtsClientService.get().getQueryService().getArtifact(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C)));
      Assert.assertEquals(1, workPackages.size());

      // Confirm that report is empty for Work Packages
      EarnedValueReportOperation earnedValueoperation = new EarnedValueReportOperation("report", workPackages);
      Operations.executeWorkAndCheckStatus(earnedValueoperation);
      Assert.assertEquals(0, earnedValueoperation.getResults().size());
   }
}
