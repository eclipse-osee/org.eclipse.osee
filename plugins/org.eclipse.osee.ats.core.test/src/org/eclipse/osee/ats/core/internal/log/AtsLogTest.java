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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsLogTest {

   // @formatter:off
   @Mock IAtsUserService userService; 
   @Mock IAtsUser user;
   // @formatter:on

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      when(user.getName()).thenReturn("joe");
      when(user.getUserId()).thenReturn("joe");

      when(userService.getUserById("joe")).thenReturn(user);
   }

   @Test
   public void testToAndFromStore() throws OseeCoreException {
      Date date = new Date();
      SimpleLogStore store = new SimpleLogStore();
      IAtsLog log = AtsCore.getLogFactory().getLog(store, userService);
      IAtsLogItem item = LogItemTest.getTestLogItem(date, user, userService);
      log.addLogItem(item);

      IAtsLog log2 = AtsCore.getLogFactory().getLog(store, userService);
      Assert.assertEquals(1, log2.getLogItems().size());
      IAtsLogItem loadItem = log2.getLogItems().iterator().next();
      LogItemTest.validateItem(user, loadItem, date);
   }

   public class SimpleLogStore implements ILogStorageProvider {

      String store = "";

      @Override
      public String getLogXml() {
         return store;
      }

      @Override
      public IStatus saveLogXml(String xml) {
         store = xml;
         return Status.OK_STATUS;
      }

      @Override
      public String getLogTitle() {
         return "This is the title";
      }

      @Override
      public String getLogId() {
         return GUID.create();
      }

   }

}
