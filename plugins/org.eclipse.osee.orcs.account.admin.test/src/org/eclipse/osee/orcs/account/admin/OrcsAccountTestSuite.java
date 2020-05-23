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

package org.eclipse.osee.orcs.account.admin;

import org.eclipse.osee.orcs.account.admin.integration.OsgiIntegrationTestSuite;
import org.eclipse.osee.orcs.account.admin.internal.OrcsAccountInternalTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({OsgiIntegrationTestSuite.class, OrcsAccountInternalTestSuite.class})
public class OrcsAccountTestSuite {
   // Test Suite
}
