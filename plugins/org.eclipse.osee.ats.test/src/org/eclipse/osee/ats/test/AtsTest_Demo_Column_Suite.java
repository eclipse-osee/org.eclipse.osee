/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.test.column.ActionableItemsColumnTest;
import org.eclipse.osee.ats.test.column.AssigneeColumnTest;
import org.eclipse.osee.ats.test.column.BranchStatusColumnTest;
import org.eclipse.osee.ats.test.column.CancelledDateColumnTest;
import org.eclipse.osee.ats.test.column.CategoryColumnTest;
import org.eclipse.osee.ats.test.column.ChangeTypeColumnTest;
import org.eclipse.osee.ats.test.column.CompletedDateColumnTest;
import org.eclipse.osee.ats.test.column.CreatedDateColumnTest;
import org.eclipse.osee.ats.test.column.DeadlineColumnTest;
import org.eclipse.osee.ats.test.column.DescriptionColumnTest;
import org.eclipse.osee.ats.test.column.EstimatedHoursColumnTest;
import org.eclipse.osee.ats.test.column.GroupsColumnTest;
import org.eclipse.osee.ats.test.column.NumberOfTasksAndInWorkTasksColumnsTest;
import org.eclipse.osee.ats.test.column.OriginatorColumnTest;
import org.eclipse.osee.ats.test.column.ParentStateAndIdColumnTest;
import org.eclipse.osee.ats.test.column.PeerToPeerReviewColumnsTest;
import org.eclipse.osee.ats.test.column.PriorityColumnTest;
import org.eclipse.osee.ats.test.column.RelatedToStateColumnTest;
import org.eclipse.osee.ats.test.column.StateColumnTest;
import org.eclipse.osee.ats.test.column.TargetedVersionColumnTest;
import org.eclipse.osee.ats.test.column.TeamColumnTest;
import org.eclipse.osee.ats.test.column.TypeColumnTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   RelatedToStateColumnTest.class,
   NumberOfTasksAndInWorkTasksColumnsTest.class,
   EstimatedHoursColumnTest.class,
   PriorityColumnTest.class,
   TargetedVersionColumnTest.class,
   TypeColumnTest.class,
   TeamColumnTest.class,
   ParentStateAndIdColumnTest.class,
   StateColumnTest.class,
   OriginatorColumnTest.class,
   GroupsColumnTest.class,
   DescriptionColumnTest.class,
   PeerToPeerReviewColumnsTest.class,
   ChangeTypeColumnTest.class,
   DeadlineColumnTest.class,
   CreatedDateColumnTest.class,
   ActionableItemsColumnTest.class,
   AssigneeColumnTest.class,
   BranchStatusColumnTest.class,
   CancelledDateColumnTest.class,
   CompletedDateColumnTest.class,
   CategoryColumnTest.class,})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Column_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }
}
