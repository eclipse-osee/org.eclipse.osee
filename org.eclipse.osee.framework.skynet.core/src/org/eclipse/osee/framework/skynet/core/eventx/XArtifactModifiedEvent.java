/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ArtifactModType;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * @author Donald G. Dunne
 */
public class XArtifactModifiedEvent extends XModifiedEvent {

   protected final Sender sender;
   protected final ArtifactModType artifactModType;
   protected final Artifact artifact;
   protected final UnloadedArtifact unloadedArtifact;
   protected final int transactionNumber;
   protected final Collection<SkynetAttributeChange> dirtySkynetAttributeChanges;

   public XArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact, int transactionNumber, Collection<SkynetAttributeChange> dirtySkynetAttributeChanges) {
      this.sender = sender;
      this.artifactModType = artifactModType;
      this.artifact = artifact;
      this.transactionNumber = transactionNumber;
      this.dirtySkynetAttributeChanges = dirtySkynetAttributeChanges;
      this.unloadedArtifact = null;
   }

   public XArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, UnloadedArtifact unloadedArtifact) {
      this.sender = sender;
      this.artifactModType = artifactModType;
      this.unloadedArtifact = unloadedArtifact;
      this.artifact = null;
      this.transactionNumber = -1;
      this.dirtySkynetAttributeChanges = null;
   }

   @Override
   public String toString() {
      return sender.getSource() + " - " + artifactModType + " - " + (artifact != null ? "Loaded" : "Unloaded") + " - " + dirtySkynetAttributeChanges;
   }
}
