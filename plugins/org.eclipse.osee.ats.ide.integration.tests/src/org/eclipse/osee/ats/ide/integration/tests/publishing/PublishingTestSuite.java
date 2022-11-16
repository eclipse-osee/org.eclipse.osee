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

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for the Publishing REST API end point.
 *
 * @author Loren K. Ashley
 */

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses
   (
      {
         PublishingDataRightsTest.class,
         PublishingServerPreviewTest.class,
         PublishingSharedArtifactsFolderTest.class
      }
   )
//@formatter:on

public class PublishingTestSuite {
   // Test Suite
}

/* EOF */
