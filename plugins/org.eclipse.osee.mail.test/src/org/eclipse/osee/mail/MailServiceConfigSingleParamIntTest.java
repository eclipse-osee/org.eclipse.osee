/*
 * Created on Jun 6, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
