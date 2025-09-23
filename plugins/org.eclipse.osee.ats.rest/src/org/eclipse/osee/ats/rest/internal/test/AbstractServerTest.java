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
package org.eclipse.osee.ats.rest.internal.test;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AbstractServerTest {

   protected final AtsApi atsApi;
   protected final OrcsApi orcsApi;

   public AbstractServerTest(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   protected void assertEquals(Object obj1, Object obj2, XResultData rd) {
      if (!obj1.equals(obj2)) {
         rd.errorf("Not Equal [%s] [%s]", obj1, obj2);
      }
   }

   protected void assertTrue(boolean actual, String comment, XResultData rd) {
      assertBoolean(true, actual, comment, rd);
   }

   protected void assertFalse(boolean actual, String comment, XResultData rd) {
      assertBoolean(false, actual, comment, rd);
   }

   protected void assertBoolean(boolean expected, boolean actual, String comment, XResultData rd) {
      if (!expected == actual) {
         rd.errorf("[%s] expected [%s], actual [%s]", comment, expected, actual);
      }
   }

}
