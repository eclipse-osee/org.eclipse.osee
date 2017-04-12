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
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractBranchColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public AbstractBranchColumn(String idPostfix, String name) {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + idPostfix, name, 40, XViewerAlign.Center, false, SortDataType.String,
         false, null);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
            if (teamArt != null) {
               try {
                  BranchId workingBranch = null;
                  if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
                     workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
                  }
                  if (workingBranch == null) {
                     workingBranch = AtsClientService.get().getBranchService().getCommittedWorkingBranch(teamArt);
                  }
                  if (workingBranch != null) {
                     result = getColumnText(workingBranch);
                  }
               } catch (Exception ex) {
                  result = "Exception: " + ex.getLocalizedMessage();
               }
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(element)) {
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
