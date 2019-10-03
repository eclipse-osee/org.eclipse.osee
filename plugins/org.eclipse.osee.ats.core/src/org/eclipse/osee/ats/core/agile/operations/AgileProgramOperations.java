/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.IAgileProgram;
import org.eclipse.osee.ats.api.agile.IAgileProgramBacklog;
import org.eclipse.osee.ats.api.agile.IAgileProgramBacklogItem;
import org.eclipse.osee.ats.api.agile.IAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.IAgileStory;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramBacklogItem;
import org.eclipse.osee.ats.api.agile.JaxAgileProgramFeature;
import org.eclipse.osee.ats.api.agile.JaxAgileStory;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.agile.AgileFolders;
import org.eclipse.osee.ats.core.agile.AgileProgram;
import org.eclipse.osee.ats.core.agile.AgileProgramBacklog;
import org.eclipse.osee.ats.core.agile.AgileProgramBacklogItem;
import org.eclipse.osee.ats.core.agile.AgileProgramFeature;
import org.eclipse.osee.ats.core.agile.AgileStory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class AgileProgramOperations {

   private final AtsApi atsApi;

   public AgileProgramOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public IAgileProgram createAgileProgram(IAgileProgram agileProgram) {
      org.eclipse.osee.framework.core.data.ArtifactId userArt =
         atsApi.getQueryService().getArtifact((IAtsObject) atsApi.getUserService().getCurrentUser());

      ArtifactId agileProgramArt = atsApi.getQueryService().getArtifact(agileProgram);
      if (agileProgramArt == null) {

         IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Program");

         agileProgramArt =
            changes.createArtifact(AtsArtifactTypes.AgileProgram, agileProgram.getName(), agileProgram.getId());
         changes.setSoleAttributeValue(agileProgramArt, AtsAttributeTypes.Active, true);
         ArtifactId topAgileFolder = AgileFolders.getOrCreateTopAgileFolder(atsApi, userArt, changes);
         if (topAgileFolder.notEqual(atsApi.getRelationResolver().getParent(agileProgramArt))) {
            changes.unrelateFromAll(CoreRelationTypes.DefaultHierarchical_Parent, agileProgramArt);
            changes.addChild(topAgileFolder, agileProgramArt);
         }

         // re-set parent to program
         for (Long aTeamId : agileProgram.getTeamIds()) {
            IAgileTeam aTeam = atsApi.getQueryService().getConfigItem(aTeamId);
            if (aTeam != null) {
               changes.unrelateAll(aTeam, CoreRelationTypes.DefaultHierarchical_Parent);
               changes.addChild(agileProgramArt, aTeam.getStoreObject());
            }
         }

         changes.execute();
      }
      return getAgileProgram(atsApi, agileProgramArt);
   }

   public static IAgileProgram getAgileProgram(AtsApi atsApi, Object artifact) {
      IAgileProgram program = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         program = new AgileProgram(atsApi.getLogger(), atsApi, art);
      }
      return program;
   }

   public static IAgileProgramFeature getAgileProgramFeature(AtsApi atsApi, Object artifact) {
      IAgileProgramFeature feature = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         feature = new AgileProgramFeature(atsApi.getLogger(), atsApi, art);
      }
      return feature;
   }

   public IAgileProgramFeature createAgileProgramFeature(IAgileProgramBacklogItem programBacklogItem, JaxAgileProgramFeature feature) {
      ArtifactId agileProgramBacklogItemArt = atsApi.getQueryService().getArtifact(programBacklogItem.getId());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Program");
      ArtifactId featureArt =
         changes.createArtifact(AtsArtifactTypes.AgileProgramFeature, feature.getName(), feature.getId());
      changes.setSoleAttributeValue(agileProgramBacklogItemArt, AtsAttributeTypes.Active, true);
      changes.addChild(agileProgramBacklogItemArt, featureArt);
      changes.execute();

      return getAgileProgramFeature(atsApi, featureArt);
   }

   public static IAgileStory getAgileStory(AtsApi atsApi, Object artifact) {
      IAgileStory story = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         story = new AgileStory(atsApi.getLogger(), atsApi, art);
      }
      return story;
   }

   public IAgileStory createAgileStory(IAgileProgramFeature feature, JaxAgileStory story) {
      ArtifactId featureArt = atsApi.getQueryService().getArtifact(feature.getId());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Agile Story");
      ArtifactId storyArt = changes.createArtifact(AtsArtifactTypes.AgileStory, story.getName(), story.getId());
      changes.setSoleAttributeValue(storyArt, AtsAttributeTypes.Active, true);
      changes.addChild(featureArt, storyArt);
      changes.execute();

      return getAgileStory(atsApi, storyArt);
   }

   public static IAgileProgramBacklog getAgileProgramBacklog(AtsApi atsApi, Object artifact) {
      IAgileProgramBacklog progBacklog = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         progBacklog = new AgileProgramBacklog(atsApi.getLogger(), atsApi, art);
      }
      return progBacklog;
   }

   public IAgileProgramBacklog createAgileProgramBacklog(IAgileProgram agileProgram, JaxAgileProgramBacklog jaxProgramBacklog) {
      ArtifactId programArt = atsApi.getQueryService().getArtifact(agileProgram.getId());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Program Backlog");
      ArtifactId programBacklogArt = changes.createArtifact(AtsArtifactTypes.AgileProgramBacklog,
         jaxProgramBacklog.getName(), jaxProgramBacklog.getId());
      changes.setSoleAttributeValue(programBacklogArt, AtsAttributeTypes.Active, true);
      changes.addChild(programArt, programBacklogArt);
      changes.execute();

      return getAgileProgramBacklog(atsApi, programBacklogArt);
   }

   public IAgileProgramBacklogItem createAgileProgramBacklogItem(IAgileProgramBacklog agileProgramBacklog, JaxAgileProgramBacklogItem jaxProgramBacklogItem) {
      ArtifactId programArt = atsApi.getQueryService().getArtifact(agileProgramBacklog.getId());

      IAtsChangeSet changes = atsApi.createChangeSet("Create new Program Backlog Item");
      ArtifactId item = changes.createArtifact(AtsArtifactTypes.AgileProgramBacklogItem,
         jaxProgramBacklogItem.getName(), jaxProgramBacklogItem.getId());
      changes.setSoleAttributeValue(item, AtsAttributeTypes.Active, true);
      changes.addChild(programArt, item);
      changes.execute();

      return getAgileProgramBacklogItem(atsApi, item);
   }

   public static IAgileProgramBacklogItem getAgileProgramBacklogItem(AtsApi atsApi, Object artifact) {
      IAgileProgramBacklogItem item = null;
      if (artifact instanceof ArtifactId) {
         ArtifactToken art = atsApi.getQueryService().getArtifact((ArtifactId) artifact);
         item = new AgileProgramBacklogItem(atsApi.getLogger(), atsApi, art);
      }
      return item;
   }

}
