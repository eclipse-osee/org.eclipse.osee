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

package org.eclipse.osee.orcs.db;

import org.eclipse.osee.orcs.db.intergration.OrcsDb_Integration_TestSuites;
import org.eclipse.osee.orcs.db.internal.Internal_JUnit_TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite run by build system and OrcsDbTestSuite.launch. Runs both JUnit and Integration tests.
 *
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({Internal_JUnit_TestSuite.class, OrcsDb_Integration_TestSuites.class})
public class OrcsDb_JUnitAndIntegration_TestSuite {
   // Test Suite
}
