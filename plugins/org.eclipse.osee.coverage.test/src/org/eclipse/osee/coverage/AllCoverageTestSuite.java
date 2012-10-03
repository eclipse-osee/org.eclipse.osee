/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage;

import org.eclipse.osee.coverage.model.CoverageItemTest;
import org.eclipse.osee.coverage.model.CoverageOptionManagerTest;
import org.eclipse.osee.coverage.model.CoverageUnitTest;
import org.eclipse.osee.coverage.model.SimpleTestUnitProviderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   SimpleTestUnitProviderTest.class,
   CoverageItemTest.class,
   CoverageUnitTest.class,
   MatchTypeTest.class,
   CoverageOptionManagerTest.class,
   DbInitTest.class,
   Coverage_Db_Suite.class})
public class AllCoverageTestSuite {
   // do nothing
}
