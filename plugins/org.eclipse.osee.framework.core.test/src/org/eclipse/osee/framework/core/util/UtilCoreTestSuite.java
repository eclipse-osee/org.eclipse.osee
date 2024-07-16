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

package org.eclipse.osee.framework.core.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AbstractOperationTest.class,
   ConditionsTest.class,
   ManifestTest.class,
   WordCoreUtilTest.class,
   XResultDataTest.class,
   XResultDataTimeMapTest.class})
public class UtilCoreTestSuite {
   // Test Suite
}
