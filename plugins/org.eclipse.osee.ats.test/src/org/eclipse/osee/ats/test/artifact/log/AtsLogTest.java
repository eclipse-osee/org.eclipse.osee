/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.artifact.log;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.log.AtsLog;
import org.eclipse.osee.ats.artifact.log.ILogStorageProvider;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.junit.Test;

public class AtsLogTest {

   @Test
   public void testToAndFromStore() throws OseeCoreException {
      Date date = new Date();
      SimpleLogStore store = new SimpleLogStore();
      AtsLog log = new AtsLog(store);
      LogItem item = LogItemTest.getTestLogItem(date);
      log.addLogItem(item);

      AtsLog log2 = new AtsLog(store);
      Assert.assertEquals(1, log2.getLogItems().size());
      LogItem loadItem = log2.getLogItems().iterator().next();
      LogItemTest.validateItem(loadItem, date);
   }

   public class SimpleLogStore implements ILogStorageProvider {

      String store = "";

      @Override
      public String getLogXml() {
         return store;
      }

      @Override
      public Result saveLogXml(String xml) {
         store = xml;
         return Result.TrueResult;
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
