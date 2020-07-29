/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.api.task;

import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskProvider {

   IAutoGenTaskData getAutoGenTaskData(String autoGenVerStr, IAtsTask task);

   /**
    * Add additional attributes to taskMatch if this task is applicable to this provider
    */
   void addToAutoGeneratingTask(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch, JaxAtsTask task);

   boolean isAutoGen(IAtsTask task);

   /**
    * @return AutoGenVersion to set if this is task that is applicable to this provider
    */
   AutoGenVersion getAutoGenTaskVersionToSet(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch);

}
