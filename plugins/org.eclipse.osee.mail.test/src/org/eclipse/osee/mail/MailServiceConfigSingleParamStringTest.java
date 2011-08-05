/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mail;

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailServiceConfig}
 * 
 * @author Shawn F. cook
 */
@RunWith(Parameterized.class)
public class MailServiceConfigSingleParamStringTest {
   String str_value;

   public MailServiceConfigSingleParamStringTest(String str_value) {
      this.str_value = str_value;
   }

   @org.junit.Test
   public void testMailMsgStringAccessors() {
      MailServiceConfig mailsvc = new MailServiceConfig();

      mailsvc.setHost(str_value);
      Assert.assertEquals(str_value, mailsvc.getHost());
      mailsvc.setPassword(str_value);
      Assert.assertEquals(str_value, mailsvc.getPassword());
      mailsvc.setSystemAdminEmailAddress(str_value);
      Assert.assertEquals(str_value, mailsvc.getSystemAdminEmailAddress());
      mailsvc.setTransport(str_value);
      Assert.assertEquals(str_value, mailsvc.getTransport());
      mailsvc.setUserName(str_value);
      Assert.assertEquals(str_value, mailsvc.getUserName());

   }//test_MailMsg_accessors

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"One new string value."});
      data.add(new Object[] {"joe.schmoe@somedomain.com"});
      data.add(new Object[] {"Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./"});

      //generate a very long string
      String aLongString = "";
      //make a 1000 char string.
      for (int i = 0; i < 1000; i++) {
         aLongString += "x";
      }
      data.add(new Object[] {aLongString});

      return data;
   }//getData
}
