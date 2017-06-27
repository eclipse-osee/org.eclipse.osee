/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class EventChangeTypeBasicGuidArtifact extends EventBasicGuidArtifact {

   private final ArtifactTypeId fromArtTypeGuid;

   public EventChangeTypeBasicGuidArtifact(BranchId branch, ArtifactTypeId fromArtifactType, ArtifactTypeId artifactType, String guid) {
      super(EventModType.ChangeType, branch, artifactType, guid);
      this.fromArtTypeGuid = fromArtifactType;
   }

   public ArtifactTypeId getFromArtTypeGuid() {
      return fromArtTypeGuid;
   }

   @Override
   public String toString() {
      try {
         return String.format("[%s - %s from type [%s][%s] to [%s][%s]]", EventModType.ChangeType.name(), getGuid(),
            fromArtTypeGuid, ArtifactTypeManager.getType(fromArtTypeGuid), getArtifactType(),
            ArtifactTypeManager.getType(getArtifactType()));
      } catch (OseeCoreException ex) {
         return String.format("[%s - %s from type [%s] to [%s]]", EventModType.ChangeType.name(), getGuid(),
            fromArtTypeGuid, getArtifactType());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (fromArtTypeGuid == null ? 0 : fromArtTypeGuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      EventChangeTypeBasicGuidArtifact other = (EventChangeTypeBasicGuidArtifact) obj;
      if (fromArtTypeGuid == null) {
         if (other.fromArtTypeGuid != null) {
            return false;
         }
      } else if (!fromArtTypeGuid.equals(other.fromArtTypeGuid)) {
         return false;
      }
      return true;
   }

}
