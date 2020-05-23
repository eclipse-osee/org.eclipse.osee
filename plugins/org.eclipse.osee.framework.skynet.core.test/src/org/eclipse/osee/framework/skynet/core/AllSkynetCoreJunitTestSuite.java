/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchTestSuite;
import org.eclipse.osee.framework.skynet.core.event.EventTestSuite;
import org.eclipse.osee.framework.skynet.core.httpRequests.CommitBranchHttpRequestOperationTest;
import org.eclipse.osee.framework.skynet.core.importing.ImportingSuite;
import org.eclipse.osee.framework.skynet.core.internal.event.InternalEventTestSuite;
import org.eclipse.osee.framework.skynet.core.linking.LinkingTestSuite;
import org.eclipse.osee.framework.skynet.core.relation.RelationTestSuite;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionTestSuite;
import org.eclipse.osee.framework.skynet.core.utility.UtilityTestSuite;
import org.eclipse.osee.framework.skynet.core.word.WordTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactSearchTestSuite.class,
   EventTestSuite.class,
   CommitBranchHttpRequestOperationTest.class,
   ImportingSuite.class,
   InternalEventTestSuite.class,
   LinkingTestSuite.class,
   RelationTestSuite.class,
   TransactionTestSuite.class,
   UtilityTestSuite.class,
   WordTestSuite.class})
public class AllSkynetCoreJunitTestSuite {
   //
}
