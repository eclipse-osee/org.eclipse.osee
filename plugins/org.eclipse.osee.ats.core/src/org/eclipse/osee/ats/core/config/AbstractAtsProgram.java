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
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.UuidNamedIdentity;

public abstract class AbstractAtsProgram extends UuidNamedIdentity implements IAtsProgram {

   private final IAtsServices services;
   private ArtifactId artifact;
   IAtsTeamDefinition teamDefinition = null;

   public AbstractAtsProgram(ArtifactId artifact, IAtsServices services) {
      super(artifact.getUuid(), artifact.getName());
      this.artifact = artifact;
      this.services = services;
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s][%s]", getUuid(), getName());
   }

   @Override
   public boolean isActive() throws OseeCoreException {
      return services.getProgramService().isActive(this);
   }

   @Override
   public ArtifactId getStoreObject() {
      return artifact;
   }

   @Override
   public void setStoreObject(ArtifactId artifact) {
      this.artifact = artifact;
   }

   @Override
   public String getName() {
      try {
         return artifact.getName();
      } catch (Exception ex) {
         return services.getProgramService().getNamespace(this);
      }
   }

   public IAtsServices getServices() {
      return services;
   }

   @Override
   public String getDescription() {
      return services.getProgramService().getDescription(this);
   }

}
