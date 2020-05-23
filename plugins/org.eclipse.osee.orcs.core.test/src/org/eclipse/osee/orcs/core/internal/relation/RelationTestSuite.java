/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.orcs.core.internal.relation.impl.RelationImplTestSuite;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderTestSuite;
import org.eclipse.osee.orcs.core.internal.relation.sorter.SorterTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   RelationImplTestSuite.class,
   OrderTestSuite.class,
   SorterTestSuite.class,
   RelationManagerTest.class,
   RelationTest.class,
   RelationTypeValidityTest.class})
public class RelationTestSuite {
   // Test Suite
}
