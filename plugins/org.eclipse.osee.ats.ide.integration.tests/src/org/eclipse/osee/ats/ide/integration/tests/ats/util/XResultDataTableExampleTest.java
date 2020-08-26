/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.table.ExampleTableData;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTable;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTableColumn;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for @link XResultDataTableExample
 *
 * @author Donald G. Dunne
 */
public class XResultDataTableExampleTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      XResultData rd = AtsApiService.get().getServerEndpoints().getConfigEndpoint().getResultTableTest();
      Assert.assertTrue(rd.isSuccess());
      Assert.assertEquals(1, rd.getTables().size());
      XResultTable table = rd.getTables().iterator().next();
      Assert.assertEquals(3, table.getColumns().size());
      XResultTableColumn col = table.getColumns().iterator().next();
      Assert.assertEquals(ExampleTableData.columns.iterator().next().getId(), col.getId());
      Assert.assertEquals(ExampleTableData.columns.iterator().next().getName(), col.getName());
      Assert.assertEquals(ExampleTableData.columns.iterator().next().getType(), col.getType());
      Assert.assertEquals(ExampleTableData.columns.iterator().next().getWidth(), col.getWidth());
      Assert.assertEquals(table.getRows().size(), ExampleTableData.chartDateStrs.size());

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
