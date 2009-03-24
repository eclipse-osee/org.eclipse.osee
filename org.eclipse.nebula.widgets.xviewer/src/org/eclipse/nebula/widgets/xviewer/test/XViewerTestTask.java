/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class XViewerTestTask implements IXViewerTestTask {

   private final RunDb runDb;
   private final TaskType taskType;
   private final String id;
   private final String startTime;
   private final String description;
   private final String category;
   private final String emailAddress;
   private final Date lastRunDate;

   /**
    * 
    */
   public XViewerTestTask(RunDb runDb, TaskType taskType, String id, String startTime, String description, String category, String emailAddress) {
      this(runDb, taskType, new Date(), id, startTime, description, category, emailAddress);
   }

   public XViewerTestTask(RunDb runDb, TaskType taskType, Date lastRunDate, String id, String startTime, String description, String category, String emailAddress) {
      this.runDb = runDb;
      this.taskType = taskType;
      this.lastRunDate = lastRunDate;
      this.id = id;
      this.startTime = startTime;
      this.description = description;
      this.category = category;
      this.emailAddress = emailAddress;
   }

   /**
    * @return the runDb
    */
   public RunDb getRunDb() {
      return runDb;
   }

   /**
    * @return the taskType
    */
   public TaskType getTaskType() {
      return taskType;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the startTime
    */
   public String getStartTime() {
      return startTime;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the category
    */
   public String getCategory() {
      return category;
   }

   /**
    * @return the emailAddress
    */
   public String getEmailAddress() {
      return emailAddress;
   }

   /**
    * @return the lastRunDate
    */
   public Date getLastRunDate() {
      return lastRunDate;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.test.IXViewerTestTask#getLastRunDateStr()
    */
   @Override
   public String getLastRunDateStr() {
      return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(getLastRunDate());
   }

}
