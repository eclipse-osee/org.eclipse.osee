/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import org.eclipse.osee.framework.skynet.core.event.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;

/**
 * This listener will be called upon a artifact,relation,attribute change gets persisted to the database
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactEventListener extends IEventFilteredListener {

   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender);

}
