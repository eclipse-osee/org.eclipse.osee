/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import java.util.Collection;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnServiceTest {

   @org.junit.Test
   public void testGetColumns() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      Collection<AtsCoreColumn> cols = AtsApiService.get().getColumnService().getColumns();
      Assert.assertTrue("No columns defined", cols.size() > 50);

      XResultData loadResults = AtsApiService.get().getColumnService().getLoadResults();
      Assert.assertTrue(loadResults.toString(), loadResults.isSuccess());

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   @org.junit.Test
   public void testAtsConfigViewColumnsOverride() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      Assert.assertEquals(120, AtsColumnTokensDefault.LegacyPcrIdColumn.getWidth());
      Assert.assertEquals(false, AtsColumnTokensDefault.LegacyPcrIdColumn.isInheritParent());
      Assert.assertEquals(false, AtsColumnTokensDefault.LegacyPcrIdColumn.isActionRollup());

      AtsCoreColumn col = AtsApiService.get().getColumnService().getColumn(AtsColumnTokensDefault.LegacyPcrIdColumn);

      Assert.assertEquals(50, col.getColumnToken().getWidth());
      Assert.assertEquals(true, col.getColumnToken().isInheritParent());
      Assert.assertEquals(true, col.getColumnToken().isActionRollup());

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   @org.junit.Test
   public void testProviders() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      Assert.assertTrue(AtsApiService.get().getColumnService().getColumns().size() > 50);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
