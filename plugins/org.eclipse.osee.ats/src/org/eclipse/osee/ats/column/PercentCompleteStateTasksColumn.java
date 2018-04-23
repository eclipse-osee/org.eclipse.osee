/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteStateTasksColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteStateTasksColumn instance = new PercentCompleteStateTasksColumn();

   public static PercentCompleteStateTasksColumn getInstance() {
      return instance;
   }

   private PercentCompleteStateTasksColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateTaskPercentComplete", "State Task Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the tasks related to the current state.\n\nCalculation: total percent of all tasks related to state / number of tasks related to state");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteStateTasksColumn copy() {
      PercentCompleteStateTasksColumn newXCol = new PercentCompleteStateTasksColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(getPercentCompleteFromStateTasks((Artifact) element));
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
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteFromStateTasks((Artifact) team.getStoreObject());
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / AtsClientService.get().getWorkItemService().getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact instanceof TeamWorkFlowArtifact) {
         return getPercentCompleteFromStateTasks(artifact,
            ((TeamWorkFlowArtifact) artifact).getStateMgr().getCurrentState());
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
      return ((TeamWorkFlowArtifact) artifact).getPercentCompleteFromTasks(relatedToState);
   }

}
