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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionFeature;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertion;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertionFeature;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.agile.AgileFeatureGroup;
import org.eclipse.osee.ats.core.client.agile.AgileTeam;
import org.eclipse.osee.ats.core.client.program.internal.Program;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ConfigItemFactory implements IAtsConfigItemFactory {

   private final IAtsClient atsClient;

   public ConfigItemFactory(IAtsClient atsClient) {
      this.atsClient = atsClient;
   }

   @Override
   public IAtsConfigObject getConfigObject(Object artifact) throws OseeCoreException {
      IAtsConfigObject configObject = null;
      if (artifact instanceof IAtsConfigObject) {
         configObject = (IAtsConfigObject) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.Program)) {
         return new Program(atsClient, (Artifact) artifact);
      }
      return configObject;
   }

   @Override
   public IAtsVersion getVersion(Object artifact) {
      IAtsVersion version = null;
      if (artifact instanceof IAtsVersion) {
         version = (IAtsVersion) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.Version)) {
         version = (IAtsVersion) atsClient.getConfigObject((Artifact) artifact);
      }
      return version;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(Object artifact) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      if (artifact instanceof IAtsTeamDefinition) {
         teamDef = (IAtsTeamDefinition) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.TeamDefinition)) {
         teamDef = (IAtsTeamDefinition) atsClient.getConfigObject((Artifact) artifact);
      }
      return teamDef;
   }

   @Override
   public IAtsActionableItem getActionableItem(Object artifact) throws OseeCoreException {
      IAtsActionableItem ai = null;
      if (artifact instanceof IAtsActionableItem) {
         ai = (IAtsActionableItem) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.ActionableItem)) {
         ai = (IAtsActionableItem) atsClient.getConfigObject((Artifact) artifact);
      }
      return ai;
   }

   @Override
   public IAtsProgram getProgram(Object object) {
      IAtsProgram program = null;
      if (object instanceof IAtsProgram) {
         program = (IAtsProgram) object;
      } else if ((object instanceof Artifact) && ((Artifact) object).isOfType(AtsArtifactTypes.Program)) {
         program = new Program(atsClient, (Artifact) object);
      }
      return program;
   }

   @Override
   public IAgileTeam getAgileTeam(Object artifact) {
      IAgileTeam agileTeam = null;
      if (artifact instanceof IAgileTeam) {
         agileTeam = (IAgileTeam) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.AgileTeam)) {
         agileTeam = new AgileTeam(atsClient, (Artifact) artifact);
      }
      return agileTeam;
   }

   @Override
   public IAgileFeatureGroup getAgileFeatureGroup(Object artifact) {
      IAgileFeatureGroup group = null;
      if (artifact instanceof IAgileFeatureGroup) {
         group = (IAgileFeatureGroup) artifact;
      } else if ((artifact instanceof Artifact) && ((Artifact) artifact).isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         group = new AgileFeatureGroup(atsClient, (Artifact) artifact);
      }
      return group;
   }

   @Override
   public IAtsInsertion getInsertion(ArtifactId object) {
      throw new UnsupportedOperationException("getInsertion not implemented on client");
   }

   @Override
   public IAtsInsertionFeature getInsertionFeature(ArtifactId object) {
      throw new UnsupportedOperationException("getInsertionFeature not implemented on client");
   }

   @Override
   public IAtsInsertion createInsertion(ArtifactId teamArtifact, JaxNewInsertion newInsertion) {
      throw new UnsupportedOperationException("createInsertion not implemented on client");
   }

   @Override
   public IAtsInsertion updateInsertion(JaxNewInsertion newInsertion) {
      throw new UnsupportedOperationException("updateInsertion not implemented on client");
   }

   @Override
   public void deleteInsertion(ArtifactId artifact) {
      throw new UnsupportedOperationException("deleteInsertion not implemented on client");
   }

   @Override
   public IAtsInsertionFeature createInsertionFeature(ArtifactId insertion, JaxNewInsertionFeature newFeature) {
      throw new UnsupportedOperationException("createInsertionFeature not implemented on client");
   }

   @Override
   public IAtsInsertionFeature updateInsertionFeature(JaxNewInsertionFeature newFeature) {
      throw new UnsupportedOperationException("updateInsertionFeature not implemented on client");
   }

   @Override
   public void deleteInsertionFeature(ArtifactId artifact) {
      throw new UnsupportedOperationException("deleteInsertionFeature not implemented on client");
   }
}
