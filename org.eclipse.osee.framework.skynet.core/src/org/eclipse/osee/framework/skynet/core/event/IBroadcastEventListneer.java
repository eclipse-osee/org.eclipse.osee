/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;


/**
 * @author Donald G. Dunne
 */
public interface IBroadcastEventListneer extends IEventListner {
   public void handleBroadcastEvent(Sender sender, BroadcastEventType broadcastEventType, String[] userIds, String message);

}
