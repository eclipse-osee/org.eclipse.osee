/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
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
}
