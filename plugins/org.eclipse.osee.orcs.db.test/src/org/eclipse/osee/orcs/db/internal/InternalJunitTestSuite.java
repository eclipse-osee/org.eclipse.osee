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

import org.eclipse.osee.orcs.db.internal.exchange.ExchangeTestSuite;
import org.eclipse.osee.orcs.db.internal.loader.JUnitLoaderTestSuite;
import org.eclipse.osee.orcs.db.internal.proxy.ProxyTestSuite;
import org.eclipse.osee.orcs.db.internal.resource.ResourceTestSuite;
import org.eclipse.osee.orcs.db.internal.search.SearchTestSuite;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   ExchangeTestSuite.class,
   JUnitLoaderTestSuite.class,
   ProxyTestSuite.class,
   ResourceTestSuite.class,
   TransactionTestSuite.class,
   SearchTestSuite.class})
public class InternalJunitTestSuite {
   // Test Suite
}
