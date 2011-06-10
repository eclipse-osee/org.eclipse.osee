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