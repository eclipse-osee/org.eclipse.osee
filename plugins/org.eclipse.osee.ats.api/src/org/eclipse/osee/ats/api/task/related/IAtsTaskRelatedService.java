/*********************************************************************
 * Copyright (c) 2018 Boeing
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
   DerivedFromTaskData getDerivedTeamWf(DerivedFromTaskData trd);

   /**
    * @param trd with task and derived art
    * @return trd with headArt and latestArt for changeArtifact
    */
   void getRelatedChangedArtifactFromChangeReport(DerivedFromTaskData trd);

   default DerivedFromTaskData getTaskRelatedData(IAtsTask task) {
      return getTaskRelatedData(new DerivedFromTaskData(task));
   }

   /**
    * @param trd with getTask specified
    * @return with all fields filled out and getResults() with errors
    */
   DerivedFromTaskData getTaskRelatedData(DerivedFromTaskData trd);

   /**
    * @return true if AutoGenTaskVer attr is set. This does not mean it's related to a change report artifact.
    */
   boolean isAutoGenTask(IAtsTask task);

   /**
    * @return true if AutoGenTaskVer attr is set. This does not mean it's related to a change report artifact.
    */
   boolean isAutoGenTasks(Collection<IAtsTask> tasks);

   IAutoGenTaskData getAutoGenTaskData(IAtsTask task);

   /**
    * @return true if auto generated task and has related change report artifact attr
    */
   boolean isAutoGenChangeReportRelatedTasks(Collection<IAtsTask> tasks);

   /**
    * @return true if auto generated task and has related change report artifact attr
    */
   boolean isAutoGenChangeReportRelatedTask(IAtsTask task);

}
