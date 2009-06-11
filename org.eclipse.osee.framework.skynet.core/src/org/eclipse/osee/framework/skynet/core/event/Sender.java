/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

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
            networkSender.userId, networkSender.machineIp, networkSender.port, networkSender.clientVersion, "n/a"));
   }

   public Sender(Object sourceObject) throws OseeAuthenticationRequiredException {
      this.sourceObject = InternalEventManager.getObjectSafeName(sourceObject);
      this.oseeSession = ClientSessionManager.getSession();
   }

   public boolean isRemote() throws OseeAuthenticationRequiredException {
      OseeClientSession session = ClientSessionManager.getSession();
      //Don't add version check here - can't assume events come from clients using the same version - could be old clients;
      return !oseeSession.getId().equals(session.getId());
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "Sender: " + remote + " [" + oseeSession.toString() + "  [" + sourceObject + "]]";
   }
}
