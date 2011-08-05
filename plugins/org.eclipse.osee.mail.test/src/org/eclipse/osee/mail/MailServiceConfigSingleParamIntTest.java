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
public class MailServiceConfigSingleParamIntTest {
   int int_value;

   public MailServiceConfigSingleParamIntTest(int int_value) {
      this.int_value = int_value;
   }

   @org.junit.Test
   public void testMailMsgIntAccessors() {
      MailServiceConfig mailsvc = new MailServiceConfig();
      mailsvc.setPort(int_value);
      Assert.assertEquals(int_value, mailsvc.getPort());
   }//test_MailMsg_accessors

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {0});
      data.add(new Object[] {-1234});
      data.add(new Object[] {999999});
      data.add(new Object[] {-8376346});

      return data;
   }//getData
}
