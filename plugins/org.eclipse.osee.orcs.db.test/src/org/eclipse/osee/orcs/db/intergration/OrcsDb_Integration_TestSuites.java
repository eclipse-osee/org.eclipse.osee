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

package org.eclipse.osee.orcs.db.intergration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Integration test suite. DO NOT RUN WITH PDE JUnit launch. Use OrcsDbTestSuite.launch instead.
 *
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({OseeInfoDataAccessorTest.class, PurgeAttributeTest.class})
public class OrcsDb_Integration_TestSuites {
   // Test Suite
}
