/*
 * Created on Jan 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.autoRun;

import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAutoRunTask {

   public enum RunDb {
      Production_Db, Test_Db
   };

   public enum TaskType {
      Regression, Db_Health, Data_Exchange, Backup
   }

   public void startTasks(XResultData resultData) throws Exception;

   /**
    * @return xx:xx start time
    */
   public String get24HourStartTime();

   public String[] getNotificationEmailAddresses();

   public void setAutoRunUniqueId(String autoRunUniqueId);

   public String getAutoRunUniqueId();

   public TaskType getTaskType();

   public String getDescription();

   public RunDb getRunDb();

   public String getCategory();
}
