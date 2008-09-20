/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Donald G. Dunne
 */
public interface IAccessControlEventListener extends IEventListner {
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlModType, LoadedArtifacts loadedArtifactss);

}
