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
package org.eclipse.osee.framework.core.util;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class XResultDataTimeMapTest {

   @Test
   public void testXResultDataTimeMap() {
      XResultData rd = new XResultData();
      rd.logTimeStart("key1");
      try {
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         // do nothing
      }
      rd.logTimeSpent("key1");
      rd.addTimeMapToResultData();
      Assert.assertTrue(rd.toString(), rd.toString().contains("or 2 sec or"));
      System.err.println(rd.toString());
   }

   @Test
   public void testXResultDataTimeMapIncremental() {
      XResultData rd = new XResultData();
      for (int x = 1; x < 4; x++) {
         rd.logTimeStart("key2");
         try {
            Thread.sleep(2000);
         } catch (InterruptedException ex) {
            // do nothing
         }
         rd.addTimeSpentAndClearTimeForKey("key2");
      }
      rd.addTimeMapToResultData();
      Assert.assertTrue(rd.toString(), rd.toString().contains("or 6 sec or"));
      System.err.println(rd.toString());
   }
}
