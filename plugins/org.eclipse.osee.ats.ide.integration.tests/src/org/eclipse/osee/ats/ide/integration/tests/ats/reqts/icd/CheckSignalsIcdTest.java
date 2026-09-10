/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.reqts.icd;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CheckSignalsIcdTest extends AbstractRestTest {

   @Test
   public void testCheckSignals() throws Exception {
      AtsApi atsApi = AtsApiService.get();
      SignalCheckerData scd = new SignalCheckerData();
      scd.setBranch(DemoBranches.SAW_Bld_2);
      scd.setSigDbSystem("DEMO_V1");
      scd.setRecurse(false);

      SignalCheckerData result = atsApi.getAtsIcdService().checkSignalsTest(scd);
      Assert.assertTrue(result.getRd().isFailed());
      Assert.assertEquals(2, result.getRd().getErrorCount());
      String html = atsApi.getAtsIcdService().getHtmlReport(result);
      Assert.assertTrue(html.contains("Signal Checker Results"));
   }
}