/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.client.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ClientSession {

   private final String clientAddress;
   private final String clientPort;
   private final String userId;
   private final String clientVersion;
   private final String sessionId;
   private final String createdOn;

   public ClientSession(String clientAddress, String clientPort, String userId, String clientVersion, String sessionId, String createdOn) {
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
      this.userId = userId;
      this.clientVersion = clientVersion;
      this.sessionId = sessionId;
      this.createdOn = createdOn;
   }

   public String getClientAddress() {
      return clientAddress;
   }

   public String getClientPort() {
      return clientPort;
   }

   public String getUserId() {
      return userId;
   }

   public String getClientVersion() {
      return clientVersion;
   }

   public String getSessionId() {
      return sessionId;
   }

   public String getCreatedOn() {
      return createdOn;
   }
}