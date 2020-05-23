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

package org.eclipse.osee.orcs.db;

import org.eclipse.osee.orcs.db.internal.Internal_JUnit_TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite to run all the pure JUnit test suites in this bundle.
 *
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({Internal_JUnit_TestSuite.class})
public class OrcsDb_JUnit_TestSuite {
   // Test Suite
}
