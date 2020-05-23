/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.util;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.test.db.AtsIntegrationByMethodRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Donald G. Dunne
 */
public class AtsDabaseInitializedTest {

   @Rule
   public TestRule db = AtsIntegrationByMethodRule.integrationRule(this);

   // @formatter:off
   @OsgiService public IAtsServer atsServer;
   // @formatter:on

   @Test
   public void testInitialized() {
      Assert.assertNotNull("ATS Server is NOT alive!", atsServer);

      IAtsTeamDefinition teamDef = atsServer.getTeamDefinitionService().getTeamDefinition("SAW Code");
      Assert.assertNotNull(teamDef);
   }
}
