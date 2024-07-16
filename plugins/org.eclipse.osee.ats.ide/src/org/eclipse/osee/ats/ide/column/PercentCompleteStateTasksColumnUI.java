/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteStateTasksColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteStateTasksColumnUI instance = new PercentCompleteStateTasksColumnUI();

   public static PercentCompleteStateTasksColumnUI getInstance() {
      return instance;
   }

   private PercentCompleteStateTasksColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateTaskPercentComplete", "State Task Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the tasks related to the current state.\n\nCalculation: total percent of all tasks related to state / number of tasks related to state");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteStateTasksColumnUI copy() {
      PercentCompleteStateTasksColumnUI newXCol = new PercentCompleteStateTasksColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(
               getPercentCompleteFromStateTasks(AtsApiService.get().getQueryServiceIde().getArtifact(element)));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromStateTasks(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double percent = 0;
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteFromStateTasks(AtsApiService.get().getQueryServiceIde().getArtifact(team));
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / AtsApiService.get().getWorkItemService().getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact instanceof TeamWorkFlowArtifact) {
         return getPercentCompleteFromStateTasks(artifact, ((TeamWorkFlowArtifact) artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromStateTasks(Artifact artifact, IStateToken relatedToState) {
      if (!(artifact instanceof TeamWorkFlowArtifact)) {
         return 0;
      }
      return AtsApiService.get().getEarnedValueService().getPercentCompleteFromTasks((TeamWorkFlowArtifact) artifact,
         relatedToState);
   }

}
