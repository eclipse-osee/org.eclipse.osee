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
public class MailServiceConfigSetToTest {
   private final String host;
   private final String pword;
   private final String sysAdminEmail;
   private final String transport;
   private final String username;
   private final int port;

   public MailServiceConfigSetToTest(String host, String pword, int port, String sysAdminEmail, String transport, String username) {
      this.host = host;
      this.pword = pword;
      this.port = port;
      this.sysAdminEmail = sysAdminEmail;
      this.transport = transport;
      this.username = username;
   }

   @org.junit.Test
   public void testMailMsgSetTo() {
      MailServiceConfig mailsvc = new MailServiceConfig();
      MailServiceConfig mailsvc_to = new MailServiceConfig();

      mailsvc_to.setHost(host);
      mailsvc_to.setPassword(pword);
      mailsvc_to.setPort(port);
      mailsvc_to.setSystemAdminEmailAddress(sysAdminEmail);
      mailsvc_to.setTransport(transport);
      mailsvc_to.setUserName(username);
      mailsvc.setTo(mailsvc_to);
      Assert.assertEquals(mailsvc_to.getHost(), mailsvc.getHost());
      Assert.assertEquals(mailsvc_to.getPassword(), mailsvc.getPassword());
      Assert.assertEquals(mailsvc_to.getPort(), mailsvc.getPort());
      Assert.assertEquals(mailsvc_to.getSystemAdminEmailAddress(), mailsvc.getSystemAdminEmailAddress());
      Assert.assertEquals(mailsvc_to.getTransport(), mailsvc.getTransport());
      Assert.assertEquals(mailsvc_to.getUserName(), mailsvc.getUserName());

   }//test_MailMsg_accessors

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {"This is a host value.",//host
         "This is a pword value.",//pword
         0,//port
         "This is a sysAdminEmail value.",//sysAdminEmail
         "This is a transport value.",//transport
         "This is a username value."});//username

      data.add(new Object[] {"such.and.such.somewhere.com",//host
         "th1$_!S_@_p#$$w0)d",//pword
         25,//port
         "so.and.so@such-and-such.org",//sysAdminEmail
         "smtp",//transport
         "joe.schmoe"});//username

      data.add(new Object[] {"1.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",//host
         "2.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",//pword
         32000,//port
         "3.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",//sysAdminEmail
         "4.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",//transport
         "5.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./"});//username

      //generate a very long string
      String aLongString1 = "1.";
      String aLongString2 = "2.";
      String aLongString3 = "3.";
      String aLongString4 = "4.";
      String aLongString5 = "5.";
      //make a 1000 char string.
      for (int i = 0; i < 1000; i++) {
         aLongString1 += "x";
         aLongString2 += "x";
         aLongString3 += "x";
         aLongString4 += "x";
         aLongString5 += "x";
      }
      data.add(new Object[] {aLongString1, aLongString2, 49151, aLongString3, aLongString4, aLongString5});

      return data;
   }//getData
}