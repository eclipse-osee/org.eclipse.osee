/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   RelatedToStateColumnTest.class,
   NumberOfTasksAndInWorkTasksColumnsTest.class,
   EstimatedHoursColumnTest.class,
   PriorityColumnTest.class,
   TargetedVersionColumnTest.class,
   FoundInVersionColumnTest.class,
   TypeColumnTest.class,
   ParentStateAndIdColumnTest.class,
   StateColumnTest.class,
   OriginatorColumnTest.class,
   GroupsColumnTest.class,
   DescriptionColumnTest.class,
   PeerToPeerReviewColumnsTest.class,
   ChangeTypeColumnTest.class,
   DeadlineColumnTest.class,
   CreatedDateColumnTest.class,
   BranchStatusColumnTest.class,
   CancelledDateColumnTest.class,
   CompletedDateColumnTest.class,
   CategoryColumnTest.class,})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Column_Suite {
   // do nothing
}
