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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class OriginatingWorkFlowColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static OriginatingWorkFlowColumnUI instance = new OriginatingWorkFlowColumnUI();

   public static OriginatingWorkFlowColumnUI getInstance() {
      return instance;
   }

   private OriginatingWorkFlowColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".origWf", "Originating Workflow", 150, XViewerAlign.Left, false,
         SortDataType.String, false,
         "Team Workflow(s) that were created upon origination of this Action.  Cancelled workflows not included.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OriginatingWorkFlowColumnUI copy() {
      OriginatingWorkFlowColumnUI newXCol = new OriginatingWorkFlowColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            IAtsAction action = ((AbstractWorkflowArtifact) element).getParentAction();
            if (action != null) {
               Artifact parentAction = (Artifact) action.getStoreObject();
               return getColumnText(parentAction, column, columnIndex);
            }
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   public static String getWorldViewOriginatingWorkflowStr(Artifact actionArt) {
      Set<String> strs = new HashSet<>();
      for (IAtsTeamWorkflow team : getWorldViewOriginatingWorkflows(actionArt)) {
         strs.add(
            AtsApiService.get().getColumnService().getColumn(AtsColumnTokensDefault.TeamColumn).getColumnText(team));
      }
      return Collections.toString(";", strs);
   }

   public static Collection<IAtsTeamWorkflow> getWorldViewOriginatingWorkflows(Artifact actionArt) {
      if (AtsApiService.get().getWorkItemService().getTeams(actionArt).size() == 1) {
         return AtsApiService.get().getWorkItemService().getTeams(actionArt);
      }
      Collection<IAtsTeamWorkflow> results = new ArrayList<>();
      Date origDate = null;
      for (IAtsTeamWorkflow teamArt : AtsApiService.get().getWorkItemService().getTeams(actionArt)) {
         if (teamArt.isCancelled()) {
            continue;
         }
         Date teamArtDate =
            AtsApiService.get().getColumnService().getColumnDate(AtsColumnTokensDefault.CreatedDateColumn, teamArt);
         if (origDate == null || teamArtDate.before(origDate)) {
            results.clear();
            origDate = teamArtDate;
            results.add(teamArt);
         } else if (origDate.equals(teamArtDate)) {
            results.add(teamArt);
         }
      }
      return results;
   }
}
