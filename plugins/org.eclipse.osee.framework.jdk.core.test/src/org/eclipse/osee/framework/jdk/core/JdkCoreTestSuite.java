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
package org.eclipse.osee.framework.jdk.core;

import org.eclipse.osee.framework.jdk.core.rules.JdkCoreRuleTestSuite;
import org.eclipse.osee.framework.jdk.core.text.JdkCoreTextTestSuite;
import org.eclipse.osee.framework.jdk.core.type.JdkCoreTypeTestSuite;
import org.eclipse.osee.framework.jdk.core.util.JdkCoreUtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
   JdkCoreRuleTestSuite.class,
   JdkCoreTextTestSuite.class,
   JdkCoreTypeTestSuite.class,
   JdkCoreUtilTestSuite.class,})
public class JdkCoreTestSuite {
   // Test Suite Class
}
