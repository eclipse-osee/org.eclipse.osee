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
package org.eclipse.osee.framework.core;

import org.eclipse.osee.framework.core.data.DataTestSuite;
import org.eclipse.osee.framework.core.enums.EnumsTestSuite;
import org.eclipse.osee.framework.core.exception.ExceptionTestSuite;
import org.eclipse.osee.framework.core.executor.ExecutorAdminTestSuite;
import org.eclipse.osee.framework.core.operation.OperationTestSuite;
import org.eclipse.osee.framework.core.ops.OpsTestSuite;
import org.eclipse.osee.framework.core.util.UtilCoreTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DataTestSuite.class,
   EnumsTestSuite.class,
   ExceptionTestSuite.class,
   ExecutorAdminTestSuite.class,
   UtilCoreTestSuite.class,
   OperationTestSuite.class,
   OpsTestSuite.class})
/**
 * @author Roberto E. Escobar
 */
public class FrameworkCoreTestSuite {
   // Test Suite
}
