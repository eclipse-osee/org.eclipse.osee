/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Calendar;
import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.DeadlineColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests DeadlineColumn
 * @author Donald G Dunne
 */
public class DeadlineColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(DeadlineColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(), transaction);
      transaction.execute();

      Assert.assertNull(DeadlineColumn.getDate(teamArt));

      Date creationDate = teamArt.getLog().getCreationDate();
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

   }
}
