/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.hours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * This is the class responsible for constructing the burndown logs based on the Artifact data. The burndown logs
 * constructed is in the form of a list of HourBurndownEntry objects.
 * 
 * @author Praveen Joseph
 */
public class HourBurndownLog {

   private Date startDate;
   private Date endDate;
   private List<AbstractWorkflowArtifact> artifacts;
   private double totalWork;
   private double avgIdeal;

   private final List<HourBurndownEntry> entries;

   /**
    * Constructor to initialise list of Work flow artifacts and Log entries
    */
   public HourBurndownLog() {
      this.artifacts = new ArrayList<>();
      this.entries = new ArrayList<>();
   }

   /**
    * @param artifacts : sets the list of work flow artifacts
    */
   public void setArtifacts(final List<AbstractWorkflowArtifact> artifacts) {
      this.artifacts = artifacts;
   }

   /**
    * @param endDate : sets the end date
    */
   public void setEndDate(final Date endDate) {
      this.endDate = endDate;
   }

   /**
    * @param startDate : sets the start date
    */
   public void setStartDate(final Date startDate) {
      this.startDate = startDate;
   }

   /**
    * @param totalWork : sets the total work done
    */
   public void setTotalWork(final double totalWork) {
      this.totalWork = totalWork;
   }

   /**
    * @return the list of workflow artifacts
    */
   public List<AbstractWorkflowArtifact> getArtifacts() {
      return this.artifacts;
   }

   /**
    * @return the end date
    */
   public Date getEndDate() {
      return this.endDate;
   }

   /**
    * @return the start date
    */
   public Date getStartDate() {
      return this.startDate;
   }

   /**
    * @return the total work
    */
   public double getTotalWork() {
      return this.totalWork;
   }

   /**
    * @param avgIdeal : sets average work hours
    */
   public void setAvgIdeal(final double avgIdeal) {
      this.avgIdeal = avgIdeal;
   }

   /**
    * @return average work hours
    */
   public double getAvgIdeal() {
      return this.avgIdeal;
   }

   /**
    * @return the entries
    */
   public List<HourBurndownEntry> getEntries() {
      return this.entries;
   }

   /**
    * Method to compute the total work and add hourburndown log entries
    * 
    * @throws OseeCoreException
    */
   public void compute() throws OseeCoreException {
      this.totalWork = computeTotalWork();
      long days = (this.endDate.getTime() - this.startDate.getTime()) / (1000 * 60 * 60 * 24);
      final double idealAvg = this.totalWork / days;

      // Day-wise work log
      Date inspectDate = this.startDate;
      double remainingWork = this.totalWork;
      double idealRemainingWork = this.totalWork;

      // Create 0th entry
      HourBurndownEntry zeroeth = new HourBurndownEntry(inspectDate);
      zeroeth.setHoursWorked(0);
      zeroeth.setHoursRemaining(remainingWork);
      zeroeth.setBurndownRate(0);
      zeroeth.setIdealHoursRemaining(idealRemainingWork);
      this.entries.add(zeroeth);

      inspectDate = incrementDate(inspectDate, 1);

      while (!inspectDate.after(this.endDate)) {
         double totalWorkDay = computeTotalWork(inspectDate);
         remainingWork = remainingWork - totalWorkDay;
         idealRemainingWork -= idealAvg;
         double burndown = remainingWork / days--;
         HourBurndownEntry entry = new HourBurndownEntry(inspectDate);
         entry.setHoursWorked(totalWorkDay);
         entry.setHoursRemaining(remainingWork);
         entry.setIdealHoursRemaining(idealRemainingWork);
         entry.setBurndownRate(burndown);
         this.entries.add(entry);
         // increment
         inspectDate = incrementDate(inspectDate, 1);

      }

   }

   private Date incrementDate(final Date inspectDate, final int i) {
      // IncrementDate
      Calendar cal = Calendar.getInstance();
      cal.setTime(inspectDate);
      cal.add(Calendar.DATE, i);
      Date newDate = cal.getTime();
      return newDate;
   }

   /**
    * This method computes the total work to be done from the list of available artifacts
    */
   private double computeTotalWork() throws OseeCoreException {
      double work = 0;
      for (AbstractWorkflowArtifact artifact : this.artifacts) {
         work += artifact.getEstimatedHoursTotal();
      }
      return work;
   }

   /**
    * This method computes the total work done on a given date.
    */
   @SuppressWarnings("deprecation")
   private double computeTotalWork(final Date currentDate) throws OseeCoreException {
      double work = 0;
      for (AbstractWorkflowArtifact artifact : this.artifacts) {
         double previousWork = 0;
         IAtsLog log = artifact.getLog();
         List<IAtsLogItem> logItems = log.getLogItems();
         for (IAtsLogItem item : logItems) {
            if (item.getType() == LogType.Metrics) {
               double itemWork = ReportUtil.getWork(item);
               Date itemDate = item.getDate();
               if ((itemDate.getDate() == currentDate.getDate()) && (itemDate.getMonth() == currentDate.getMonth()) && (itemDate.getYear() == currentDate.getYear())) {
                  work += itemWork - previousWork;
               }
               previousWork = itemWork;
            }
         }
      }
      return work;
   }

}
