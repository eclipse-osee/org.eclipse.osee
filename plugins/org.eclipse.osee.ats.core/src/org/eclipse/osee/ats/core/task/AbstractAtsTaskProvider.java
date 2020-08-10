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
package org.eclipse.osee.ats.core.task;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsTaskProvider implements IAtsTaskProvider {

   protected AtsApi atsApi;

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public boolean isAutoGen(IAtsTask task) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.TaskAutoGen, false);
   }

   @Override
   public void addToAutoGeneratingTask(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch, JaxAtsTask task) {
      // do nothing
   }

}
