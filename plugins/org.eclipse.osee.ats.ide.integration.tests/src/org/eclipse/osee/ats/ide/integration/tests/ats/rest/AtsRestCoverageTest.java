/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.rest;

import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.junit.Assert;

/**
 * Calculate and report test coverage of ATS REST calls. <br/>
 * Actual REST calls are retrieved from the OSEE_ACTIVITY table that logs all server calls. <br/>
 * Expected come from the ats_?wadl which shows available REST calls
 *
 * @author Donald G. Dunne
 */
public class AtsRestCoverageTest {

   @org.junit.Test
   public void test() {
      XResultData rd = AtsApiService.get().getServerEndpoints().getReportEp().getRestCoverageReport();
      XResultDataUI.reportAndOpen(rd, "ATS REST Test Coverage", "atsCoverage.html");

      Assert.assertTrue("Percent test coverage too low; See html report in browser.", rd.isSuccess());
   }

}
