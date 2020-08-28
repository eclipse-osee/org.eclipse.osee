/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.related;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.ats.api.task.related.DerivedFromTaskData;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.task.related.AbstractAtsTaskRelatedService;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskRelatedService extends AbstractAtsTaskRelatedService {

   public AtsTaskRelatedService(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public void getRelatedChangedArtifactFromChangeReport(DerivedFromTaskData trd) {
      ChangeData changeData = AtsApiService.get().getBranchServiceIde().getChangeDataFromEarliestTransactionId(
         (TeamWorkFlowArtifact) trd.getDerivedFromTeamWf().getStoreObject());
      getTaskRelatedData(trd, changeData);
   }

   private DerivedFromTaskData getTaskRelatedData(DerivedFromTaskData trd, ChangeData changeData) {
      final IAutoGenTaskData data = AtsApiService.get().getTaskRelatedService().getAutoGenTaskData(trd.getTask());

      if (data.isNoChangedArtifact()) {
         trd.getResults().error("No changed artifact to show");
         return trd;
      }
      if (!data.hasRelatedArt()) {
         trd.getResults().error(
            "Task is not against artifact or is named incorrectly.\n\n" + "Must be \"Code|Test \"<partition>\" for \"<requirement name>\"");
         return trd;
      }

      // Can't get head artifact for deleted art
      if (!trd.isDeleted()) {
         Artifact headArtifact = findHeadArtifact(changeData, data.getRelatedArtName(), data.getAddDetails());
         if (headArtifact == null) {
            trd.getResults().error("Corresponding requirement can not be found.");
            return trd;
         }
         Artifact latestArt = null;
         if (!headArtifact.isDeleted()) {
            latestArt = ArtifactQuery.getArtifactFromToken(headArtifact, DeletionFlag.INCLUDE_DELETED);
            trd.setLatestArt(latestArt);
         }
      }
      return trd;
   }

   public static Artifact findHeadArtifact(ChangeData changeData, String name, String appendedStr) {
      Artifact headArtifact = null;
      for (Artifact art : changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.NEW,
         ModificationType.MODIFIED)) {
         if (Strings.isValid(appendedStr) && appendedStr.equals(IMPL_DETAILS) && //
            art.isOfType(CoreArtifactTypes.AbstractImplementationDetails)) {
            if (name.contains(art.getName())) {
               headArtifact = art;
               break;
            }
         } else if (Strings.isInValid(appendedStr) && //
            !art.isOfType(CoreArtifactTypes.AbstractImplementationDetails) && //
            art.getName().contains(name)) {
            headArtifact = art;
            break;
         }
      }
      if (headArtifact == null) {
         for (Artifact art : changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.DELETED)) {
            if (appendedStr.equals(DELETED)) {
               if (name.contains(art.getName())) {
                  headArtifact = art;
                  break;
               }
            }
         }
      }
      return headArtifact;
   }

   @Override
   public IAutoGenTaskData getAutoGenTaskData(IAtsTask task) {
      String autoGenVerStr = atsApi.getAttributeResolver().getSoleAttributeValue(task,
         AtsAttributeTypes.TaskAutoGenVersion, AutoGenVersion.Other.getName());
      for (IAtsTaskProvider taskProvider : atsApi.getTaskService().getTaskProviders()) {
         IAutoGenTaskData taskGenData = taskProvider.getAutoGenTaskData(autoGenVerStr, task);
         if (taskGenData != null) {
            return taskGenData;
         }
      }
      return null;
   }

}
