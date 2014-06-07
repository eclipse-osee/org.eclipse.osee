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
package org.eclipse.osee.account.admin.internal;

import org.eclipse.osee.account.admin.internal.validator.ValidatorTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AccountAdminImplTest.class,
   AccountResolverTest.class,
   ValidatorTestSuite.class,
   SubscriptionAdminImplTest.class,
   SubscriptionResolverTest.class})
public class InternalTestSuite {
   // Test Suite
}
