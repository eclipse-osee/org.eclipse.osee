/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.test;

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public interface IXViewerTestTask {
   public enum RunDb {
      Production_Db, Test_Db
   };

   public enum TaskType {
      Regression, Db_Health, Data_Exchange, Backup
   }

   public String getStartTime();

   public String getEmailAddress();

   public String getId();

   public TaskType getTaskType();

   public String getDescription();

   public RunDb getRunDb();

   public String getCategory();

   public Date getLastRunDate();

   public String getLastRunDateStr();
}
