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

package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.orcs.db.internal.change.Change_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.loader.Loader_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.proxy.Proxy_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.resource.Resource_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.search.Query_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.transaction.Transaction_JUnit_TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   Change_JUnit_TestSuite.class,
   Loader_JUnit_TestSuite.class,
   Proxy_JUnit_TestSuite.class,
   Resource_JUnit_TestSuite.class,
   Query_JUnit_TestSuite.class,
   Transaction_JUnit_TestSuite.class})
public class Internal_JUnit_TestSuite {
   // Test Suite
}
