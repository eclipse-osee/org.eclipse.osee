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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest;

import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class KeyValueEndpointTest {

   @Test
   public void testCreateGetUpdate() {

      String value = OseeApiService.keyValueSvc().getByKey(1553L);
      Assert.assertTrue(value == null);

      boolean success = OseeApiService.keyValueSvc().putByKey(1553L, "Home");
      Assert.assertTrue(success);

      value = OseeApiService.keyValueSvc().getByKey(1553L);
      Assert.assertEquals("Home", value);

   }
}
