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
package org.eclipse.osee.ats.rest.internal.workitem;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.AbstractConfigItemFactory;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.Country;
import org.eclipse.osee.ats.core.config.Program;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.ats.core.config.Version;
import org.eclipse.osee.ats.core.insertion.Insertion;
import org.eclipse.osee.ats.core.insertion.InsertionActivity;
import org.eclipse.osee.ats.core.model.WorkPackage;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 * @author David W. Miller
 */
public class ConfigItemFactory extends AbstractConfigItemFactory {

   private final Log logger;
   private final IAtsServer atsServer;

   public ConfigItemFactory(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public IAtsConfigObject getConfigObject(ArtifactId artifact) {
      if (artifact instanceof IAtsConfigObject) {
         return (IAtsConfigObject) artifact;
      } else if (artifact instanceof ArtifactReadable) {
         return getConfigObject((ArtifactReadable) artifact);
      }
      QueryBuilder query = atsServer.getOrcsApi().getQueryFactory().fromBranch(atsServer.getAtsBranch());
      return getConfigObject(query.andId(artifact).getResults().getExactlyOne());
   }

   private IAtsConfigObject getConfigObject(ArtifactReadable artifact) {
      IAtsConfigObject configObject;
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         configObject = getVersion(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
         configObject = getTeamDef(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
         configObject = getActionableItem(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.Program)) {
         configObject = getProgram(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.AgileTeam)) {
         configObject = getAgileTeam(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         configObject = getAgileFeatureGroup(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.Insertion)) {
         configObject = getInsertion(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.InsertionActivity)) {
         configObject = getInsertionActivity(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.Country)) {
         configObject = getCountry(artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.WorkPackage)) {
         configObject = getWorkPackage(artifact);
      } else {
         throw new OseeArgumentException("Unexpected artifact type [%s]", artifact.getArtifactTypeId());
      }
      return configObject;
   }

   @Override
   public IAtsWorkPackage getWorkPackage(ArtifactId artifact) {
      IAtsWorkPackage workPackage = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable art = (ArtifactReadable) artifact;
         if (art.isOfType(AtsArtifactTypes.WorkPackage)) {
            workPackage = new WorkPackage(logger, art, atsServer.getServices());
         }
      }
      return workPackage;
   }

   @Override
   public IAtsVersion getVersion(ArtifactId artifact) {
      IAtsVersion version = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Version)) {
            version = new Version(logger, atsServer, artRead);
         }
      }
      return version;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(ArtifactId artifact) {
      IAtsTeamDefinition teamDef = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef = new TeamDefinition(logger, atsServer, artRead);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsActionableItem getActionableItem(ArtifactId artifact) {
      IAtsActionableItem ai = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai = new ActionableItem(logger, atsServer, artRead);
         }
      }
      return ai;
   }

   @Override
   public IAtsProgram getProgram(ArtifactId artifact) {
      Program program = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Program)) {
            program = new Program(logger, atsServer, artRead);
         }
      }
      return program;
   }

   @Override
   public IAgileTeam getAgileTeam(ArtifactId artifact) {
      IAgileTeam agileTeam = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.AgileTeam)) {
            agileTeam = atsServer.getAgileService().getAgileTeam(artRead);
         }
      }
      return agileTeam;
   }

   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact) {
      IAgileFeatureGroup agileTeam = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
            agileTeam = atsServer.getAgileService().getAgileFeatureGroup(artRead);
         }
      }
      return agileTeam;
   }

   @Override
   public IAtsInsertion getInsertion(ArtifactId artifact) {
      Insertion insertion = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Insertion)) {
            insertion = new Insertion(logger, atsServer.getServices(), artRead);
            ArtifactReadable programArt =
               ((ArtifactReadable) artifact).getRelated(AtsRelationTypes.ProgramToInsertion_Program).getOneOrNull();
            if (programArt != null) {
               insertion.setProgramId(programArt.getId());
            }
         } else {
            throw new OseeCoreException("Requested id not Insertion");
         }
      }
      return insertion;
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(ArtifactId artifact) {
      InsertionActivity insertionActivity = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.InsertionActivity)) {
            insertionActivity = new InsertionActivity(logger, atsServer.getServices(), artRead);
            ArtifactReadable insertionArt = ((ArtifactReadable) artifact).getRelated(
               AtsRelationTypes.InsertionToInsertionActivity_Insertion).getOneOrNull();
            if (insertionArt != null) {
               insertionActivity.setInsertionId(insertionArt.getId());
            }
         } else {
            throw new OseeCoreException("Requested id not Insertion Activity");
         }
      }
      return insertionActivity;
   }

   @Override
   public IAtsInsertion createInsertion(ArtifactId programArtifact, JaxInsertion newInsertion) {

      long id = newInsertion.getId();
      if (id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Create new Insertion",
         atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.Insertion, newInsertion.getName(), id);

      changes.relate(programArtifact, AtsRelationTypes.ProgramToInsertion_Insertion, insertionArt);
      changes.execute();
      return getInsertion(insertionArt);
   }

   @Override
   public IAtsInsertion updateInsertion(JaxInsertion updatedInsertion) {
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Update Insertion",
         atsServer.getUserService().getCurrentUser());
      changes.setSoleAttributeValue(ArtifactId.valueOf(updatedInsertion.getId()), CoreAttributeTypes.Name,
         updatedInsertion.getName());
      changes.execute();
      return getInsertion(atsServer.getQueryService().getArtifact(updatedInsertion.getId()));
   }

   @Override
   public void deleteInsertion(ArtifactId artifact) {
      deleteConfigObject(artifact.getId(), "Delete Insertion", AtsArtifactTypes.Insertion);
   }

   @Override
   public IAtsInsertionActivity createInsertionActivity(ArtifactId insertion, JaxInsertionActivity newActivity) {
      long id = newActivity.getId();
      if (id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Create new Insertion Activity",
         atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionActivityArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.InsertionActivity, newActivity.getName(), id);

      changes.relate(insertion, AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity, insertionActivityArt);
      changes.execute();
      return getInsertionActivity(insertionActivityArt);
   }

   @Override
   public IAtsInsertionActivity updateInsertionActivity(JaxInsertionActivity updatedActivity) {
      IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Update Insertion",
         atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionActivityArt =
         (ArtifactReadable) atsServer.getQueryService().getArtifact(updatedActivity.getId());

      changes.setSoleAttributeValue(insertionActivityArt, CoreAttributeTypes.Name, updatedActivity.getName());
      changes.setSoleAttributeValue(ArtifactId.valueOf(updatedActivity.getId()), CoreAttributeTypes.Name,
         updatedActivity.getName());
      changes.execute();
      return getInsertionActivity(atsServer.getQueryService().getArtifact(updatedActivity.getId()));
   }

   @Override
   public void deleteInsertionActivity(ArtifactId artifact) {
      deleteConfigObject(artifact.getId(), "Delete Insertion Activity", AtsArtifactTypes.InsertionActivity);
   }

   private void deleteConfigObject(long id, String comment, IArtifactType type) {
      ArtifactReadable toDelete = (ArtifactReadable) atsServer.getQueryService().getArtifact(id);
      if (toDelete == null) {
         throw new OseeCoreException("No object found for id %d", id);
      }

      if (!toDelete.isTypeEqual(type)) {
         throw new OseeCoreException("Artifact type does not match for %s", comment);
      }
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory().createTransaction(atsServer.getAtsBranch(), toDelete, comment);
      transaction.deleteArtifact(toDelete);
      transaction.commit();
   }

   @Override
   public boolean isAtsConfigArtifact(ArtifactId artifact) {
      return getAtsConfigArtifactTypes().contains(((ArtifactReadable) artifact).getArtifactTypeId());
   }

   @Override
   public IAtsCountry getCountry(ArtifactId artifact) {
      IAtsCountry country = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Country)) {
            country = new Country(logger, atsServer.getServices(), artRead);
         } else {
            throw new OseeCoreException("Requested id not Country");
         }
      }
      return country;
   }
}