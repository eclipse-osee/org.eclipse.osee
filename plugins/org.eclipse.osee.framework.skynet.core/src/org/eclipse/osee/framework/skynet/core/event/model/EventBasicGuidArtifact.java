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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidArtifact extends DefaultBasicGuidArtifact {

   private final EventModType eventModType;

   public EventBasicGuidArtifact(EventModType eventModType, Artifact artifact) {
      this(eventModType, artifact.getBranch(), artifact.getArtifactType(), artifact);
   }

   public EventBasicGuidArtifact(EventModType eventModType, ArtifactToken basicGuidArtifact) {
      this(eventModType, basicGuidArtifact.getBranch(), basicGuidArtifact.getArtifactType(), basicGuidArtifact);
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactTypeId artifactType, String guid) {
      super(branch, artifactType, guid);
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactTypeId artifactType) {
      super(branch, artifactType, GUID.create());
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, BranchId branch, ArtifactTypeId artifactType, ArtifactId artifact) {
      super(branch, artifactType, artifact);
      this.eventModType = eventModType;
   }

   public EventModType getModType() {
      return eventModType;
   }

   public static Set<EventBasicGuidArtifact> getRemoteBasicGuidArtifact1(EventModType eventModType, Collection<? extends RemoteBasicGuidArtifact1> basicGuidArtifacts) {
      if (eventModType == EventModType.ChangeType) {
         throw new OseeArgumentException("Can't be used for ChangeType");
      }
      Set<EventBasicGuidArtifact> eventArts = new HashSet<>();
      for (RemoteBasicGuidArtifact1 guidArt : basicGuidArtifacts) {
         eventArts.add(new EventBasicGuidArtifact(eventModType, guidArt.getBranch(), guidArt.getArtifactType(),
            guidArt.getArtGuid()));
      }
      return eventArts;
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
