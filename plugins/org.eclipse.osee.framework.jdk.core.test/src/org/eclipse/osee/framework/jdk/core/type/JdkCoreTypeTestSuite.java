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

package org.eclipse.osee.framework.jdk.core.type;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   CompositeKeyHashMapTest.class,
   MatchLocationTest.class,
   PairTest.class,
   PropertyStoreTest.class,
   PropertyStoreWriterTest.class,
   QuadTest.class,
   ResultSetIterableTest.class,
   StringRangeTest.class,
   CharSequenceWindowTest.class,
   TripletTest.class})
public class JdkCoreTypeTestSuite {
   // Test Suite
}
