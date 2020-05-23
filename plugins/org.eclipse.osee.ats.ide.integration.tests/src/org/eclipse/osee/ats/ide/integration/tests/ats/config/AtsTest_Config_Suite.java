/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.config.copy.AtsTest_Demo_Copy_Suite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ProgramEndpointImplTest.class,
   AtsConfigEndpointImplClientIntegrationTest.class,
   AtsActionableItemToTeamDefinitionTest.class,
   TeamResourceTest.class,
   VersionResourceTest.class,
   CountryResourceTest.class,
   ProgramResourceTest.class,
   InsertionResourceTest.class,
   InsertionActivityResourceTest.class,
   ActionableItemResourceTest.class,
   AtsTest_Demo_Copy_Suite.class,
   AtsBranchConfigurationTest.class})
/**
 * This test suite contains test that can be run against any production db
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Config_Suite {

   @BeforeClass
   public static void setup() {
      AtsUtil.setIsInTest(true);
   }
}
