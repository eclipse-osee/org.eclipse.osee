/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.program;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramService {

   // country
   IAtsCountry getCountry(IAtsProgram atsProgram);

   long getCountryId(IAtsProgram program);

   // program
   IAtsProgram getProgram(IAtsWorkItem wi);

   List<IAtsProgram> getPrograms(IAtsCountry atsCountry);

   IAtsProgram getProgramById(ArtifactId programId);

   Collection<IAtsProgram> getPrograms();

   IAtsProgram getProgram(IAtsInsertion insertion);

   IAtsProgram getProgram(IAtsTeamDefinition iAtsTeamDefinition);

   // insertions
   Collection<IAtsInsertion> getInsertions(IAtsProgram program);

   IAtsInsertion getInsertion(Long insertionId);

   IAtsInsertion getInsertion(IAtsInsertionActivity activity);

   // insertion activities
   IAtsInsertionActivity getInsertionActivity(IAtsWorkPackage workPackage);

   Collection<IAtsInsertionActivity> getInsertionActivities(IAtsInsertion iAtsInsertion);

   IAtsInsertionActivity getInsertionActivity(Long insertionActivityId);

   // work package
   IAtsWorkPackage getWorkPackage(Long workPackageId);

   void setWorkPackage(IAtsWorkPackage workPackage, List<IAtsWorkItem> workItems, AtsUser asUser);

   // program
   String getDescription(IAtsProgram program);

   IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program);

   IAtsTeamDefinition getTeamDefinition(IAtsProgram program);

   Collection<IAtsTeamDefinition> getTeamDefs(IAtsProgram program);

   Collection<IAtsTeamDefinition> getTeamDefs(IAtsProgram program, Collection<WorkType> workTypes);

   Collection<IAtsTeamDefinition> getTeamDefs(IAtsProgram program, WorkType workType);

   Collection<IAtsActionableItem> getAis(IAtsProgram program);

   Collection<IAtsActionableItem> getAis(IAtsProgram program, WorkType workType);

   Collection<IAtsActionableItem> getAis(IAtsProgram program, Collection<WorkType> workTypes);

   Collection<IAtsProgram> getPrograms(ArtifactTypeToken artifactType);

   Collection<String> getCscis(IAtsProgram program);

   WorkType getWorkType(IAtsTeamDefinition teamDef);

   WorkType getWorkType(IAtsTeamWorkflow teamWf);

   ProjectType getProjectType(IAtsProgram program);

   String getNamespace(IAtsProgram program);

   boolean isActive(IAtsProgram program);

   Collection<IAtsVersion> getVersions(IAtsProgram program);

   IAtsVersion getVersion(IAtsProgram program, String versionName);

   // workflows
   Collection<IAtsTeamWorkflow> getWorkflows(IAtsProgram program, WorkType workType, IAtsWorkItem workItem);

   Collection<IAtsTeamWorkflow> getWorkflows(IAtsProgram program, Collection<WorkType> workTypes, IAtsWorkItem workItem);

   Collection<IAtsTeamWorkflow> getWorkflows(IAtsProgram program, Collection<WorkType> workTypes);

   Collection<IAtsTeamWorkflow> getWorkflows(IAtsProgram program, WorkType workType);

   Collection<IAtsTeamWorkflow> getWorkflows(IAtsProgram program);

   List<ProgramVersions> getProgramVersions(ArtifactTypeToken artType, boolean activeOnly);

   ProgramVersions getProgramVersions(ArtifactToken program, boolean onlyActive);

   ArtifactToken getProgramFromVersion(ArtifactId version);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   IAtsInsertion getInsertionById(ArtifactId insertionId);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   IAtsInsertionActivity getInsertionActivityById(ArtifactId insertionActivityId);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   IAtsCountry getCountryById(ArtifactId countryId);

   BranchToken getProductLineBranch(IAtsProgram program);

   IAtsProgramManager getProgramManager(IAtsTeamWorkflow teamWf);

}
