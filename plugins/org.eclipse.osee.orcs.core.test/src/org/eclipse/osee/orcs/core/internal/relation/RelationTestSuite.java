/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
