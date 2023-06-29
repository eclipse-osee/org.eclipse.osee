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
   AssigneeColumnTest.class,
   BranchStatusColumnTest.class,
   CancelledDateColumnTest.class,
   CategoryColumnTest.class,
   ChangeTypeColumnTest.class,
   CompletedDateColumnTest.class,
   CreatedDateColumnTest.class,
   DeadlineColumnTest.class,
   DescriptionColumnTest.class,
   EstimatedHoursColumnTest.class,
   FoundInVersionColumnTest.class,
   GroupsColumnTest.class,
   NumberOfTasksAndInWorkTasksColumnsTest.class,
   OriginatorColumnTest.class,
   ParentStateAndIdColumnTest.class,
   PeerToPeerReviewColumnsTest.class,
   PriorityColumnTest.class,
   RelatedToStateColumnTest.class,
   StateColumnTest.class,
   TargetedVersionColumnTest.class,
   TypeColumnTest.class,})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Column_Suite {
   // do nothing
}
