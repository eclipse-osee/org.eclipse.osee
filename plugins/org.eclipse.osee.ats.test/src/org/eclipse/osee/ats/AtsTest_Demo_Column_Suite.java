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
package org.eclipse.osee.ats;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.column.ActionableItemsColumnTest;
import org.eclipse.osee.ats.column.AssigneeColumnTest;
import org.eclipse.osee.ats.column.BranchStatusColumnTest;
import org.eclipse.osee.ats.column.CancelledDateColumnTest;
import org.eclipse.osee.ats.column.CategoryColumnTest;
import org.eclipse.osee.ats.column.ChangeTypeColumnTest;
import org.eclipse.osee.ats.column.CompletedDateColumnTest;
import org.eclipse.osee.ats.column.CreatedDateColumnTest;
import org.eclipse.osee.ats.column.DeadlineColumnTest;
import org.eclipse.osee.ats.column.DescriptionColumnTest;
import org.eclipse.osee.ats.column.EstimatedHoursColumnTest;
import org.eclipse.osee.ats.column.GroupsColumnTest;
import org.eclipse.osee.ats.column.NumberOfTasksAndInWorkTasksColumnsTest;
import org.eclipse.osee.ats.column.OriginatorColumnTest;
import org.eclipse.osee.ats.column.ParentStateAndIdColumnTest;
import org.eclipse.osee.ats.column.PeerToPeerReviewColumnsTest;
import org.eclipse.osee.ats.column.PriorityColumnTest;
import org.eclipse.osee.ats.column.RelatedToStateColumnTest;
import org.eclipse.osee.ats.column.StateColumnTest;
import org.eclipse.osee.ats.column.TargetedVersionColumnTest;
import org.eclipse.osee.ats.column.TeamColumnTest;
import org.eclipse.osee.ats.column.TypeColumnTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
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
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }
}
