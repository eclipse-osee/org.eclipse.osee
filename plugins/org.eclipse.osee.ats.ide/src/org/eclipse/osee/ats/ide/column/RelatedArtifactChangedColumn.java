/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Morgan E. Cook
 */
public class RelatedArtifactChangedColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn {

   public static RelatedArtifactChangedColumn instance = new RelatedArtifactChangedColumn();
   private final Map<IAtsTeamWorkflow, BranchToken> teamWfToDerivedBranch = new HashMap<>();

   public static RelatedArtifactChangedColumn getInstance() {
      return instance;
   }

   private RelatedArtifactChangedColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".RelatedArtifactChangedColumn", "Related Artifact Changed", 75,
         XViewerAlign.Left, false, SortDataType.String, false,
         "Committed - baseline/committed branch \nUnmodified - Related artifact has not changed " + "\n<date> - Related artifact has been modified after task at the specified date \nEmpty - There is no related artifact");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RelatedArtifactChangedColumn copy() {
      RelatedArtifactChangedColumn newXCol = new RelatedArtifactChangedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object object : objects) {
         String value = "";
         try {
            if (!(object instanceof TaskArtifact)) {
               return;
            }
            TaskArtifact task = (TaskArtifact) object;
            ArtifactId refArtId = AtsApiService.get().getQueryServiceIde().getArtifact(object).getSoleAttributeValue(
               AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
            if (refArtId.isInvalid()) {
               return;
            }
            TeamWorkFlowArtifact teamWf = task.getParentTeamWorkflow();
            BranchToken refBranch = teamWfToDerivedBranch.get(teamWf);
            if (refBranch == null) {
               TeamWorkFlowArtifact reqWf =
                  (TeamWorkFlowArtifact) AtsApiService.get().getRelationResolver().getRelatedOrNull((IAtsObject) teamWf,
                     AtsRelationTypes.Derive_From);
               if (reqWf == null) {
                  return;
               }
               refBranch = reqWf.getWorkingBranch();
               if (refBranch.isInvalid()) {
                  return;
               }
               teamWfToDerivedBranch.put(teamWf, refBranch);
            }
            Artifact refArt = AtsApiService.get().getQueryServiceIde().getArtifact(refArtId, refBranch);
            if (refArt != null) {
               if (refArt.isDeleted()) {
                  value = "Deleted";
               } else if (BranchManager.getState(refBranch).isCommitted() || BranchManager.getType(
                  refBranch).isBaselineBranch()) {
                  value = "Commited";
               } else if (refArt.getLastModified().after(
                  AtsApiService.get().getQueryServiceIde().getArtifact(object).getLastModified())) {
                  value = refArt.getLastModified().toString();
               } else {
                  value = "Unmodified";
               }
            }
         } catch (OseeCoreException ex) {
            value = LogUtil.getCellExceptionString(ex);
         }
         Long key = getKey(object);
         preComputedValueMap.put(key, value);
      }
   }

}