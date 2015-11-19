/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.program.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramService implements IAtsProgramService {

   private final IAtsConfig config;
   private final IAtsClient atsClient;

   public AtsProgramService(IAtsClient atsClient, IAtsConfig config) {
      this.atsClient = atsClient;
      this.config = config;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram) {
      IAtsTeamDefinition teamDef = atsProgram.getTeamDefinition();
      if (teamDef == null) {
         Artifact artifact = (Artifact) atsProgram.getStoreObject();
         String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, null);
         if (Strings.isValid(teamDefGuid)) {
            Long uuid = atsClient.getStoreService().getUuidFromGuid(teamDefGuid);
            if (uuid != null) {
               teamDef = (IAtsTeamDefinition) config.getSoleByUuid(uuid);
            }
         }
      }
      return teamDef;
   }

   @Override
   public IAtsProgram getProgram(IAtsWorkItem workItem) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

   @Override
   public IAtsProgram getProgramByGuid(String guid) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

   @Override
   public IAtsCountry getCountry(IAtsProgram atsProgram) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

   @Override
   public List<IAtsProgram> getPrograms(IAtsCountry atsCountry) {
      throw new UnsupportedOperationException("Not implemented yet");
   }

   @Override
   public IAtsProgram getProgram(Long programUuid) {
      return atsClient.getConfigItemFactory().getProgram(atsClient.getArtifact(programUuid));
   }

   @Override
   public Collection<IAtsProgram> getPrograms() {
      List<IAtsProgram> programs = new ArrayList<>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromTypeWithInheritence(AtsArtifactTypes.Program,
         AtsUtilCore.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED)) {
         programs.add(AtsClientService.get().getConfigItemFactory().getProgram(artifact));
      }
      return programs;
   }

   @Override
   public Collection<IAtsInsertion> getInsertions(IAtsProgram program) {
      List<IAtsInsertion> insertions = new ArrayList<>();
      for (Artifact artifact : atsClient.getArtifact(program.getUuid()).getRelatedArtifacts(
         AtsRelationTypes.ProgramToInsertion_Insertion)) {
         insertions.add(AtsClientService.get().getConfigItemFactory().getInsertion(artifact));
      }
      return insertions;
   }

   @Override
   public IAtsInsertion getInsertion(Long insertionUuid) {
      return AtsClientService.get().getConfigItemFactory().getInsertion(atsClient.getArtifact(insertionUuid));
   }

   @Override
   public Collection<IAtsInsertionActivity> getInsertionActivities(IAtsInsertion insertion) {
      List<IAtsInsertionActivity> insertionActivitys = new ArrayList<>();
      for (Artifact artifact : atsClient.getArtifact(insertion.getUuid()).getRelatedArtifacts(
         AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
         insertionActivitys.add(AtsClientService.get().getConfigItemFactory().getInsertionActivity(artifact));
      }
      return insertionActivitys;
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(Long insertionActivityUuid) {
      return AtsClientService.get().getConfigItemFactory().getInsertionActivity(
         atsClient.getArtifact(insertionActivityUuid));
   }

   @Override
   public IAtsWorkPackage getWorkPackage(Long workPackageUuid) {
      return AtsClientService.get().getEarnedValueService().getWorkPackage(atsClient.getArtifact(workPackageUuid));
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(IAtsWorkPackage workPackage) {
      Artifact wpArt = atsClient.getArtifact(workPackage.getUuid());
      return atsClient.getConfigItemFactory().getInsertionActivity(
         wpArt.getRelatedArtifact(AtsRelationTypes.InsertionActivityToWorkPackage_InsertionActivity));
   }

   @Override
   public IAtsInsertion getInsertion(IAtsInsertionActivity activity) {
      return atsClient.getConfigItemFactory().getInsertion(((Artifact) activity.getStoreObject()).getRelatedArtifact(
         AtsRelationTypes.InsertionToInsertionActivity_Insertion));
   }

   @Override
   public IAtsProgram getProgram(IAtsInsertion insertion) {
      return atsClient.getConfigItemFactory().getProgram(
         ((Artifact) insertion.getStoreObject()).getRelatedArtifact(AtsRelationTypes.ProgramToInsertion_Program));
   }

   @Override
   public void setWorkPackage(IAtsWorkPackage workPackage, List<IAtsWorkItem> workItems) {
      AtsClientService.get().getEarnedValueService().setWorkPackage(workPackage, workItems);
   }

}
