/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.server.integration.tests;

import org.eclipse.osee.server.integration.tests.endpoint.RestAssuredTest;
import org.eclipse.osee.server.integration.tests.performance.AccountClientTest;
import org.eclipse.osee.server.integration.tests.performance.OseeClientQueryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OseeClientQueryTest.class, AccountClientTest.class, RestAssuredTest.class})
public class ServerIntegrationTestSuite {
   // Test Suite
}
