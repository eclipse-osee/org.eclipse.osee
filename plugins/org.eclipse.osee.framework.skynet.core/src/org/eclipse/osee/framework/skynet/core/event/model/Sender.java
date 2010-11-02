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
package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;

/**
 * @author Donald G. Dunne
 */
public class Sender {

   private final String sourceObject;
   private final OseeClientSession oseeSession;

   public Sender(Object sourceObject, OseeClientSession oseeSession) {
      this.sourceObject = EventUtil.getObjectSafeName(sourceObject);
      this.oseeSession = oseeSession;
   }

   public Sender(NetworkSender networkSender) {
      this(networkSender.sourceObject, new OseeClientSession(networkSender.sessionId, networkSender.machineName,
         networkSender.userId, networkSender.machineIp, networkSender.port, networkSender.clientVersion, "n/a"));
   }

   public Sender(RemoteNetworkSender1 networkSender) {
      this(networkSender.getSourceObject(), new OseeClientSession(networkSender.getSessionId(),
         networkSender.getMachineName(), networkSender.getUserId(), networkSender.getMachineIp(),
         networkSender.getPort(), networkSender.getClientVersion(), "n/a"));
   }

   public Sender(Object sourceObject) throws OseeAuthenticationRequiredException {
      this.sourceObject = EventUtil.getObjectSafeName(sourceObject);
      this.oseeSession = ClientSessionManager.getSession();
   }

   public boolean isRemote() {
      try {
         OseeClientSession session = ClientSessionManager.getSession();
         //Don't add version check here - can't assume events come from clients using the same version - could be old clients;
         return !oseeSession.getId().equals(session.getId());
      } catch (OseeAuthenticationRequiredException ex) {
         return false;
      }
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
   public OseeClientSession getOseeSession() {
      return oseeSession;
   }

   /**
    * @return the sender
    */
   public Object getSourceObject() {
      return sourceObject;
   }

   public NetworkSender getNetworkSender() {
      return new NetworkSender(sourceObject, oseeSession.getId(), oseeSession.getMachineName(),
         oseeSession.getUserId(), oseeSession.getMachineIp(), oseeSession.getPort(), oseeSession.getVersion());
   }

   public RemoteNetworkSender1 getNetworkSenderRes() {
      RemoteNetworkSender1 sender = new RemoteNetworkSender1();
      sender.setSourceObject(sourceObject);
      sender.setSessionId(oseeSession.getId());
      sender.setMachineName(oseeSession.getMachineName());
      sender.setUserId(oseeSession.getUserId());
      sender.setMachineIp(oseeSession.getMachineIp());
      sender.setPort(oseeSession.getPort());
      sender.setClientVersion(oseeSession.getVersion());
      return sender;
   }

   @Override
   public String toString() {
      String remote = isRemote() ? "Remote" : "Local";
      return "Sender: " + remote + " [" + oseeSession.toString() + "  [" + sourceObject + "]]";
   }
}
