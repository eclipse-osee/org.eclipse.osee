/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class EventChangeTypeBasicGuidArtifact extends EventBasicGuidArtifact {

   private final ArtifactTypeId fromArtTypeGuid;

   public EventChangeTypeBasicGuidArtifact(BranchId branch, ArtifactTypeId fromArtifactType, ArtifactTypeToken artifactType, String guid) {
      super(EventModType.ChangeType, branch, artifactType, guid);
      this.fromArtTypeGuid = fromArtifactType;
   }

   public ArtifactTypeId getFromArtTypeGuid() {
      return fromArtTypeGuid;
   }

   @Override
   public String toString() {
      return String.format("[%s - %s from type [%s][%s] to [%s][%s]]", EventModType.ChangeType.name(), getGuid(),
         fromArtTypeGuid.getIdString(), fromArtTypeGuid, getArtifactType().getIdString(), getArtifactType());
   }

   @Override
   public boolean equals(Object obj) {
      boolean equal = super.equals(obj);
      if (equal && obj instanceof EventChangeTypeBasicGuidArtifact) {
         EventChangeTypeBasicGuidArtifact other = (EventChangeTypeBasicGuidArtifact) obj;
         return fromArtTypeGuid.equals(other.fromArtTypeGuid);
      }
      return equal;
   }
}