/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class NumberOfTasksColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static NumberOfTasksColumn instance = new NumberOfTasksColumn();

   public static NumberOfTasksColumn getInstance() {
      return instance;
   }

   private NumberOfTasksColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".numberOfTasks", "Number of Tasks", 40, XViewerAlign.Center, false,
         SortDataType.Integer, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NumberOfTasksColumn copy() {
      NumberOfTasksColumn newXCol = new NumberOfTasksColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(", ", strs);
         }
         if (element instanceof TeamWorkFlowArtifact) {
            int num = AtsClientService.get().getTaskService().getTasks(((TeamWorkFlowArtifact) element)).size();
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
}
