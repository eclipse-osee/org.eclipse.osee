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
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidArtifact extends DefaultBasicGuidArtifact {

   private final EventModType eventModType;

   public EventBasicGuidArtifact(EventModType eventModType, DefaultBasicGuidArtifact guidArt) {
      super(guidArt.getBranchGuid(), guidArt.getArtTypeGuid(), guidArt.getGuid());
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, String branchGuid, String artTypeGuid, String guid) {
      super(branchGuid, artTypeGuid, guid);
      this.eventModType = eventModType;
   }

   public EventBasicGuidArtifact(EventModType eventModType, Artifact artifact) throws OseeCoreException {
      this(eventModType, artifact.getBranch().getGuid(), artifact.getArtifactType().getGuid(), artifact.getGuid());
   }

   public EventBasicGuidArtifact(EventModType eventModType, IBasicGuidArtifact basicGuidArtifact) throws OseeCoreException {
      this(eventModType, basicGuidArtifact.getBranchGuid(), basicGuidArtifact.getArtTypeGuid(),
         basicGuidArtifact.getGuid());
   }

   public EventModType getModType() {
      return eventModType;
   }

   public static Set<EventBasicGuidArtifact> get(EventModType eventModType, Collection<? extends IBasicGuidArtifact> basicGuidArtifacts) throws OseeCoreException {
      if (eventModType == EventModType.ChangeType) {
         throw new OseeArgumentException("Can't be used for ChangeType");
      }
      Set<EventBasicGuidArtifact> eventArts = new HashSet<EventBasicGuidArtifact>();
      for (IBasicGuidArtifact guidArt : basicGuidArtifacts) {
         eventArts.add(new EventBasicGuidArtifact(eventModType, guidArt));
      }
      return eventArts;
   }

   public static Set<EventBasicGuidArtifact> getRemoteBasicGuidArtifact1(EventModType eventModType, Collection<? extends RemoteBasicGuidArtifact1> basicGuidArtifacts) throws OseeCoreException {
      if (eventModType == EventModType.ChangeType) {
         throw new OseeArgumentException("Can't be used for ChangeType");
      }
      Set<EventBasicGuidArtifact> eventArts = new HashSet<EventBasicGuidArtifact>();
      for (RemoteBasicGuidArtifact1 guidArt : basicGuidArtifacts) {
         eventArts.add(new EventBasicGuidArtifact(eventModType, guidArt.getBranchGuid(), guidArt.getArtTypeGuid(),
            guidArt.getArtGuid()));
      }
      return eventArts;
   }

   public DefaultBasicGuidArtifact getBasicGuidArtifact() {
      return new DefaultBasicGuidArtifact(getBranchGuid(), getArtTypeGuid(), getGuid());
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      boolean equals = false;
      if (this == obj) {
         equals = true;
      }
      if (!equals) {
         equals = super.equals(obj);
      }
      if (equals && obj instanceof EventBasicGuidArtifact) {
         EventBasicGuidArtifact other = (EventBasicGuidArtifact) obj;
         if (eventModType != other.getModType()) {
            equals = false;
         }
      }
      return equals;
   }

   @Override
   public String toString() {
      return String.format("[%s - G:%s - B:%s - A:%s]", eventModType, getGuid(), getBranchGuid(), getArtTypeGuid());
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
