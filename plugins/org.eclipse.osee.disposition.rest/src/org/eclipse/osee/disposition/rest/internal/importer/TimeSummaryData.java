/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.internal.importer;

/**
 * @author Andrew M. Finkbeiner
 */
public class TimeSummaryData {

   private final String elapsed;

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
   private final String endDate;
   private final String milliseconds;
   private final String startDate;

   TimeSummaryData(String elapsed, String endDate, String milliseconds, String startDate) {
      this.elapsed = elapsed;
      this.endDate = endDate;
      this.milliseconds = milliseconds;
      this.startDate = startDate;
   }

}
