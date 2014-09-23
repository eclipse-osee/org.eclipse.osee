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
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.config.AbstractAtsProgram;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public abstract class AbstractAtsProgramClient extends AbstractAtsProgram implements IAtsProgramClient {

   IAtsTeamDefinition teamDefinition = null;
   private Artifact artifact;

   public AbstractAtsProgramClient(Artifact artifact) {
      super((artifact != null ? Long.valueOf(artifact.getArtId()) : 0L),
         (artifact != null ? artifact.getName() : "Null"));
      this.artifact = artifact;
   }

   @Override
   public boolean isActive() throws OseeCoreException {
      return getArtifact().getSoleAttributeValue(AtsAttributeTypes.Active, false);
   }

   @Override
   public Object getStoreObject() {
      return artifact;
   }

   @Override
   public void setStoreObject(Object object) {
      this.artifact = (Artifact) object;
   }

   @Override
   public String getName() {
      try {
         return getArtifact().getName();
      } catch (Exception ex) {
         return this.getStaticIdPrefix();
      }
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions() throws OseeCoreException {
      return getTeamDefinition();
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      if (teamDefinition == null) {
         teamDefinition =
            AtsClientService.get().getConfig().getSoleByGuid(
               artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, ""), IAtsTeamDefinition.class);
      }
      return teamDefinition;
   }

   @Override
   public String getStaticIdPrefix() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Namespace, "");
   }

   @Override
   public String getProgramName() throws OseeCoreException {
      return getName();
   }

   @Override
   public Artifact getArtifact() throws OseeCoreException {
      return artifact;
   }

   @Override
   public void setArtifact(Artifact artifact) throws OseeCoreException {
      this.artifact = artifact;
   }

   @Override
   public String getGuid() {
      return artifact.getGuid();
   }

   @Override
   public String getDescription() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Description, "");
   }

   @Override
   public String getNamespace() {
      return getStaticIdPrefix();
   }

}
