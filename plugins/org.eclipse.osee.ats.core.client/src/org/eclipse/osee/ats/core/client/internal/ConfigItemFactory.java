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
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.agile.AgileFeatureGroup;
import org.eclipse.osee.ats.core.agile.AgileTeam;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.config.AbstractConfigItemFactory;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.Country;
import org.eclipse.osee.ats.core.config.Program;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.ats.core.config.Version;
import org.eclipse.osee.ats.core.insertion.Insertion;
import org.eclipse.osee.ats.core.insertion.InsertionActivity;
import org.eclipse.osee.ats.core.model.WorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class ConfigItemFactory extends AbstractConfigItemFactory {

   private final IAtsClient atsClient;
   private final Log logger;

   public ConfigItemFactory(Log logger, IAtsClient atsClient) {
      this.logger = logger;
      this.atsClient = atsClient;
   }

   @Override
   public IAtsConfigObject getConfigObject(ArtifactId art)  {
      IAtsConfigObject configObject = null;
      if (art instanceof IAtsConfigObject) {
         configObject = (IAtsConfigObject) art;
      } else if (art instanceof Artifact) {
         Artifact artifact = (Artifact) art;
         if (artifact.isOfType(AtsArtifactTypes.Program)) {
            configObject = getProgram(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.Version)) {
            configObject = getVersion(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
            configObject = getTeamDef(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
            configObject = getActionableItem(artifact);
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
            throw new OseeArgumentException("Unhandled artifact type %s for %s", artifact.getArtifactTypeName(),
               artifact.toStringWithId());
         }
      }
      return configObject;
   }

   @Override
   public IAtsWorkPackage getWorkPackage(ArtifactId artifact) {
      IAtsWorkPackage workPackage = null;
      if (artifact instanceof Artifact) {
         Artifact art = (Artifact) artifact;
         if (art.isOfType(AtsArtifactTypes.WorkPackage)) {
            workPackage = new WorkPackage(logger, art, atsClient.getServices());
         }
      }
      return workPackage;
   }

   @Override
   public IAtsVersion getVersion(ArtifactId artifact) {
      IAtsVersion version = null;
      if (artifact instanceof Artifact) {
         Artifact art = (Artifact) artifact;
         if (art.isOfType(AtsArtifactTypes.Version)) {
            version = new Version(logger, atsClient, art);
         }
      }
      return version;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(ArtifactId artifact)  {
      IAtsTeamDefinition teamDef = null;
      if (artifact instanceof Artifact) {
         Artifact art = (Artifact) artifact;
         if (art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef = new TeamDefinition(logger, atsClient, art);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsActionableItem getActionableItem(ArtifactId artifact)  {
      IAtsActionableItem ai = null;
      if (artifact instanceof Artifact) {
         Artifact art = (Artifact) artifact;
         if (art.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai = new ActionableItem(logger, atsClient, art);
         }
      }
      return ai;
   }

   @Override
   public IAtsProgram getProgram(ArtifactId object) {
      IAtsProgram program = null;
      if (object instanceof IAtsProgram) {
         program = (IAtsProgram) object;
      } else if (object instanceof Artifact) {
         Artifact art = (Artifact) object;
         if (art.isOfType(AtsArtifactTypes.Program)) {
            program = new Program(logger, atsClient.getServices(), art);
         }
      }
      return program;
   }

   @Override
   public IAgileTeam getAgileTeam(ArtifactId artifact) {
      IAgileTeam agileTeam = null;
      if (artifact instanceof IAgileTeam) {
         agileTeam = (IAgileTeam) artifact;
      } else if (artifact instanceof Artifact && ((Artifact) artifact).isOfType(AtsArtifactTypes.AgileTeam)) {
         agileTeam = new AgileTeam(logger, atsClient, (Artifact) artifact);
      }
      return agileTeam;
   }

   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact) {
      IAgileFeatureGroup group = null;
      if (artifact instanceof IAgileFeatureGroup) {
         group = (IAgileFeatureGroup) artifact;
      } else if (artifact instanceof Artifact && ((Artifact) artifact).isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         group = new AgileFeatureGroup(logger, atsClient, (Artifact) artifact);
      }
      return group;
   }

   @Override
   public IAtsInsertion getInsertion(ArtifactId artifact) {
      IAtsInsertion result = null;
      if (artifact instanceof IAtsInsertion) {
         result = (IAtsInsertion) artifact;
      } else if (artifact instanceof Artifact) {
         Artifact art = ((Artifact) artifact);
         if (art.isOfType(AtsArtifactTypes.Insertion)) {
            result = new Insertion(logger, atsClient.getServices(), art);
         }
      }
      return result;
   }

   @Override
   public IAtsInsertionActivity getInsertionActivity(ArtifactId artifact) {
      IAtsInsertionActivity result = null;
      if (artifact instanceof IAtsInsertionActivity) {
         result = (IAtsInsertionActivity) artifact;
      } else if (artifact instanceof Artifact) {
         Artifact art = ((Artifact) artifact);
         if (art.isOfType(AtsArtifactTypes.InsertionActivity)) {
            result = new InsertionActivity(logger, atsClient.getServices(), art);
         }
      }
      return result;
   }

   @Override
   public IAtsInsertion createInsertion(ArtifactId teamArtifact, JaxInsertion newInsertion) {
      throw new UnsupportedOperationException("createInsertion not implemented on client");
   }

   @Override
   public IAtsInsertion updateInsertion(JaxInsertion newInsertion) {
      throw new UnsupportedOperationException("updateInsertion not implemented on client");
   }

   @Override
   public void deleteInsertion(ArtifactId artifact) {
      throw new UnsupportedOperationException("deleteInsertion not implemented on client");
   }

   @Override
   public IAtsInsertionActivity createInsertionActivity(ArtifactId insertion, JaxInsertionActivity newActivity) {
      throw new UnsupportedOperationException("createInsertionActivity not implemented on client");
   }

   @Override
   public IAtsInsertionActivity updateInsertionActivity(JaxInsertionActivity newActivity) {
      throw new UnsupportedOperationException("updateInsertionActivity not implemented on client");
   }

   @Override
   public void deleteInsertionActivity(ArtifactId artifact) {
      throw new UnsupportedOperationException("deleteInsertionActivity not implemented on client");
   }

   @Override
   public boolean isAtsConfigArtifact(ArtifactId artifact) {
      return getAtsConfigArtifactTypes().contains(((Artifact) artifact).getArtifactType());
   }

   @Override
   public IAtsCountry getCountry(ArtifactId artifact) {
      IAtsCountry country = null;
      if (artifact instanceof IAtsCountry) {
         country = (IAtsCountry) artifact;
      } else if (artifact instanceof Artifact) {
         Artifact art = ((Artifact) artifact);
         if (art.isOfType(AtsArtifactTypes.Country)) {
            country = new Country(logger, atsClient.getServices(), art);
         }
      }
      return country;
   }
}