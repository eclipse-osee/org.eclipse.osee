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
package org.eclipse.osee.ats.impl.internal.workitem;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionFeature;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertion;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertionFeature;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.util.AtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 * @author David W. Miller
 */
public class ConfigItemFactory implements IAtsConfigItemFactory {

   private final Log logger;
   private final IAtsServer atsServer;

   public ConfigItemFactory(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public IAtsConfigObject getConfigObject(Object artifact) throws OseeCoreException {
      IAtsConfigObject configObject = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            ArtifactReadable artRead = (ArtifactReadable) artifact;
            if (artRead.isOfType(AtsArtifactTypes.Version)) {
               configObject = getVersion(artifact);
            } else if (artRead.isOfType(AtsArtifactTypes.TeamDefinition)) {
               configObject = getTeamDef(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.ActionableItem)) {
               configObject = getActionableItem(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Program)) {
               configObject = getProgram(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.AgileTeam)) {
               configObject = getAgileTeam(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
               configObject = getAgileFeatureGroup(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Insertion)) {
               configObject = getInsertion(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.InsertionFeature)) {
               configObject = getInsertionFeature(artRead);
            }
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting config object for [%s]", artifact);
      }
      return configObject;
   }

   @Override
   public IAtsVersion getVersion(Object artifact) {
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
   public IAtsTeamDefinition getTeamDef(Object artifact) throws OseeCoreException {
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
   public IAtsActionableItem getActionableItem(Object artifact) throws OseeCoreException {
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
   public IAtsProgram getProgram(Object artifact) {
      IAtsProgram program = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Program)) {
            program = new Program(logger, atsServer, artRead);
         }
      }
      return program;
   }

   @Override
   public IAgileTeam getAgileTeam(Object artifact) {
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
   public IAgileFeatureGroup getAgileFeatureGroup(Object artifact) {
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
      IAtsInsertion insertion = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Insertion)) {
            insertion = new Insertion(logger, atsServer, artRead);
         } else {
            throw new OseeCoreException("Requested uuid not Insertion");
         }
      }
      return insertion;
   }

   @Override
   public IAtsInsertionFeature getInsertionFeature(ArtifactId artifact) {
      IAtsInsertionFeature insertionFeature = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.InsertionFeature)) {
            insertionFeature = new InsertionFeature(logger, atsServer, artRead);
         } else {
            throw new OseeCoreException("Requested uuid not Insertion Feature");
         }
      }
      return insertionFeature;
   }

   @Override
   public IAtsInsertion createInsertion(ArtifactId programArtifact, JaxNewInsertion newInsertion) {

      Long uuid = newInsertion.getUuid();
      if (uuid == null || uuid <= 0) {
         uuid = Lib.generateArtifactIdAsInt();
      }
      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreService().createAtsChangeSet("Create new Insertion",
            atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.Insertion, newInsertion.getName(), GUID.create(),
            uuid);

      changes.relate(programArtifact, AtsRelationTypes.ProgramToInsertion_Insertion, insertionArt);
      changes.execute();
      return getInsertion(insertionArt);
   }

   @Override
   public IAtsInsertion updateInsertion(JaxNewInsertion updatedInsertion) {
      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreService().createAtsChangeSet("Update Insertion",
            atsServer.getUserService().getCurrentUser());
      changes.setSoleAttributeValue(atsServer.getConfig().getSoleByUuid(updatedInsertion.getUuid()),
         CoreAttributeTypes.Name, updatedInsertion.getName());
      changes.execute();
      return getInsertion(atsServer.getQuery().andUuid(updatedInsertion.getUuid()).getResults().getExactlyOne());
   }

   @Override
   public void deleteInsertion(ArtifactId artifact) {
      deleteConfigObject(artifact.getUuid(), "Delete Insertion", AtsArtifactTypes.Insertion);
   }

   @Override
   public IAtsInsertionFeature createInsertionFeature(ArtifactId insertion, JaxNewInsertionFeature newFeature) {
      Long uuid = newFeature.getUuid();
      if (uuid == null || uuid <= 0) {
         uuid = Lib.generateArtifactIdAsInt();
      }
      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreService().createAtsChangeSet("Create new Insertion Feature",
            atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionFeatureArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.InsertionFeature, newFeature.getName(),
            GUID.create(), uuid);

      changes.relate(insertion, AtsRelationTypes.InsertionToInsertionFeature_InsertionFeature, insertionFeatureArt);
      changes.execute();
      return getInsertionFeature(insertionFeatureArt);
   }

   @Override
   public IAtsInsertionFeature updateInsertionFeature(JaxNewInsertionFeature updatedFeature) {
      AtsChangeSet changes =
         (AtsChangeSet) atsServer.getStoreService().createAtsChangeSet("Update Insertion",
            atsServer.getUserService().getCurrentUser());
      ArtifactReadable insertionFeatureArt =
         atsServer.getQuery().andUuid(updatedFeature.getUuid()).getResults().getExactlyOne();

      changes.getTransaction().setSoleAttributeValue(insertionFeatureArt, CoreAttributeTypes.Name,
         updatedFeature.getName());
      changes.setSoleAttributeValue(atsServer.getConfig().getSoleByUuid(updatedFeature.getUuid()),
         CoreAttributeTypes.Name, updatedFeature.getName());
      changes.execute();
      return getInsertionFeature(atsServer.getQuery().andUuid(updatedFeature.getUuid()).getResults().getExactlyOne());
   }

   @Override
   public void deleteInsertionFeature(ArtifactId artifact) {
      deleteConfigObject(artifact.getUuid(), "Delete Insertion Feature", AtsArtifactTypes.InsertionFeature);
   }

   private void deleteConfigObject(long uuid, String comment, IArtifactType type) {
      ArtifactReadable toDelete = atsServer.getArtifactByUuid(uuid);
      if (toDelete == null) {
         throw new OseeCoreException("No object found for uuid %d", uuid);
      }

      if (!toDelete.getArtifactType().equals(type)) {
         throw new OseeCoreException("Artifact type does not match for %s", comment);
      }
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory().createTransaction(AtsUtilCore.getAtsBranch(), toDelete, comment);
      transaction.deleteArtifact(toDelete);
      transaction.commit();
   }
}
