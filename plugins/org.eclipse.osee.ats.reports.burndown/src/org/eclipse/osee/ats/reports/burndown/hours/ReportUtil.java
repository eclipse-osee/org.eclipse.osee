/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.hours;

import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;

/**
 * Utility Class used during the report data generation
 * 
 * @author Praveen Joseph
 */
public class ReportUtil {

   /**
    * This method returns the amount of work with respect to a given work item
    * 
    * @param item The {@link IAtsLogItem} instance
    * @return the work done
    */
   public static double getWork(final IAtsLogItem item) {
      if (item.getType() == LogType.Metrics) {
         String msg = item.getMsg();
         String[] segments = msg.split(" ");
         String workString = segments[segments.length - 1];
         return Double.parseDouble(workString);
      }
      return 0;
   }

}
