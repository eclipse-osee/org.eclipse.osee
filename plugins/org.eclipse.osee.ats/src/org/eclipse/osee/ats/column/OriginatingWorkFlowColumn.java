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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class OriginatingWorkFlowColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static OriginatingWorkFlowColumn instance = new OriginatingWorkFlowColumn();

   public static OriginatingWorkFlowColumn getInstance() {
      return instance;
   }

   private OriginatingWorkFlowColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".origWf", "Originating Workflow", 150, XViewerAlign.Left, false,
         SortDataType.String, false,
         "Team Workflow(s) that were created upon origination of this Action.  Cancelled workflows not included.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OriginatingWorkFlowColumn copy() {
      OriginatingWorkFlowColumn newXCol = new OriginatingWorkFlowColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            getWorldViewOriginatingWorkflowStr(AtsClientService.get().getQueryServiceClient().getArtifact(element));
         }
         if (element instanceof AbstractWorkflowArtifact) {
            Artifact parentAction = ((AbstractWorkflowArtifact) element).getParentActionArtifact();
            if (parentAction != null) {
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
         strs.add(AtsClientService.get().getColumnService().getColumn(AtsColumnId.Team).getColumnText(team));
      }
      return Collections.toString(";", strs);
   }

   public static Collection<IAtsTeamWorkflow> getWorldViewOriginatingWorkflows(Artifact actionArt) {
      if (AtsClientService.get().getWorkItemService().getTeams(actionArt).size() == 1) {
         return AtsClientService.get().getWorkItemService().getTeams(actionArt);
      }
      Collection<IAtsTeamWorkflow> results = new ArrayList<>();
      Date origDate = null;
      for (IAtsTeamWorkflow teamArt : AtsClientService.get().getWorkItemService().getTeams(actionArt)) {
         if (teamArt.isCancelled()) {
            continue;
         }
         Date teamArtDate = CreatedDateColumn.getDate(teamArt);
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
