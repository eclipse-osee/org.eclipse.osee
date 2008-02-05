package org.eclipse.osee.framework.ui.skynet.autoRun;

import java.util.Calendar;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleAutoRunTasks {

   private Scheduler getSchedulerReference() throws SchedulerException {
      SchedulerFactory sf = new StdSchedulerFactory();
      return sf.getScheduler();
   }

   public void run() throws Exception {

      try {
         // Tell quartz to schedule the job using our trigger
         Scheduler scheduler = getSchedulerReference();

         // TESTING
         // scheduleDailyJob(scheduler, "Hello World", HelloJob.class, 22,
         // 29, true);
         // GregorianCalendar cal = new GregorianCalendar();

         // Schedule simple Hello World example
         // scheduleDailyJob(scheduler, "Hello World",
         // SchedulableHelloAndEmailJob.class,
         // cal.get(GregorianCalendar.HOUR_OF_DAY),
         // cal.get(GregorianCalendar.MINUTE) + 1, true);

         // Schedule test populate trax ui job for 1 minute from now
         // scheduleDailyJob(scheduler, UpdateBuildPlanning.JOB_NAME,
         // SchedulableUpdateBuiltPlanningJob.class,
         // cal.get(GregorianCalendar.HOUR_OF_DAY), cal.get(GregorianCalendar.MINUTE) + 1, true);

         // Jobs and times to schedule
         // scheduleDailyJob(scheduler, PopulateTraxUICountJob.JOB_NAME,
         // SchedulablePopulateTraxUICountJob.class, 00, 02,
         // false);
         // scheduleDailyJob(scheduler, PopulateTraxMextricsJob.JOB_NAME,
         // SchedulablePopulateTraxMetricsJob.class, 00, 32,
         // false);
         // scheduleDailyJob(scheduler, UpdateBuildPlanning.JOB_NAME,
         // SchedulableUpdateBuildPlanningJob.class, 01, 02,
         //               false);

         // Start up the scheduler (nothing can actually run until the
         // scheduler has been started)
         scheduler.start();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      // try {
      // Thread.sleep(90L * 1000L);
      // }
      // catch (Exception e) {
      // }
      // scheduler.shutdown(true);
   }

   /**
    * Schedule a daily job to run at hour:minute
    * 
    * @param scheduler
    * @param name
    * @param clazz
    * @param hour
    * @param minute
    * @param startToday true if want job to start running today. if hour:minute already passed, job will start
    *           immediately
    * @throws Exception
    */
   public void scheduleDailyJob(Scheduler scheduler, String name, Class<? extends Job> clazz, int hour, int minute, boolean startToday) throws Exception {
      // define the job and tie it to our HelloJob class
      JobDetail jobDetail = new JobDetail(name, name, clazz);

      // compute a time that is on the next round minute
      // and Trigger the job to run on the next round minute
      // Date runTime = TriggerUtils.getEvenMinuteDate(new Date());
      // SimpleTrigger trigger = new SimpleTrigger(name, name, runTime);

      Calendar cal = new java.util.GregorianCalendar();
      // If start today, make day of month today; else add a day
      cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + (startToday ? 0 : 1),
            hour, minute);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      System.out.println("Scheduling " + clazz + " for " + XDate.getDateStr(cal.getTime(), XDate.MMDDYYHHMM) + ".");
      SimpleTrigger trigger =
            new SimpleTrigger(name + "trigger", null, cal.getTime(), null, 5, 24L * 60L * 60L * 1000L);

      scheduler.deleteJob(name, name);
      scheduler.scheduleJob(jobDetail, trigger);
   }

   public static void main(String[] args) throws Exception {
      ScheduleAutoRunTasks example = new ScheduleAutoRunTasks();
      example.run();
   }
}
