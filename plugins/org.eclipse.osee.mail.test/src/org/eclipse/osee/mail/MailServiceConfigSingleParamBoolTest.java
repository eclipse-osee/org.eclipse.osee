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
public class MailServiceConfigSingleParamBoolTest {
   boolean bool_value;

   public MailServiceConfigSingleParamBoolTest(boolean bool_value) {
      this.bool_value = bool_value;
   }

   @org.junit.Test
   public void testMailMsgBoolAccessors() {
      MailServiceConfig mailsvc = new MailServiceConfig();
      mailsvc.setAuthenticationRequired(bool_value);
      Assert.assertEquals(bool_value, mailsvc.isAuthenticationRequired());
      mailsvc.setDebug(bool_value);
      Assert.assertEquals(bool_value, mailsvc.isDebug());
      mailsvc.setMailStatsEnabled(bool_value);
      Assert.assertEquals(bool_value, mailsvc.isMailStatsEnabled());
   }//test_MailMsg_accessors

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {false});
      data.add(new Object[] {true});

      return data;
   }//getData
}