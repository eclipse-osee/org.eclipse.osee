/*
 * Created on Sep 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.artifact;

import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Test;

public class LogItemTest {

   @Test
   public void testLogItemLogTypeDateUserStringStringString() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      validateItem(item, date);
   }

   private void validateItem(LogItem item, Date date) throws OseeCoreException {
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

   @Test
   public void testToXml() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      String xml = item.toXml();
      List<LogItem> items = LogItem.getLogItems(xml, "ASDF4");
      Assert.assertEquals(1, items.size());
      LogItem loadItem = items.iterator().next();
      validateItem(loadItem, date);
   }

   private LogItem getTestLogItem(Date date) throws OseeCoreException {
      return new LogItem(LogType.Error, date, UserManager.getUser(), "Analyze", "my msg", "ASDF4");
   }

   @Test
   public void testToString() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals("asdf", item.toString());
   }

   @Test
   public void testToHTML() throws OseeCoreException {
      Date date = new Date();
      LogItem item = getTestLogItem(date);

      Assert.assertEquals("asdf", item.toHTML(AHTML.LABEL_FONT));
   }

}
