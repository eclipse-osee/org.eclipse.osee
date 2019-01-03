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
package org.eclipse.osee.ats.ide.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class BranchStatusColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn {

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
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object element : objects) {
         Long key = getKey(element);
         try {
            if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
               String status = getBranchStatus((IAtsTeamWorkflow) element);
               preComputedValueMap.put(key, status);
            } else if (!(element instanceof AbstractWorkflowArtifact) && element instanceof IAtsWorkItem) {
               populateCachedValues(Arrays.asList(((IAtsWorkItem) element).getStoreObject()), preComputedValueMap);
            } else {
               preComputedValueMap.put(key, "");
            }
         } catch (OseeCoreException ex) {
            String cellExceptionString = LogUtil.getCellExceptionString(ex);
            preComputedValueMap.put(key, cellExceptionString);
         }
      }
   }

   public String getBranchStatus(IAtsTeamWorkflow teamWf) {
      try {
         if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamWf)) {
            return "Working";
         } else if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamWf)) {
            if (!AtsClientService.get().getBranchService().isAllObjectsToCommitToConfigured(
               teamWf) || !AtsClientService.get().getBranchService().isBranchesAllCommitted(teamWf)) {
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
