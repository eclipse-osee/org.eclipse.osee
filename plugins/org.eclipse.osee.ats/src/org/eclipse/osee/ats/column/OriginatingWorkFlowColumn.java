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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class OriginatingWorkFlowColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static OriginatingWorkFlowColumn instance = new OriginatingWorkFlowColumn();

   public static OriginatingWorkFlowColumn getInstance() {
      return instance;
   }

   private OriginatingWorkFlowColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".origWf", "Originating Workflow", 150, SWT.LEFT, false,
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
            getWorldViewOriginatingWorkflowStr((Artifact) element);
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

   public static String getWorldViewOriginatingWorkflowStr(Artifact actionArt) throws OseeCoreException {
      Set<String> strs = new HashSet<>();
      for (TeamWorkFlowArtifact team : getWorldViewOriginatingWorkflows(actionArt)) {
         strs.add(AtsClientService.get().getColumnUtilities().getTeamUtility().getColumnText(team));
      }
      return Collections.toString(";", strs);
   }

   public static Collection<TeamWorkFlowArtifact> getWorldViewOriginatingWorkflows(Artifact actionArt) throws OseeCoreException {
      if (ActionManager.getTeams(actionArt).size() == 1) {
         return ActionManager.getTeams(actionArt);
      }
      Collection<TeamWorkFlowArtifact> results = new ArrayList<>();
      Date origDate = null;
      for (TeamWorkFlowArtifact teamArt : ActionManager.getTeams(actionArt)) {
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
