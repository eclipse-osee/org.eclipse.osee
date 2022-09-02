/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.synchronization;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for the Synchronization REST API end point.
 *
 * @author Loren K. Ashley
 */

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses
   (
      {
         SynchronizationEndpointTest.class,
         ReqifRelationships.class,
         Permissions.class
      }
   )

public class SynchronizationTestSuite {
   // Test Suite
}
//@formatter:on

/* EOF */
