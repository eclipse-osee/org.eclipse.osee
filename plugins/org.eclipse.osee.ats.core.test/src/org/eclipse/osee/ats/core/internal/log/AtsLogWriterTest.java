/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.log;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AtsLogWriter}
 *
 * @author Donald G. Dunne
 */
public class AtsLogWriterTest {

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   @Mock IAttributeResolver attrResolver;
   @Mock AtsUser Joe;
   @Mock IAtsChangeSet changes;
   @Mock ILogStorageProvider storeProvider;
   // @formatter:on
   private AtsLog log;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      log = new AtsLog();

      when(workItem.getLog()).thenReturn(log);
      when(storeProvider.getLogId()).thenReturn("logId");
   }

   @Test
   public void testEmpty() {
      when(storeProvider.getLogXml()).thenReturn("");
      AtsLogWriter writer = new AtsLogWriter(log, storeProvider);
      when(storeProvider.saveLogXml(any(String.class), any(IAtsChangeSet.class))).thenReturn(Result.TrueResult);
      writer.save(changes);
      verify(storeProvider).saveLogXml(getEmptyLog(), changes);
   }

   private String getEmptyLog() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog/>";
   }

   @Test
   public void testSave() {
      IAtsLogItem item = log.addLog(LogType.Originated, "", "", "456");
      Date testDate2011 = getTestDate2011();
      item.setDate(testDate2011);
      item = log.addLog(LogType.StateEntered, "Analyze", "", "456");
      item.setDate(testDate2011);
      AtsLogWriter writer = new AtsLogWriter(log, storeProvider);
      when(storeProvider.saveLogXml(any(String.class), any(IAtsChangeSet.class))).thenReturn(Result.TrueResult);
      writer.save(changes);
      verify(storeProvider).saveLogXml(getLogStr(), changes);
   }

   private String getLogStr() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog><Item date=\"1164960000000\" msg=\"\" state=\"\" type=\"Originated\" userId=\"456\"/><Item date=\"1164960000000\" msg=\"\" state=\"Analyze\" type=\"StateEntered\" userId=\"456\"/></AtsLog>";
   }

   public Date getTestDate2011() {
      Calendar cal = Calendar.getInstance();
      Date date = new Date(Long.valueOf("1164960000000").longValue());
      cal.setTime(date);
      return date;
   }
}
