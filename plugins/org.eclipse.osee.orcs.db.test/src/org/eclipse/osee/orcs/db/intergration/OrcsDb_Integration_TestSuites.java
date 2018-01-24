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
