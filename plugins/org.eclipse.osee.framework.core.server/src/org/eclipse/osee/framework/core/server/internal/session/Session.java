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
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Date;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Roberto E. Escobar
 */
public class Session extends BaseIdentity<String> implements ISession {

   private final String userId;
   private final Date creationDate;
   private final String clientVersion;
   private String clientMachineName;
   private String clientAddress;
   private int clientPort;

   public Session(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
      super(guid);
      this.userId = userId;
      this.creationDate = creationDate;
      this.clientVersion = clientVersion;
      this.clientMachineName = clientMachineName;
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   @Override
   public String getClientMachineName() {
      return clientMachineName;
   }

   @Override
   public String getClientVersion() {
      return clientVersion;
   }

   @Override
   public String getClientAddress() {
      return clientAddress;
   }

   @Override
   public Date getCreationDate() {
      return creationDate;
   }

   @Override
   public int getClientPort() {
      return clientPort;
   }

   public void setClientMachineName(String clientMachineName) {
      this.clientMachineName = clientMachineName;
   }

   public void setClientAddress(String clientAddress) {
      this.clientAddress = clientAddress;
   }

   public void setClientPort(int clientPort) {
      this.clientPort = clientPort;
   }
}