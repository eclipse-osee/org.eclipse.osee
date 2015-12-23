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
package org.eclipse.osee.ats.rest.internal.workitem;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramService implements IAtsProgramService {

   private final IAtsServer atsServer;
   private final Cache<IAtsTeamDefinition, IAtsProgram> cache =
      CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

   public AtsProgramService(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram) {
      IAtsTeamDefinition teamDef = atsProgram.getTeamDefinition();
      if (teamDef == null) {
         ArtifactReadable artifact = (ArtifactReadable) atsProgram.getStoreObject();
         String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, null);
         if (Strings.isValid(teamDefGuid)) {
            teamDef = (IAtsTeamDefinition) atsServer.getArtifactByGuid(teamDefGuid);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsProgram getProgram(IAtsWorkItem wi) {
      IAtsTeamDefinition teamDefinition = wi.getParentTeamWorkflow().getTeamDefinition();
      IAtsProgram program = cache.getIfPresent(teamDefinition);
      if (program == null) {
         IAtsTeamDefinition topTeamDef = teamDefinition.getTeamDefinitionHoldingVersions();
         QueryBuilder query = atsServer.getQuery();
         query.and(AtsAttributeTypes.TeamDefinition, AtsUtilCore.getGuid(topTeamDef)).andIsOfType(
            AtsArtifactTypes.Program);
         ArtifactReadable programArt = query.getResults().getOneOrNull();
         program = atsServer.getConfigItemFactory().getProgram(programArt);
         cache.put(teamDefinition, program);
      }
      return program;
   }

   @Override
   public IAtsProgram getProgramByGuid(String guid) {
      ArtifactReadable prgArt = (ArtifactReadable) atsServer.getArtifactById(guid);
      return atsServer.getConfigItemFactory().getProgram(prgArt);
   }

   @Override
   public IAtsCountry getCountry(IAtsProgram atsProgram) {
      IAtsCountry country = null;
      ArtifactReadable artifact = (ArtifactReadable) atsProgram.getStoreObject();
      if (artifact != null) {
         ArtifactReadable countryArt =
            artifact.getRelated(AtsRelationTypes.CountryToProgram_Country).getAtMostOneOrNull();
         if (countryArt != null) {
            country = atsServer.getConfigItemFactory().getCountry(countryArt);
         }
      }
      return country;
   }

   @Override
   public List<IAtsProgram> getPrograms(IAtsCountry atsCountry) {
      List<IAtsProgram> programs = new LinkedList<>();
      ArtifactReadable artifact = (ArtifactReadable) atsCountry.getStoreObject();
      if (artifact != null) {
         for (ArtifactReadable related : artifact.getRelated(AtsRelationTypes.CountryToProgram_Program)) {
            programs.add(atsServer.getConfigItemFactory().getProgram(related));
         }
      }
      return programs;
   }

   @Override
   public IAtsProgram getProgram(Long programUuid) {
      return atsServer.getConfigItemFactory().getProgram(atsServer.getArtifact(programUuid));
   }

   @Override
   public Collection<IAtsProgram> getPrograms() {
      List<IAtsProgram> programs = new ArrayList<>();
      for (ArtifactReadable artifact : atsServer.getOrcsApi().getQueryFactory().fromBranch(
         AtsUtilCore.getAtsBranch()).andIsOfType(AtsArtifactTypes.Program).getResults()) {
         programs.add(atsServer.getConfigItemFactory().getProgram(artifact));
      }
      return programs;
   }

   @Override
   public Collection<IAtsInsertion> getInsertions(IAtsProgram program) {
      List<IAtsInsertion> insertions = new ArrayList<>();
      for (ArtifactReadable artifact : atsServer.getArtifact(program.getUuid()).getRelated(
         AtsRelationTypes.ProgramToInsertion_Insertion)) {
         insertions.add(atsServer.getConfigItemFactory().getInsertion(artifact));
      }
      return insertions;
   }

   @Override
   public IAtsInsertion getInsertion(Long insertionUuid) {
      return atsServer.getConfigItemFactory().getInsertion(atsServer.getArtifact(insertionUuid));
   }

   @Override
   public Collection<IAtsInsertionActivity> getInsertionActivities(IAtsInsertion insertion) {
      List<IAtsInsertionActivity> insertionActivitys = new ArrayList<>();
      for (ArtifactReadable artifact : atsServer.getArtifact(insertion.getUuid()).getRelated(
         AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
         insertionActivitys.add(atsServer.getConfigItemFactory().getInsertionActivity(artifact));
      }
      return insertionActivitys;
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(Long insertionActivityUuid) {
      return atsServer.getConfigItemFactory().getInsertionActivity(atsServer.getArtifact(insertionActivityUuid));
   }

   @Override
   public IAtsWorkPackage getWorkPackage(Long workPackageUuid) {
      throw new UnsupportedOperationException("getWorkPackage not supported on server");
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(IAtsWorkPackage workPackage) {
      ArtifactReadable wpArt = atsServer.getArtifact(workPackage.getUuid());
      ResultSet<ArtifactReadable> related =
         wpArt.getRelated(AtsRelationTypes.InsertionActivityToWorkPackage_InsertionActivity);
      if (related.size() > 0) {
         return atsServer.getConfigItemFactory().getInsertionActivity(related.iterator().next());
      }
      return null;
   }

   @Override
   public IAtsInsertion getInsertion(IAtsInsertionActivity activity) {
      ResultSet<ArtifactReadable> related = ((ArtifactReadable) activity.getStoreObject()).getRelated(
         AtsRelationTypes.InsertionToInsertionActivity_Insertion);
      if (related.size() > 0) {
         return atsServer.getConfigItemFactory().getInsertion(related.iterator().next());
      }
      return null;
   }

   @Override
   public IAtsProgram getProgram(IAtsInsertion insertion) {
      ResultSet<ArtifactReadable> related =
         ((ArtifactReadable) insertion.getStoreObject()).getRelated(AtsRelationTypes.ProgramToInsertion_Program);
      if (related.size() > 0) {
         return atsServer.getConfigItemFactory().getProgram(related.iterator().next());
      }
      return null;
   }

   @Override
   public void setWorkPackage(IAtsWorkPackage workPackage, List<IAtsWorkItem> workItems) {
      throw new UnsupportedOperationException("setWorkPackage not supported on server");
   }

}
