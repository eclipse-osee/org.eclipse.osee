/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.client;

/**
 * @author Roberto E. Escobar
 */
public class OseeClientConfig {

   private String serverAddress = "";
   private String proxyAddress = "";

   public OseeClientConfig(String serverAddress) {
      this.serverAddress = serverAddress;
   }

   public String getServerAddress() {
      return serverAddress;
   }

   public void setServerAddress(String serverAddress) {
      this.serverAddress = serverAddress;
   }

   public String getProxyAddress() {
      return proxyAddress;
   }

   public void setProxyAddress(String proxyAddress) {
      this.proxyAddress = proxyAddress;
   }

   @Override
   public String toString() {
      return "OseeClientConfig [serverAddress=" + serverAddress + ", proxyAddress=" + proxyAddress + "]";
   }

}
