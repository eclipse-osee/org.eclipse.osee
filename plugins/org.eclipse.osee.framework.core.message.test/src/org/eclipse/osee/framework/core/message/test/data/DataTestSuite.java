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

package org.eclipse.osee.framework.core.message.test.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   BranchCommitRequestTest.class,
   BranchCommitResponseTest.class,
   CacheUpdateRequestTest.class,
   ChangeItemTest.class,
   ChangeItemUtilTest.class,
   ChangeVersionTest.class,
   PurgeBranchRequestTest.class,
   SearchRequestTest.class,
   SearchResponseTest.class,
   TransactionCacheUpdateResponseTest.class,})
/**
 * @author Roberto E. Escobar
 */
public class DataTestSuite {
   // Test Suite
}
