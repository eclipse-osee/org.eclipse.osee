/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.log;

import static org.eclipse.osee.ats.core.users.AbstractUserTest.joe;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AtsLog}
 *
 * @author Donald G. Dunne
 */
public class AtsLogTest {

   public static IAtsLogItem getAnalyzeTestLogItem(Date date, IAtsUser user) {
      return new LogItem(LogType.Error, date, user.getUserId(), "Analyze", "my msg");
   }

   public static IAtsLogItem getImplementTestLogItem(Date date, IAtsUser user) {
      return new LogItem(LogType.Error, date, user.getUserId(), "Implement", "my msg2");
   }

   @Test
   public void testToString() {
      AtsLog log = getTestLog();
      Assert.assertEquals(
         "my msg (Error)from Analyze by joe on 11/01/2011 09:23 AM\nmy msg2 (Error)from Implement by joe on 11/01/2011 09:23 AM",
         log.toString());
   }

   @Test
   public void testGetLastStatusDate() {
      AtsLog log = new AtsLog();
      Assert.assertNull(log.getLastStatusDate());
      Date testDate2011 = getTestDate2011();
      IAtsLogItem analyzeTestLogItem = getAnalyzeTestLogItem(testDate2011, joe);
      analyzeTestLogItem.setType(LogType.Metrics);
      log.addLogItem(analyzeTestLogItem);
      IAtsLogItem implementTestLogItem = getImplementTestLogItem(getTestDate2012(), joe);
      implementTestLogItem.setType(LogType.Metrics);
      log.addLogItem(implementTestLogItem);
      Assert.assertTrue(log.isDirty());
      Assert.assertTrue("Implement", log.getLastStatusDate().after(testDate2011));
   }

   @Test
   public void testGetLogItemsReversed() {
      AtsLog log = getTestLog();
      Assert.assertEquals("Implement", log.getLogItemsReversed().iterator().next().getState());
   }

   private AtsLog getTestLog() {
      AtsLog log = new AtsLog();
      log.addLogItem(getAnalyzeTestLogItem(getTestDate2011(), joe));
      log.addLogItem(getImplementTestLogItem(getTestDate2011(), joe));
      return log;
   }

   @Test
   public void testInternalResetCreatedDate() {
      AtsLog log = new AtsLog();
      log.internalResetCreatedDate(new Date());
      log.addLog(LogType.StateCancelled, "analyze", "msg", "345");

      IAtsLogItem item = log.addLog(LogType.Originated, "analyze", "msg", joe.getUserId());
      Assert.assertTrue(log.isDirty());
      Date testDate2011 = getTestDate2011();
      item.setDate(testDate2011);
      Assert.assertEquals(testDate2011, log.getStateEvent(LogType.Originated).getDate());
      Date testDate2012 = getTestDate2012();
      log.internalResetCreatedDate(testDate2012);
      Assert.assertEquals(testDate2012, log.getStateEvent(LogType.Originated).getDate());
      Assert.assertTrue(log.isDirty());
   }

   @Test
   public void testInternalGetCancelledReason() {
      AtsLog log = new AtsLog();
      Assert.assertEquals("", log.internalGetCancelledReason());
      log.addLog(LogType.StateCancelled, "analyze", "cancel reason", joe.getUserId());
      Assert.assertEquals("cancel reason", log.internalGetCancelledReason());
   }

   @Test
   public void testInternalGetCompletedFromState() {
      AtsLog log = new AtsLog();
      Assert.assertEquals("", log.internalGetCompletedFromState());
      log.addLog(LogType.StateEntered, "analyze", "", joe.getUserId());
      log.addLog(LogType.StateComplete, "analyze", "", joe.getUserId());
      log.addLog(LogType.StateEntered, "completed", "", joe.getUserId());
      Assert.assertEquals("analyze", log.internalGetCompletedFromState());
      Assert.assertTrue(log.isDirty());
   }

   @Test
   public void testClearLog() {
      AtsLog log = getTestLog();
      Assert.assertEquals(2, log.getLogItems().size());
      log.clearLog();
      Assert.assertEquals(0, log.getLogItems().size());
      Assert.assertTrue(log.isDirty());
   }

   @Test
   public void testGetLastEvent() {
      AtsLog log = getTestLog();
      Assert.assertEquals("Implement", log.getLastEvent(LogType.Error).getState());
      log.addLog(LogType.Error, "complete", "msg", joe.getUserId());
      Assert.assertEquals("complete", log.getLastEvent(LogType.Error).getState());
      Assert.assertNull(log.getLastEvent(LogType.Metrics));
   }

   @Test
   public void testGetStateEventLogTypeString() {
      AtsLog log = new AtsLog();
      Assert.assertEquals(null, log.getStateEvent(LogType.Error, "Analyze"));

      log = getTestLog();
      IAtsLogItem secondLog = log.addLogItem(getAnalyzeTestLogItem(getTestDate2011(), joe));
      secondLog.setMsg("2nd msg");
      Assert.assertEquals("2nd msg", log.getStateEvent(LogType.Error, "Analyze").getMsg());

      Assert.assertEquals(null, log.getStateEvent(LogType.Error, "Analyze2"));

      Assert.assertEquals(null, log.getStateEvent(LogType.Originated, "Analyze"));
   }

   @Test
   public void testGetLogItems() {
      AtsLog log = getTestLog();
      Assert.assertEquals("Analyze", log.getLogItems().iterator().next().getState());
   }

   @Test
   public void testIsDirty() {
      AtsLog log = new AtsLog();
      Assert.assertFalse(log.isDirty());
      log.setDirty(true);
      Assert.assertTrue(log.isDirty());
   }

   @Test
   public void testSetGetLogId() {
      AtsLog log = new AtsLog();
      Assert.assertEquals("none", log.getLogId());
      log.setLogId("id");
      Assert.assertEquals("id", log.getLogId());
   }

   public Date getTestDate2011() {
      Calendar cal = Calendar.getInstance();
      cal.set(2011, 10, 1, 9, 23, 55);
      Date date = cal.getTime();
      return date;
   }

   public Date getTestDate2012() {
      Calendar cal = Calendar.getInstance();
      cal.set(2012, 10, 1, 9, 33, 77);
      Date date = cal.getTime();
      return date;
   }
}