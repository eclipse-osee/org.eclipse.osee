/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Donald G. Dunne
 */
public class Sender {

   String sourceObject;
   private final OseeClientSession oseeSession;

   public Sender(Object sourceObject, OseeClientSession oseeSession) {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
      this.oseeSession = oseeSession;
   }

   public Sender(NetworkSender networkSender) {
      this(networkSender.sourceObject, new OseeClientSession(networkSender.sessionId, networkSender.machineName,
            networkSender.userId, networkSender.machineIp, networkSender.port, networkSender.clientVersion));
   }

   public Sender(Object sourceObject) throws OseeAuthenticationRequiredException {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
      this.oseeSession = ClientSessionManager.getSession();
   }

   public boolean isRemote() throws OseeAuthenticationRequiredException {
      OseeClientSession session = ClientSessionManager.getSession();
      return !oseeSession.getId().equals(session.getId()) && oseeSession.getVersion().equals(session.getVersion());
   }

   public boolean isLocal() throws OseeAuthenticationRequiredException {
      return !isRemote();
   }

   public String getAuthor() {
      return oseeSession.getUserId();
   }

   /**
    * @return the oseeSession
    */
   public OseeClientSession getOseeSession() {
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
            oseeSession.getUserId(), oseeSession.getMachineIp(), oseeSession.getPort(), oseeSession.getVersion());
   }

   @Override
   public String toString() {
      String remote = "Source Unknown";
      try {
         remote = (isRemote() ? "Remote" : "Local");
      } catch (OseeAuthenticationRequiredException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return "Sender: " + remote + " [" + oseeSession.toString() + "  [" + sourceObject + "]]";
   }
}
