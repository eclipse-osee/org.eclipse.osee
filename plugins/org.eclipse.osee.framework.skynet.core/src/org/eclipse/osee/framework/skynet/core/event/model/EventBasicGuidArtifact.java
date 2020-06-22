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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidArtifact extends DefaultBasicGuidArtifact {

   private final EventModType eventModType;

   public EventBasicGuidArtifact(EventModType eventModType, Artifact artifact) {
      this(eventModType, artifact.getBranch(), artifact);
   }

   public EventBasicGuidArtifact(EventModType eventModType, ArtifactToken basicGuidArtifact) {
      this(eventModType, basicGuidArtifact.getBranch(), basicGuidArtifact);
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactTypeToken artifactType, String guid) {
      super(branch, artifactType, guid);
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactTypeToken artifactType) {
      super(branch, artifactType, GUID.create());
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactToken artifact) {
      super(branch, artifact);
      this.eventModType = eventModType;
   }

   public EventModType getModType() {
      return eventModType;
   }

   @Override
   public boolean equals(Object obj) {
      boolean equal = super.equals(obj);
      if (equal && obj instanceof EventBasicGuidArtifact) {
         EventBasicGuidArtifact other = (EventBasicGuidArtifact) obj;
         return eventModType == other.getModType();
      }
      return equal;
   }

   @Override
   public String toString() {
      return String.format("[%s - G:%s - B:%s - A:%s]", eventModType, getGuid(), getBranch().getIdString(),
         getArtifactType());
   }

   public boolean is(EventModType... eventModTypes) {
      for (EventModType eventModType : eventModTypes) {
         if (this.eventModType == eventModType) {
            return true;
         }
      }
      return false;
   }

}
