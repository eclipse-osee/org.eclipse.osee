/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.reports.burndown.hours;

import java.util.Calendar;
import java.util.Date;

/**
 * Model class to store the data for a single date.
 * 
 * @author Praveen Joseph
 */
public class HourBurndownEntry {

   private final Date date;
   private double hoursWorked;
   private double hoursRemaining;
   private boolean actualData;
   private double idealHoursRemaining;
   private double burndownRate;

   /**
    * @param date : Sets the Current date
    */
   public HourBurndownEntry(final Date date) {
      this.date = date;
      Date currentTime = Calendar.getInstance().getTime();
      // The variable "actualData" is used to indicate if the data calculated is based on the actual work done on a given
      // date,
      // or if it is calculated based on assumptions (ie. If the date occurs sometime in the future).

      if (currentTime.after(date)) {
         this.actualData = true;
      } else {
         this.actualData = false;
      }
   }

   /**
    * @param actualData : sets the current date
    */
   public void setActualData(final boolean actualData) {
      this.actualData = actualData;
   }

   /**
    * @param hoursRemaining : sets the hours remaining
    */
   public void setHoursRemaining(final double hoursRemaining) {
      this.hoursRemaining = hoursRemaining;
   }

   /**
    * @param hoursWorked : sets the hours worked
    */
   public void setHoursWorked(final double hoursWorked) {
      this.hoursWorked = hoursWorked;
   }

   /**
    * @return the hours remaining
    */
   public double getHoursRemaining() {
      return this.hoursRemaining;
   }

   /**
    * @return todays date
    */
   public Date getDate() {
      return this.date;
   }

   /**
    * @return the hours worked
    */
   public double getHoursWorked() {
      return this.hoursWorked;
   }

   /**
    * @return actual data
    */
   public boolean isActualData() {
      return this.actualData;
   }

   /**
    * @return ideal Hours Remaining
    */
   public double getIdealHoursRemaining() {
      return this.idealHoursRemaining;
   }

   /**
    * @param idealHoursRemaining : sets the ideal Hours Remaining
    */
   public void setIdealHoursRemaining(final double idealHoursRemaining) {
      this.idealHoursRemaining = idealHoursRemaining;
   }

   /**
    * @return burndownRate
    */
   public double getBurndownRate() {
      return this.burndownRate;
   }

   /**
    * @param burndownRate : sets the burndownRate
    */
   public void setBurndownRate(final double burndownRate) {
      this.burndownRate = burndownRate;
   }

}
