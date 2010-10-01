/*
 * Created on Sep 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.artifact.log;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Test;

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
      Assert.assertEquals(UserManager.getUser(), item.getUser());
      Assert.assertEquals(UserManager.getUser().getUserId(), item.getUserId());
      Assert.assertEquals("Analyze", item.getState());
      Assert.assertEquals("my msg", item.getMsg());
   }

   @Test
   public void testLogItemLogTypeStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item =
         new LogItem(LogType.Error, String.valueOf(date.getTime()), UserManager.getUser().getUserId(), "Analyze",
            "my msg", "ASDF4");

      validateItem(item, date);
   }

   @Test
   public void testLogItemStringStringStringStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item =
         new LogItem(LogType.Error.name(), String.valueOf(date.getTime()), UserManager.getUser().getUserId(),
            "Analyze", "my msg", "ASDF4");

      validateItem(item, date);
   }

   public static LogItem getTestLogItem(Date date) throws OseeCoreException {
      return new LogItem(LogType.Error, date, UserManager.getUser(), "Analyze", "my msg", "ASDF4");
   }

   @Test
   public void testToString() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals(
         "my msg (Error)from Analyze by " + UserManager.getUser().getName() + " on " + DateUtil.getMMDDYYHHMM(date),
         item.toString());
   }

   @Test
   public void testToHTML() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals("NOTE (Error): my msg (" + UserManager.getUser().getName() + ")",
         item.toHTML(AHTML.LABEL_FONT));
   }

}
