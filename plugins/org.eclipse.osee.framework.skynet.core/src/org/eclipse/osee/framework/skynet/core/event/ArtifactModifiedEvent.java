/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.event.artifact.DefaultEventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event.artifact.IEventBasicGuidArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * @author Donald G. Dunne
 */
public class ArtifactModifiedEvent extends ArtifactTransactionModifiedEvent {

   protected final Sender sender;
   protected final ArtifactModType artifactModType;
   protected final Artifact artifact;
   protected final UnloadedArtifact unloadedArtifact;
   protected final int transactionNumber;
   protected final Collection<SkynetAttributeChange> dirtySkynetAttributeChanges;

   public ArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact, int transactionNumber, Collection<SkynetAttributeChange> dirtySkynetAttributeChanges) {
      this.sender = sender;
      this.artifactModType = artifactModType;
      this.artifact = artifact;
      this.transactionNumber = transactionNumber;
      this.dirtySkynetAttributeChanges = dirtySkynetAttributeChanges;
      this.unloadedArtifact = null;
   }

   public ArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, UnloadedArtifact unloadedArtifact) {
      this.sender = sender;
      this.artifactModType = artifactModType;
      this.unloadedArtifact = unloadedArtifact;
      this.artifact = null;
      this.transactionNumber = -1;
      this.dirtySkynetAttributeChanges = null;
   }

   @Override
   public String toString() {
      return artifactModType + " - " + (artifact != null ? "Loaded" : "Unloaded") + " - " + sender + " - " + dirtySkynetAttributeChanges;
   }

   public Collection<SkynetAttributeChange> getAttributeChanges() {
      return dirtySkynetAttributeChanges;
   }

   @Override
   public Set<? extends IEventBasicGuidArtifact> getArtifactChanges() throws OseeCoreException {
      EventModType eventModType = null;
      if (artifactModType == ArtifactModType.Added) {
         eventModType = EventModType.Added;
      } else if (artifactModType == ArtifactModType.Deleted) {
         eventModType = EventModType.Deleted;
      } else if (artifactModType == ArtifactModType.Reverted) {
         eventModType = EventModType.Reloaded;
      } else {
         eventModType = EventModType.Modified;
      }
      if (artifact != null) {
         return Collections.singleton(new DefaultEventBasicGuidArtifact(eventModType, artifact));
      } else if (unloadedArtifact != null) {
         return Collections.singleton(new DefaultEventBasicGuidArtifact(eventModType, unloadedArtifact.getBranchGuid(),
               unloadedArtifact.getArtTypeGuid(), unloadedArtifact.getGuid()));
      } else {
         throw new OseeStateException("unhandled artifact change state");
      }
   }
}
