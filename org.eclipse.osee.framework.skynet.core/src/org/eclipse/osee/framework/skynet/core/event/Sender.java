/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.dbinit.ApplicationServer;
import org.eclipse.osee.framework.skynet.core.dbinit.OseeSession;

/**
 * @author Donald G. Dunne
 */
public class Sender {

   String sourceObject;
   private final OseeSession oseeSession;

   public Sender(Object sourceObject, OseeSession oseeSession) {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
      this.oseeSession = oseeSession;
   }

   public Sender(NetworkSender networkSender) {
      this(networkSender.sourceObject, new OseeSession(networkSender.sessionId, networkSender.machineName,
            networkSender.userId, networkSender.machineIp));
   }

   public Sender(Object sourceObject) {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
      this.oseeSession = ApplicationServer.getOseeSession();
   }

   public boolean isRemote() {
      return oseeSession.getId() != ApplicationServer.getOseeSession().getId();
   }

   public boolean isLocal() {
      return !isRemote();
   }

   public String getAuthor() {
      return oseeSession.getUserId();
   }

   /**
    * @return the oseeSession
    */
   public OseeSession getOseeSession() {
      return oseeSession;
   }

   /**
    * @return the sender
    */
   public Object getSourceObject() {
      return sourceObject;
   }

   /**
    * @param sender the sender to set
    */
   public void setSourceObject(Object sourceObject) {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
   }

   public NetworkSender getNetworkSender() {
      return new NetworkSender(sourceObject, oseeSession.getId(), oseeSession.getMachineName(),
            oseeSession.getUserId(), oseeSession.getMachineIp());
   }

   @Override
   public String toString() {
      return "Sender: " + (isRemote() ? "Remote" : "Local") + " [" + oseeSession.toString() + "  [" + sourceObject + "]]";
   }

}
