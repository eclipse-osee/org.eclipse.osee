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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class BranchStatusColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static BranchStatusColumn instance = new BranchStatusColumn();

   public static BranchStatusColumn getInstance() {
      return instance;
   }

   private BranchStatusColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".branchStatus", "Branch Status", 40, SWT.CENTER, false,
         SortDataType.String, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BranchStatusColumn copy() {
      BranchStatusColumn newXCol = new BranchStatusColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(", ", strs);
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) element;
            try {
               if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
                  return "Working";
               } else if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
                  if (!AtsClientService.get().getBranchService().isAllObjectsToCommitToConfigured(
                     teamArt) || !AtsClientService.get().getBranchService().isBranchesAllCommitted(teamArt)) {
                     return "Needs Commit";
                  }
                  return "Committed";
               }
               return "";
            } catch (Exception ex) {
               return "Exception: " + ex.getLocalizedMessage();
            }
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
