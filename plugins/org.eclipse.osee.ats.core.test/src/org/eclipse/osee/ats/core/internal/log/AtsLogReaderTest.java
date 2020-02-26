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
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AtsLogReader}
 * 
 * @author Donald G. Dunne
 */
public class AtsLogReaderTest {

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
      AtsLogReader reader = new AtsLogReader(log, storeProvider);
      reader.load();
      List<IAtsLogItem> logItems = log.getLogItems();
      Assert.assertEquals(0, logItems.size());
   }

   @Test
   public void testLoad() {
      when(storeProvider.getLogXml()).thenReturn(getLogXml());
      AtsLogReader reader = new AtsLogReader(log, storeProvider);
      reader.load();
      List<IAtsLogItem> logItems = log.getLogItems();
      Assert.assertEquals(2, logItems.size());
      Assert.assertEquals("", logItems.get(0).getState());
      Assert.assertEquals(LogType.Originated, logItems.get(0).getType());
      Assert.assertEquals("Analyze", logItems.get(1).getState());
      Assert.assertEquals(LogType.StateEntered, logItems.get(1).getType());
   }

   private String getLogXml() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog><Item date=\"1385136355104\" msg=\"\" state=\"\" type=\"Originated\" userId=\"456\"/><Item date=\"1385136355104\" msg=\"\" state=\"Analyze\" type=\"StateEntered\" userId=\"456\"/></AtsLog>";
   }
}
