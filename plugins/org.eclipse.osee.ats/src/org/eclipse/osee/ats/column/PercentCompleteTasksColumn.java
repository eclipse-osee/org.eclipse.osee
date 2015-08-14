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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteTasksColumn instance = new PercentCompleteTasksColumn();

   public static PercentCompleteTasksColumn getInstance() {
      return instance;
   }

   private PercentCompleteTasksColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".taskPercentComplete", "Task Percent Complete", 40, SWT.CENTER,
         false, SortDataType.Percent, false,
         "Percent Complete for the tasks related to the workflow.\n\nCalculation: total percent of all tasks / number of tasks");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteTasksColumn copy() {
      PercentCompleteTasksColumn newXCol = new PercentCompleteTasksColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(getPercentCompleteFromTasks((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromTasks(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double percent = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteFromTasks(team);
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / ActionManager.getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact instanceof AbstractTaskableArtifact) {
         return ((AbstractTaskableArtifact) artifact).getPercentCompleteFromTasks();
      }
      return 0;
   }

}
