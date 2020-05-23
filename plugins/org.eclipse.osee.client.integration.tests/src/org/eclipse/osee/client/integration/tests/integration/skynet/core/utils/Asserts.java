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

package org.eclipse.osee.client.integration.tests.integration.skynet.core.utils;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.Assert;

/**
 * @author Ryan D. Brooks
 */
public final class Asserts {

   private Asserts() {
      // Utility
   }

   public static IStatus assertOperation(IOperation operation, int expectedSeverity) {
      IStatus status = Operations.executeWork(operation);
      Assert.assertEquals(status.toString(), expectedSeverity, status.getSeverity());
      return status;
   }

   public static void assertThatEquals(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String tableName : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String equalStr = postCount.get(tableName).equals(prevCount.get(tableName)) ? "Equal" : "ERROR, NotEqual";
            System.out.println(String.format(equalStr + ": [%s] pre[%d] post[%d]", tableName, prevCount.get(tableName),
               postCount.get(tableName)));
         }
      }
      for (String tableName : prevCount.keySet()) {
         Assert.assertTrue(String.format("[%s] count not equal pre[%d] post[%d]", tableName, prevCount.get(tableName),
            postCount.get(tableName)), postCount.get(tableName).equals(prevCount.get(tableName)));
      }
   }

   public static void assertThatIncreased(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String name : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String incStr = postCount.get(name) > prevCount.get(name) ? "Increased" : "ERROR, Not Increased";
            System.out.println(
               String.format(incStr + ": [%s] pre[%d] vs post[%d]", name, prevCount.get(name), postCount.get(name)));
         }
      }
      for (String name : prevCount.keySet()) {
         Assert.assertTrue(String.format("[%s] did not increase as expected: pre[%d] vs post[%d]", name,
            prevCount.get(name), postCount.get(name)), postCount.get(name) > prevCount.get(name));
      }
   }
}