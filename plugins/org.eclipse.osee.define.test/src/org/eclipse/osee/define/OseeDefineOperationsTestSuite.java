/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.define;

import org.eclipse.osee.define.operations.publisher.datarights.DataRightClassificationMapTest;
import org.eclipse.osee.define.operations.publisher.datarights.DataRightsFootersIndicatorLoaderPropertyTest;
import org.eclipse.osee.define.operations.publisher.datarights.DataRightsFootersIndicatorLoaderTest;
import org.eclipse.osee.define.operations.publisher.datarights.DataRightsOperationsImplCacheClearTest;
import org.eclipse.osee.define.operations.publisher.datarights.DataRightsOperationsImplStartupTest;
import org.eclipse.osee.define.operations.publisher.datarights.RequiredIndicatorRefresherFallbackTest;
import org.eclipse.osee.define.operations.publisher.datarights.RequiredIndicatorRefresherPropertyTest;
import org.eclipse.osee.define.operations.publisher.datarights.RequiredIndicatorRefresherTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author David W. Miller
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DataRightClassificationMapTest.class,
   DataRightsOperationsImplCacheClearTest.class,
   DataRightsOperationsImplStartupTest.class,
   DataRightsFootersIndicatorLoaderTest.class,
   DataRightsFootersIndicatorLoaderPropertyTest.class,
   RequiredIndicatorRefresherTest.class,
   RequiredIndicatorRefresherFallbackTest.class,
   RequiredIndicatorRefresherPropertyTest.class})
public class OseeDefineOperationsTestSuite {
   // Test Suite
}
