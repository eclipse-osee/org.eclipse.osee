/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public enum EventType {

   // Event is only sent locally and not sent remotely
   LocalOnly,

   // Event is not send locally, but is sent remotely
   RemoteOnly,

   // Event is sent both locally and remotely
   LocalAndRemote
}
