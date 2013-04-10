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
package org.eclipse.osee.ats.client.integration.tests.ats.artifact.log;

import java.util.Date;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.workflow.log.LogItem;
import org.eclipse.osee.ats.core.client.workflow.log.LogType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class LogItemTest {

   @Test
   public void testLogItemLogTypeDateUserStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      validateItem(item, date);
   }

   public static void validateItem(LogItem item, Date date) throws OseeCoreException {
      Assert.assertEquals(LogType.Error, item.getType());
      Assert.assertEquals(date, item.getDate());
      Assert.assertEquals(AtsClientService.get().getUserAdmin().getCurrentUser(), item.getUser());
      Assert.assertEquals(AtsClientService.get().getUserAdmin().getCurrentUser().getUserId(), item.getUserId());
      Assert.assertEquals("Analyze", item.getState());
      Assert.assertEquals("my msg", item.getMsg());
   }

   @Test
   public void testLogItemLogTypeStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item =
         new LogItem(LogType.Error, String.valueOf(date.getTime()), AtsClientService.get().getUserAdmin().getCurrentUser().getUserId(), "Analyze",
            "my msg", "ASDF4");

      validateItem(item, date);
   }

   @Test
   public void testLogItemStringStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item =
         new LogItem(LogType.Error.name(), String.valueOf(date.getTime()), AtsClientService.get().getUserAdmin().getCurrentUser().getUserId(),
            "Analyze", "my msg", "ASDF4");

      validateItem(item, date);
   }

   public static LogItem getTestLogItem(Date date) throws OseeCoreException {
      return new LogItem(LogType.Error, date, AtsClientService.get().getUserAdmin().getCurrentUser(), "Analyze", "my msg", "ASDF4");
   }

   @Test
   public void testToString() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals(
         "my msg (Error)from Analyze by " + AtsClientService.get().getUserAdmin().getCurrentUser().getName() + " on " + DateUtil.getMMDDYYHHMM(date),
         item.toString());
   }

   @Test
   public void testToHTML() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals("NOTE (Error): my msg (" + AtsClientService.get().getUserAdmin().getCurrentUser().getName() + ")",
         item.toHTML(AHTML.LABEL_FONT));
   }

}
