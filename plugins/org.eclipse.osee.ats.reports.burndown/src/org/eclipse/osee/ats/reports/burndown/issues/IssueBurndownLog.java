/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.issues;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * This class is used to compute the details wrt.the issue burndown.
 * 
 * @author Praveen Joseph
 */
public class IssueBurndownLog {

   private Date startDate;
   private Date endDate;
   private List<AbstractWorkflowArtifact> artifacts;
   private int totalIssues;
   private double avgIdeal;

   private final List<IssueBurndownEntry> entries;

   /**
    * Constructor to initialise list of Work flow artifacts and Log entries
    */
   public IssueBurndownLog() {
      this.artifacts = new ArrayList<>();
      this.entries = new ArrayList<>();
   }

   /**
    * @param artifacts sets the workflow artifacts
    */
   public void setArtifacts(final List<AbstractWorkflowArtifact> artifacts) {
      this.artifacts = artifacts;
   }

   /**
    * @param endDate sets the end date
    */
   public void setEndDate(final Date endDate) {
      this.endDate = endDate;
   }

   /**
    * @param startDate sets the start date
    */
   public void setStartDate(final Date startDate) {
      this.startDate = startDate;
   }

   /**
    * @param totalIssues sets the total issues
    */
   public void setTotalIssues(final int totalIssues) {
      this.totalIssues = totalIssues;
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
    * @return the total issues
    */
   public int getTotalIssues() {
      return this.totalIssues;
   }

   /**
    * @param avgIdeal : sets the average ideal value
    */
   public void setAvgIdeal(final double avgIdeal) {
      this.avgIdeal = avgIdeal;
   }

   /**
    * @return the average ideal value
    */
   public double getAvgIdeal() {
      return this.avgIdeal;
   }

   /**
    * @return the issues list
    */
   public List<IssueBurndownEntry> getEntries() {
      return this.entries;
   }

   /**
    * Method to compute total work done and issue entries
    * 
    * @throws OseeCoreException :
    */
   public void compute() throws OseeCoreException {
      this.totalIssues = computeTotalIssues();
      long days = (this.endDate.getTime() - this.startDate.getTime()) / (1000 * 60 * 60 * 24);
      final double idealAvg = (double) this.totalIssues / days;

      // Day-wise work log
      Date inspectDate = this.startDate;
      int remainingIssues = this.totalIssues;
      double idealRemainingIssues = this.totalIssues;

      // Create 0th entry
      IssueBurndownEntry zeroeth = new IssueBurndownEntry(inspectDate);
      zeroeth.setIssuesClosed(0);
      zeroeth.setIssuesRemaining(remainingIssues);
      zeroeth.setBurndownRate(0);
      zeroeth.setIdealIssuesRemaining(idealRemainingIssues);
      this.entries.add(zeroeth);

      inspectDate = incrementDate(inspectDate, 1);

      while (!inspectDate.after(this.endDate)) {
         int totalIssuesClosed = computeTotalIssues(inspectDate);
         remainingIssues = remainingIssues - totalIssuesClosed;
         idealRemainingIssues -= idealAvg;
         double burndown = (double) remainingIssues / days--;
         IssueBurndownEntry entry = new IssueBurndownEntry(inspectDate);
         entry.setIssuesClosed(totalIssuesClosed);
         entry.setIssuesRemaining(remainingIssues);
         entry.setIdealIssuesRemaining(idealRemainingIssues);
         entry.setBurndownRate(burndown);
         this.entries.add(entry);
         // increment
         inspectDate = incrementDate(inspectDate, 1);

      }

   }

   @SuppressWarnings("static-access")
   private Date incrementDate(final Date inspectDate, final int i) {
      // IncrementDate
      Calendar cal = Calendar.getInstance();
      cal.setTime(inspectDate);
      cal.add(cal.DATE, i);
      Date newDate = cal.getTime();
      return newDate;
   }

   private int computeTotalIssues() {
      return this.artifacts.size();
   }

   /**
    * This method returns the number of issues that were completed on a given date.
    */
   @SuppressWarnings("deprecation")
   private int computeTotalIssues(final Date currentDate) throws OseeCoreException {
      int issues = 0;
      for (AbstractWorkflowArtifact artifact : this.artifacts) {
         if (artifact.isCompleted()) {
            Date itemDate = artifact.getCompletedDate();
            if ((itemDate.getDate() == currentDate.getDate()) && (itemDate.getMonth() == currentDate.getMonth()) && (itemDate.getYear() == currentDate.getYear())) {
               issues++;
            }

         }
      }
      return issues;
   }

}
