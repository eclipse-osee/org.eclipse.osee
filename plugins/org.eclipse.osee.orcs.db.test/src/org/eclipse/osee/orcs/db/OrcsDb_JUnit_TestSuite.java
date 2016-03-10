/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
