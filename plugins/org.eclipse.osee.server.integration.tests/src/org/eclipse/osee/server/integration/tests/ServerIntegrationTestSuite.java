/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.integration.tests;

import org.eclipse.osee.server.integration.tests.endpoint.ArtifactEndpointTest;
import org.eclipse.osee.server.integration.tests.endpoint.RestAssuredTest;
import org.eclipse.osee.server.integration.tests.performance.AccountClientTest;
import org.eclipse.osee.server.integration.tests.performance.OseeClientQueryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   OseeClientQueryTest.class,
   AccountClientTest.class,
   RestAssuredTest.class,
   ArtifactEndpointTest.class})
public class ServerIntegrationTestSuite {
   // Test Suite
}