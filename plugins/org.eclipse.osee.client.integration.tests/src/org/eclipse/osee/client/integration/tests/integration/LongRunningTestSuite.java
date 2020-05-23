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

package org.eclipse.osee.client.integration.tests.integration;

import org.eclipse.osee.client.integration.tests.integration.skynet.core.BranchPurgeTest;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.BranchStateTest;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.PurgeTransactionTest;
import org.eclipse.osee.client.integration.tests.integration.ui.skynet.RelationIntegrityCheckTest;
import org.eclipse.osee.client.integration.tests.integration.ui.skynet.ViewWordChangeAndDiffTest;
import org.eclipse.osee.client.integration.tests.integration.ui.skynet.WordTemplateRendererTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Long running tests are placed here so they run at the end. This improves developers efficiency when re-running tests.
 *
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   BranchPurgeTest.class,
   BranchStateTest.class,
   RelationIntegrityCheckTest.class,
   PurgeTransactionTest.class,
   ViewWordChangeAndDiffTest.class,
   WordTemplateRendererTest.class,})
public class LongRunningTestSuite {
   // Test Suite
}
