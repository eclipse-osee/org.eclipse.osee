/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.related;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.IAtsTask;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskRelatedService {

   public static final String IMPL_DETAILS = " (Impl Details)";
   public static final String DELETED = " (Deleted)";

   /**
    * @param trd with getTask specified
    * @return with derivedTeamWf and isDerived flag.
    */
   TaskRelatedData getDerivedTeamWf(TaskRelatedData trd);

   /**
    * @param trd with task and derived art
    * @return trd with headArt and latestArt for changeArtifact
    */
   void getRelatedChangedArtifactFromChangeReport(TaskRelatedData trd);

   default TaskRelatedData getTaskRelatedData(IAtsTask task) {
      return getTaskRelatedData(new TaskRelatedData(task));
   }

   /**
    * @param trd with getTask specified
    * @return with all fields filled out and getResults() with errors
    */
   TaskRelatedData getTaskRelatedData(TaskRelatedData trd);

   boolean isAutoGenCodeTestTaskArtifact(IAtsTask task);

   boolean isAutoGenCodeTestTaskArtifacts(Collection<IAtsTask> tasks);

}
