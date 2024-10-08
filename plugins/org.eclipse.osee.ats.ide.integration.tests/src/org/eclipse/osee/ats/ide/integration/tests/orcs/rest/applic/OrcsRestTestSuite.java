/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

   ApplicabilityEndpointTest.class,
   ApplicabilityUiEndpointTest.class,
   ArtifactEndpointTest.class,
   BranchEndpointTest.class,
   GridCommanderEndpointTest.class,
   TransactionEndpointTest.class,
   TransactionTransferImportTest.class,
   ArtifactModificationTest.class,
   RelationEndpointTest.class,
   ApiKeyEndpointTest.class})
public class OrcsRestTestSuite {
   // Test Suite
}
