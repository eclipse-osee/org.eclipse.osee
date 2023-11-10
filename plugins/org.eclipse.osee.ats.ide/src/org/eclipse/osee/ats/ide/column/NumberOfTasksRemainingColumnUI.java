/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class NumberOfTasksRemainingColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static NumberOfTasksRemainingColumnUI instance = new NumberOfTasksRemainingColumnUI();

   public static NumberOfTasksRemainingColumnUI getInstance() {
      return instance;
   }

   private NumberOfTasksRemainingColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".numberOfTasksRemain", "Number of Tasks Remaining", 40,
         XViewerAlign.Center, false, SortDataType.Integer, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NumberOfTasksRemainingColumnUI copy() {
      NumberOfTasksRemainingColumnUI newXCol = new NumberOfTasksRemainingColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(", ", strs);
         }
         if (element instanceof TeamWorkFlowArtifact) {
            int num = getNumTasksInWork((TeamWorkFlowArtifact) element);
            if (num == 0) {
               return "";
            }
            return String.valueOf(num);
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   public int getNumTasksInWork(TeamWorkFlowArtifact teamWf) {
      int num = 0;
      try {
         for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWf)) {
            if (task.isInWork()) {
               num++;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return num;
   }

}
