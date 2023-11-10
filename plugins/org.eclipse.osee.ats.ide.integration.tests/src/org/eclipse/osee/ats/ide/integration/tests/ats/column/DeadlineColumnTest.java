/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Calendar;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.column.DeadlineColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(DeadlineColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(DeadlineColumnTest.class.getSimpleName(),
            changes).getStoreObject();
      changes.execute();

      Assert.assertNull(DeadlineColumnUI.getDate(teamArt));

      Date creationDate = teamArt.getCreatedDate();
      Calendar calendar = DateUtil.getCalendar(creationDate);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date overdueDate = calendar.getTime();

      teamArt.setSoleAttributeValue(AtsAttributeTypes.NeedBy, overdueDate);
      teamArt.persist(getClass().getSimpleName());

      Assert.assertEquals(overdueDate, DeadlineColumnUI.getDate(teamArt));
      Assert.assertTrue(DeadlineColumnUI.isDeadlineAlerting(teamArt).isTrue());
      Assert.assertNotNull(DeadlineColumnUI.getInstance().getColumnImage(teamArt, DeadlineColumnUI.getInstance(), 0));

      calendar = DateUtil.getCalendar(creationDate);
      calendar.add(Calendar.DAY_OF_MONTH, +5);
      Date futureDate = calendar.getTime();

      teamArt.setSoleAttributeValue(AtsAttributeTypes.NeedBy, futureDate);
      teamArt.persist(getClass().getSimpleName());

      Assert.assertEquals(futureDate, DeadlineColumnUI.getDate(teamArt));
      Assert.assertTrue(DeadlineColumnUI.isDeadlineAlerting(teamArt).isFalse());
      Assert.assertNull(DeadlineColumnUI.getInstance().getColumnImage(teamArt, DeadlineColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
