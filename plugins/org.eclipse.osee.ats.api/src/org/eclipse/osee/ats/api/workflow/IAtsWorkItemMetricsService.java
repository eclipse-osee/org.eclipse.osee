/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.api.workflow;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemMetricsService {

   void updateMetrics(IAtsWorkItem workItem, IStateToken state, double additionalHours, int percentComplete, boolean logMetrics, AtsUser user, IAtsChangeSet changes);

   void setMetrics(IAtsWorkItem workItem, double hours, int percentComplete, boolean logMetrics, AtsUser user, Date date, IAtsChangeSet changes);

   void setHoursSpent(IAtsWorkItem workItem, double hoursSpent, IAtsChangeSet changes);

   Integer getPercentComplete(IAtsWorkItem workItem);

   void setPercentComplete(IAtsWorkItem workItem, Integer percentComplete, IAtsChangeSet changes);

   void logMetrics(IAtsWorkItem workItem, IStateToken state, AtsUser user, Date date, IAtsChangeSet changes);

   void logMetrics(IAtsWorkItem workItem, String percent, String hours, IStateToken state, AtsUser user, Date date, IAtsChangeSet changes);

   double getHoursSpent(IAtsWorkItem workItem);

   double getHoursSpentTotal(IAtsObject atsObject);

   double getHoursSpentReview(IAtsObject atsObject);

   double getHoursSpentFromTasks(IAtsObject atsObject);

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    */
   int getPercentCompleteSMAStateTotal(IAtsObject atsObject, IStateToken state, AtsApi atsApi);

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/canceled)
    */
   int getPercentCompleteTotal(IAtsObject atsObject);

}
