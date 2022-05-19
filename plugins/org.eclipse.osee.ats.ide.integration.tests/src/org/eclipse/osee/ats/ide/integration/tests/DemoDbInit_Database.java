/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests;

import org.eclipse.osee.ats.ide.integration.tests.util.DbInitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Db Init database and populate with demo data. No extra tests run.
 * 
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DbInitTest.class, DemoDbPopulateSuite.class})
public class DemoDbInit_Database {
   // Test Suite
}
