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
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractBranchColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public AbstractBranchColumnUI(String idPostfix, String name) {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + idPostfix, name, 40, XViewerAlign.Center, false, SortDataType.String,
         false, null);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            TeamWorkFlowArtifact teamArt =
               (TeamWorkFlowArtifact) ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
            if (teamArt != null) {
               try {
                  BranchId workingBranch = null;
                  if (AtsApiService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
                     workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamArt);
                  }
                  if (workingBranch == null) {
                     workingBranch = AtsApiService.get().getBranchService().getCommittedWorkingBranch(teamArt);
                  }
                  if (workingBranch != null && workingBranch.isValid()) {
                     result = getColumnText(workingBranch);
                  }
               } catch (Exception ex) {
                  result = "Exception: " + ex.getLocalizedMessage();
               }
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            result = Collections.toString(", ", strs);
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return result;
   }

   abstract String getColumnText(BranchId branch);

}
