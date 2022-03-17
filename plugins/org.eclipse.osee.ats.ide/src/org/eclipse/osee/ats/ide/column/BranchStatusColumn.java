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

import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class BranchStatusColumn extends BackgroundLoadingColumn {

   public static BranchStatusColumn instance = new BranchStatusColumn();

   public static BranchStatusColumn getInstance() {
      return instance;
   }

   private BranchStatusColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".branchStatus", "Branch Status", 40, XViewerAlign.Center, false,
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
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      String value = "";
      try {
         if (workItem.isTeamWorkflow()) {
            value = getBranchStatus((IAtsTeamWorkflow) workItem);
         }
      } catch (OseeCoreException ex) {
         value = LogUtil.getCellExceptionString(ex);
      }
      return value;
   }

   public String getBranchStatus(IAtsTeamWorkflow teamWf) {
      try {
         if (AtsApiService.get().getBranchService().isWorkingBranchInWork(teamWf)) {
            return "Working";
         } else if (AtsApiService.get().getBranchService().isCommittedBranchExists(teamWf)) {
            if (!AtsApiService.get().getBranchService().isAllObjectsToCommitToConfigured(
               teamWf) || !AtsApiService.get().getBranchService().isBranchesAllCommitted(teamWf)) {
               return "Needs Commit";
            }
            return "Committed";
         }
         return "";
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

}
