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
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;

/**
 * @author Donald G. Dunne
 */
public class Sender {

   private final String sourceObject;
   private final IdeClientSession oseeSession;

   private Sender(String sourceObjectId, IdeClientSession oseeSession) {
      this.sourceObject = sourceObjectId;
      this.oseeSession = oseeSession;
   }

   public static Sender createSender(Object sourceObject, IdeClientSession oseeSession) {
      String sourceId = EventUtil.getObjectSafeName(sourceObject);
      return new Sender(sourceId, oseeSession);
   }

   public static Sender createSender(NetworkSender networkSender) {
      IdeClientSession oseeSession = new IdeClientSession();
      oseeSession.setId(networkSender.sessionId);
      oseeSession.setClientName(networkSender.machineName);
      oseeSession.setClientAddress(networkSender.machineIp);
      oseeSession.setUserId(networkSender.userId);
      oseeSession.setClientPort(String.valueOf(networkSender.port));
      oseeSession.setClientVersion(networkSender.clientVersion);
      oseeSession.setAuthenticationProtocol("N/A");
      return createSender(networkSender.sourceObject, oseeSession);
   }

   public static Sender createSender(RemoteNetworkSender1 networkSender) {
      IdeClientSession oseeSession = new IdeClientSession();
      oseeSession.setId(networkSender.getSessionId());
      oseeSession.setClientName(networkSender.getMachineName());
      oseeSession.setClientAddress(networkSender.getMachineIp());
      oseeSession.setUserId(networkSender.getUserId());
      oseeSession.setClientPort(String.valueOf(networkSender.getPort()));
      oseeSession.setClientVersion(networkSender.getClientVersion());
      oseeSession.setAuthenticationProtocol("N/A");
      return createSender(networkSender.getSourceObject(), oseeSession);
   }

   public static Sender createSender(Object sourceObject) {
      IdeClientSession oseeSession = ClientSessionManager.getSafeSession();
      return createSender(sourceObject, oseeSession);
   }

   public boolean isRemote() {
      try {
         String sessionId = oseeSession.getId();
         if (!"Invalid".equalsIgnoreCase(sessionId)) {
            IdeClientSession session = ClientSessionManager.getSession();
            //Don't add version check here - can't assume events come from clients using the same version - could be old clients;
            return !sessionId.equals(session.getId());
         } else {
            return false;
         }
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

   public IdeClientSession getOseeSession() {
      return oseeSession;
   }

   public Object getSourceObject() {
      return sourceObject;
   }

   public NetworkSender getNetworkSender() {
      return new NetworkSender(sourceObject, oseeSession.getId(), oseeSession.getClientName(), oseeSession.getUserId(),
         oseeSession.getClientAddress(), Integer.valueOf(oseeSession.getClientPort()), oseeSession.getClientVersion());
   }

   public RemoteNetworkSender1 getNetworkSenderRes() {
      RemoteNetworkSender1 sender = new RemoteNetworkSender1();
      sender.setSourceObject(sourceObject);
      sender.setSessionId(oseeSession.getId());
      sender.setMachineName(oseeSession.getClientName());
      sender.setUserId(oseeSession.getUserId());
      sender.setMachineIp(oseeSession.getClientAddress());
      sender.setPort(Integer.valueOf(oseeSession.getClientPort()));
      sender.setClientVersion(oseeSession.getClientVersion());
      return sender;
   }

   @Override
   public String toString() {
      String remote = isRemote() ? "Remote" : "Local";
      return "Sender: " + remote + " [" + oseeSession.toString() + "  [" + sourceObject + "]]";
   }
}
