/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.ide.integration.tests.ats.config.copy.AtsTest_Demo_Copy_Suite;
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
   // do nothing
}
