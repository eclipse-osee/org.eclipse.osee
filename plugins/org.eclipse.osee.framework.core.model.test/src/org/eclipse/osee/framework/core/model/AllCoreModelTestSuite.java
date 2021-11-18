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

package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.model.access.AccessTestSuite;
import org.eclipse.osee.framework.core.model.cache.CacheTestSuite;
import org.eclipse.osee.framework.core.model.change.ChangeTestSuite;
import org.eclipse.osee.framework.core.model.type.TypeTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccessTestSuite.class, CacheTestSuite.class, ChangeTestSuite.class, TypeTestSuite.class})
/**
 * @author Roberto E. Escobar
 */
public class AllCoreModelTestSuite {
   // Test Suite
}
