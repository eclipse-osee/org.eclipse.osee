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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeClientInfo extends BaseExchangeData {
   private static final long serialVersionUID = -4467800965183123628L;
   private static final String CLIENT_VERSION = "clientVersion";
   private static final String CLIENT_IP_ADDRESS = "clientIpAddress";
   private static final String CLIENT_PORT = "clientPort";
   private static final String CLIENT_MACHINE_NAME = "clientMachineName";

   public OseeClientInfo() {
      super();
   }

   public OseeClientInfo(String clientVersion, String machineName, String address, int port) {
      super();
      this.backingData.put(CLIENT_VERSION, clientVersion);
      this.backingData.put(CLIENT_MACHINE_NAME, machineName);
      setClientAddress(address, port);
   }

   public void setClientAddress(String address, int port) {
      this.backingData.put(CLIENT_IP_ADDRESS, address);
      this.backingData.put(CLIENT_PORT, port);
   }

   public void setClientVersion(String version) {
      this.backingData.put(CLIENT_VERSION, version);
   }

   public void setClientMachineName(String name) {
      this.backingData.put(CLIENT_MACHINE_NAME, name);
   }

   public String getClientAddress() {
      return getString(CLIENT_IP_ADDRESS);
   }

   public String getVersion() {
      return getString(CLIENT_VERSION);
   }

   public String getClientMachineName() {
      return getString(CLIENT_MACHINE_NAME);
   }

   public int getPort() {
      int toReturn = -1;
      String port = this.getString(CLIENT_PORT);
      if (Strings.isValid(port)) {
         toReturn = Integer.parseInt(port);
      }
      return toReturn;
   }
}
