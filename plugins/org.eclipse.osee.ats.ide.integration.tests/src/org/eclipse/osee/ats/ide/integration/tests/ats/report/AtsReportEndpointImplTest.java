/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.report;

import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsReportEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsReportEndpointImplTest extends AbstractRestTest {

   @Test
   public void testGetAttrDiffReport() throws Exception {
      AtsApiIde atsApi = AtsApiService.get();
      Date today = new Date();
      Date lastWeek = DateUtil.addWeeks(today, -1);
      // ats/report/AttrDiffReport?date=2025-07-16&artTypeId=80&attrTypeIds=4689644240272725681
      String attrDiffReportHtml =
         atsApi.getServerEndpoints().getReportEp().getAttrDiffReport(DateUtil.get(lastWeek, DateUtil.YYYY_MM_DD),
            AtsArtifactTypes.DemoReqTeamWorkflow.getIdString(), AtsAttributeTypes.CurrentStateName.getIdString());
      Assert.assertTrue(attrDiffReportHtml.contains("55313463"));
      Assert.assertTrue(attrDiffReportHtml.contains(DemoUsers.Joe_Smith.getName()));
   }

}