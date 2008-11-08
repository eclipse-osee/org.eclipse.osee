/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactsPurgedEventListener extends IEventListner {
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException;

}
