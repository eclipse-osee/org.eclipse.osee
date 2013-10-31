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
package org.eclipse.osee.ats.core.internal.log;

import static org.mockito.Mockito.when;
import java.util.Date;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class LogItemTest {

   // @formatter:off
   @Mock IAtsUser user;
   // @formatter:on

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      when(user.getName()).thenReturn("joe");
      when(user.getUserId()).thenReturn("joe");

   }

   @Test
   public void testLogItemLogTypeDateUserStringStringString() throws OseeCoreException {
      Date date = new Date();
      IAtsLogItem item = getTestLogItem(date, user);

      validateItem(user, item, date);
   }

   public static void validateItem(IAtsUser user, IAtsLogItem item, Date date) throws OseeCoreException {
      Assert.assertEquals(LogType.Error, item.getType());
      Assert.assertEquals(date, item.getDate());
      Assert.assertEquals(user.getUserId(), item.getUserId());
      Assert.assertEquals("Analyze", item.getState());
      Assert.assertEquals("my msg", item.getMsg());
   }

   @Test
   public void testLogItemLogTypeStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      IAtsLogItem item =
         new LogItem(LogType.Error, String.valueOf(date.getTime()), user.getUserId(), "Analyze", "my msg");

      validateItem(user, item, date);
   }

   @Test
   public void testLogItemStringStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      IAtsLogItem item =
         new LogItem(LogType.Error.name(), String.valueOf(date.getTime()), user.getUserId(), "Analyze", "my msg");

      validateItem(user, item, date);
   }

   public static IAtsLogItem getTestLogItem(Date date, IAtsUser user) throws OseeCoreException {
      return new LogItem(LogType.Error, date, user.getUserId(), "Analyze", "my msg");
   }

   @Test
   public void testToString() throws OseeCoreException {
      Date date = new Date();
      IAtsLogItem item = getTestLogItem(date, user);

      Assert.assertEquals("my msg (Error)from Analyze by " + user.getName() + " on " + DateUtil.getMMDDYYHHMM(date),
         item.toString());
   }

}
