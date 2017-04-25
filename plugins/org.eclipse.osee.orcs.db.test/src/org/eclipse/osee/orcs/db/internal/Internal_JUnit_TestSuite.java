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
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.orcs.db.internal.change.Change_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.exchange.Exchange_JUnit_TestSuite;
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
   Exchange_JUnit_TestSuite.class,
   Loader_JUnit_TestSuite.class,
   Proxy_JUnit_TestSuite.class,
   Resource_JUnit_TestSuite.class,
   Query_JUnit_TestSuite.class,
   Transaction_JUnit_TestSuite.class})
public class Internal_JUnit_TestSuite {
   // Test Suite
}
