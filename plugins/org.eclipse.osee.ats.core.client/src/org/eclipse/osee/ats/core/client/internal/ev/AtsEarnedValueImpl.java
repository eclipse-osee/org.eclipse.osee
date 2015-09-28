/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.ev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.ev.WorkPackageArtifact;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsEarnedValueImpl implements IAtsEarnedValueService {

   @Override
   public IAtsWorkPackage getWorkPackage(IAtsWorkItem workItem) throws OseeCoreException {
      WorkPackageArtifact wpa = null;
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
         String workPackageGuid = awa.getSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid, null);
         if (Strings.isValid(workPackageGuid)) {
            Artifact workPkgArt = ArtifactQuery.getArtifactFromId(workPackageGuid, AtsUtilCore.getAtsBranch());
            wpa = new WorkPackageArtifact(workPkgArt);
         }
      }
      return wpa;
   }

   @Override
   public Collection<IAtsWorkPackage> getWorkPackageOptions(IAtsObject object) throws OseeCoreException {
      List<IAtsWorkPackage> workPackageOptions = new ArrayList<>();
      getWorkPackageOptions(object, workPackageOptions);
      return workPackageOptions;
   }

   public Collection<IAtsWorkPackage> getWorkPackageOptions(IAtsObject object, List<IAtsWorkPackage> workPackageOptions) throws OseeCoreException {
      // Config objects get work package options from related work package artifacts
      if (object instanceof IAtsConfigObject) {
         IAtsConfigObject configObj = (IAtsConfigObject) object;
         Artifact artifact = AtsClientService.get().getArtifact(configObj);
         if (artifact != null) {
            for (Artifact workPackageArt : artifact.getRelatedArtifacts(AtsRelationTypes.WorkPackage_WorkPackage)) {
               workPackageOptions.add(new WorkPackageArtifact(workPackageArt));
            }
         }
      }
      // Team Wf get work package options of Ais and Team Definition
      else if (object instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) object;
         getWorkPackageOptions(teamWf.getTeamDefinition(), workPackageOptions);
         for (IAtsActionableItem ai : teamWf.getActionableItems()) {
            getWorkPackageOptions(ai, workPackageOptions);
         }
      }
      // Children work items inherit the work packages options of their parent team workflow
      else if (object instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) object;
         // Work Items related to Team Wf get their options from Team Wf
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            getWorkPackageOptions(teamWf, workPackageOptions);
         }
         // Stand-alone reviews get their options from related AIs and Team Defs
         else if (workItem instanceof IAtsAbstractReview) {
            IAtsAbstractReview review = (IAtsAbstractReview) workItem;
            for (IAtsActionableItem ai : review.getActionableItems()) {
               getWorkPackageOptions(ai, workPackageOptions);
               if (ai.getTeamDefinition() != null) {
                  getWorkPackageOptions(ai.getTeamDefinition(), workPackageOptions);
               }
            }
         }
      }
      return workPackageOptions;
   }
}
