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

package org.eclipse.osee.orcs;

import org.eclipse.osee.orcs.api.OrcsAttributeLoadingTest;
import org.eclipse.osee.orcs.api.OrcsAttributeSearchTest;
import org.eclipse.osee.orcs.api.OrcsBranchQueryTest;
import org.eclipse.osee.orcs.api.OrcsBranchTest;
import org.eclipse.osee.orcs.api.OrcsPortingTest;
import org.eclipse.osee.orcs.api.OrcsQueryTest;
import org.eclipse.osee.orcs.api.OrcsRelationLoadingTest;
import org.eclipse.osee.orcs.api.OrcsScriptScopeTest;
import org.eclipse.osee.orcs.api.OrcsScriptTest;
import org.eclipse.osee.orcs.api.OrcsTransactionTest;
import org.eclipse.osee.orcs.api.OrcsTupleTest;
import org.eclipse.osee.orcs.api.OrcsTxQueryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   OrcsQueryTest.class,
   OrcsAttributeLoadingTest.class,
   OrcsAttributeSearchTest.class,
   OrcsBranchQueryTest.class,
   OrcsBranchTest.class,
   OrcsPortingTest.class,
   OrcsRelationLoadingTest.class,
   OrcsScriptScopeTest.class,
   OrcsScriptTest.class,
   OrcsTupleTest.class,
   OrcsTransactionTest.class,
   OrcsTxQueryTest.class})
public class OrcsIntegrationTestSuite {
   // Test Suite
}
