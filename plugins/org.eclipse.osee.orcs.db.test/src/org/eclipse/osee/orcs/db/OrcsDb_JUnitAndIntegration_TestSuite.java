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
