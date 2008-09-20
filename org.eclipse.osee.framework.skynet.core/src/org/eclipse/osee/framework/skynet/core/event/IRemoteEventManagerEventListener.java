/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public interface IRemoteEventManagerEventListener extends IEventListner {
   public void handleRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEventType);

}
