/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TimeSummaryData {

   private String elapsed;
   /**
    * @return the elapsed
    */
   public String getElapsed() {
      return elapsed;
   }
   /**
    * @return the endDate
    */
   public String getEndDate() {
      return endDate;
   }
   /**
    * @return the milliseconds
    */
   public String getMilliseconds() {
      return milliseconds;
   }
   /**
    * @return the startDate
    */
   public String getStartDate() {
      return startDate;
   }
   private String endDate;
   private String milliseconds;
   private String startDate;
   /**
    * @param elapsed
    * @param endDate
    * @param milliseconds
    * @param startDate
    */
   TimeSummaryData(String elapsed, String endDate, String milliseconds, String startDate) {
      this.elapsed = elapsed;
      this.endDate = endDate;
      this.milliseconds = milliseconds;
      this.startDate = startDate;
   }

}
