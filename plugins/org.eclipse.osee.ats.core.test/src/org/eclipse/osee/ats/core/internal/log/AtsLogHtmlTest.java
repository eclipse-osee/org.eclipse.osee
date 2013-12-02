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

import static org.mockito.Mockito.when;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AtsLogHtml}
 * 
 * @author Donald G. Dunne
 */
public class AtsLogHtmlTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock IAttributeResolver attrResolver;
   @Mock IAtsUser Joe;
   @Mock IAtsChangeSet changes;
   @Mock ILogStorageProvider storeProvider;
   @Mock IAtsUserService atsUserService;
   // @formatter:on
   private AtsLog log;

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      log = new AtsLog();

      when(Joe.getName()).thenReturn("Joe");
      when(workItem.getLog()).thenReturn(log);
      when(storeProvider.getLogTitle()).thenReturn("This is the title");
   }

   @Test
   public void testEmpty() {
      when(storeProvider.getLogXml()).thenReturn("");
      AtsLogHtml writer = new AtsLogHtml(log, storeProvider, atsUserService, false);
      String html = writer.get();
      Assert.assertEquals("", html);
   }

   @Test
   public void testSave() {

      // without title and with user name resolution
      when(atsUserService.getUserById("456")).thenReturn(Joe);
      IAtsLogItem item = log.addLog(LogType.Originated, "", "", "456");
      Date testDate2011 = getTestDate2011();
      item.setDate(testDate2011);
      item = log.addLog(LogType.StateEntered, "Analyze", "", "456");
      item.setDate(testDate2011);
      AtsLogHtml writer = new AtsLogHtml(log, storeProvider, atsUserService, false);
      String html = writer.get();
      Assert.assertEquals(getHtmlStr(), html);

      // With title
      writer = new AtsLogHtml(log, storeProvider, atsUserService, true);
      html = writer.get();
      Assert.assertEquals(getHtmlStrWithTitle(), html);

      // No user name
      when(atsUserService.getUserById("456")).thenReturn(null);
      writer = new AtsLogHtml(log, storeProvider, atsUserService, false);
      html = writer.get();
      Assert.assertTrue(html.contains("456"));

      // without store provider
      writer = new AtsLogHtml(log, null, null, false);
      html = writer.get();
      Assert.assertTrue(html.contains("456"));

      // No user name
      when(atsUserService.getUserById("456")).thenReturn(Joe);
      when(Joe.getName()).thenReturn(null);
      writer = new AtsLogHtml(log, storeProvider, atsUserService, false);
      html = writer.get();
      Assert.assertTrue(html.contains("456"));

   }

   private Object getHtmlStr() {
      return "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\"><tr><th>Event</th><th>State</th><th>Message</th><th>User</th><th>Date</th></tr><tr><td>Originated</td><td>.</td><td>.</td><td>Joe</td><td>12/01/2006 01:00 AM</td></tr><tr><td>StateEntered</td><td>Analyze</td><td>.</td><td>Joe</td><td>12/01/2006 01:00 AM</td></tr></table>";
   }

   private Object getHtmlStrWithTitle() {
      return "&nbsp;<font color=\"black\" face=\"Arial\" size=\"-1\"><b>This is the title</b></font><table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\"><tr><th>Event</th><th>State</th><th>Message</th><th>User</th><th>Date</th></tr><tr><td>Originated</td><td>.</td><td>.</td><td>Joe</td><td>12/01/2006 01:00 AM</td></tr><tr><td>StateEntered</td><td>Analyze</td><td>.</td><td>Joe</td><td>12/01/2006 01:00 AM</td></tr></table>";
   }

   public Date getTestDate2011() {
      Calendar cal = Calendar.getInstance();
      Date date = new Date(new Long("1164960000000").longValue());
      cal.setTime(date);
      return date;
   }
}
