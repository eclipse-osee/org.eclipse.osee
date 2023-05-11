/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.users;

import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

public class SyncOseeAndUserDBDemoTest {

   @Test
   public void testReportOnly() {
      SyncOseeAndUserDBDemo demo = new SyncOseeAndUserDBDemo(false, false, AtsApiService.get());
      XResultData rd = demo.run();
      String message = rd.toString();
      Assert.assertTrue(message, rd.isSuccess());
      Assert.assertTrue(message.contains("Sync OSEE and User DB"));
      Assert.assertTrue(message.contains("Report Only"));
      Assert.assertTrue(message.contains("Processed 22 users"));
   }

   @Test
   public void testPersist() {
      SyncOseeAndUserDBDemo demo = new SyncOseeAndUserDBDemo(true, false, AtsApiService.get());
      XResultData rd = demo.run();
      String message = rd.toString();
      Assert.assertTrue(message, rd.isSuccess());
      Assert.assertTrue(message.contains("Fixed Active to false"));
      Assert.assertTrue(message.contains("Fixed Email to kay.wheeler@google.com"));
      Assert.assertTrue(message.contains("Sync OSEE and User DB"));
      Assert.assertTrue(message.contains("Changes persisted"));
      Assert.assertTrue(message.contains("Processed 22 users"));
   }

}
