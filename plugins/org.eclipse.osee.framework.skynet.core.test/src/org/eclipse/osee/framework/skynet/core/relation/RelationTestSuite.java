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

package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderTestSuite;
import org.eclipse.osee.framework.skynet.core.relation.sorters.RelationSorterTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({RelationOrderTestSuite.class, RelationSorterTestSuite.class, RelationTypeSideSorterTest.class})
/**
 * @author Roberto E. Escobar
 */
public class RelationTestSuite {
   // Test Suite
}
