/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactModifiedEventListener extends IEventListner {
   public void handleArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact);

}
