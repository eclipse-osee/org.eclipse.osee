/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.lifecycle.test;

import org.eclipse.osee.framework.lifecycle.test.internal.InternalLifecycleTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   InternalLifecycleTestSuite.class, //
})
/**
 * @author Roberto E. Escobar
 */
public class AllLifecycleTestSuite {
   // Test Suite
}
