/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.util.health;

import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.health.AtsHealthCheckOperation;
import org.eclipse.osee.ats.rest.test.db.AtsIntegrationByMethodRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthEndpointTest {

   @Rule
   public TestRule db = AtsIntegrationByMethodRule.integrationRule(this);

   // @formatter:off
   @OsgiService public IAtsServer atsServer;
   // @formatter:on

   @Test
   public void testInitialized() {
      AtsHealthCheckOperation validate = new AtsHealthCheckOperation(atsServer, atsServer.getJdbcService(), null);
      Assert.assertFalse(validate.run().isErrors());
   }
}
