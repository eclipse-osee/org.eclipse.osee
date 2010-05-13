/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;

/**
 * This listener will be called upon a artifact,relation,attribute change gets persisted to the database
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactListener extends IEventListener {

   public void handleArtifactModified(Collection<EventBasicGuidArtifact> eventArtifacts, Collection<EventBasicGuidRelation> eventRelations, Sender sender);

}
