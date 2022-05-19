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
 * Test suite for the {@link org.eclipse.osee.synchronization.rest} OSGI bundle.
 *
 * @author Loren K. Ashley
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({SynchronizationEndpointTest.class, ReqifRelationships.class})

public class SynchronizationTestSuite {
   // Test Suite
}
