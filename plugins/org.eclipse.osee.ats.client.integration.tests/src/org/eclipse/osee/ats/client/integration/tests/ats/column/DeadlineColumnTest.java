/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.DeadlineColumn;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests DeadlineColumn
 * @author Donald G. Dunne
 */
public class DeadlineColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(DeadlineColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(),
            changes).getStoreObject();
      changes.execute();

      Assert.assertNull(DeadlineColumn.getDate(teamArt));

      Date creationDate = teamArt.getCreatedDate();
      Calendar calendar = DateUtil.getCalendar(creationDate);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date overdueDate = calendar.getTime();

      teamArt.setSoleAttributeValue(AtsAttributeTypes.NeedBy, overdueDate);
      teamArt.persist(getClass().getSimpleName());

      Assert.assertEquals(overdueDate, DeadlineColumn.getDate(teamArt));
      Assert.assertTrue(DeadlineColumn.isDeadlineAlerting(teamArt).isTrue());
      Assert.assertNotNull(DeadlineColumn.getInstance().getColumnImage(teamArt, DeadlineColumn.getInstance(), 0));

      calendar = DateUtil.getCalendar(creationDate);
      calendar.add(Calendar.DAY_OF_MONTH, +5);
      Date futureDate = calendar.getTime();

      teamArt.setSoleAttributeValue(AtsAttributeTypes.NeedBy, futureDate);
      teamArt.persist(getClass().getSimpleName());

      Assert.assertEquals(futureDate, DeadlineColumn.getDate(teamArt));
      Assert.assertTrue(DeadlineColumn.isDeadlineAlerting(teamArt).isFalse());
      Assert.assertNull(DeadlineColumn.getInstance().getColumnImage(teamArt, DeadlineColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
