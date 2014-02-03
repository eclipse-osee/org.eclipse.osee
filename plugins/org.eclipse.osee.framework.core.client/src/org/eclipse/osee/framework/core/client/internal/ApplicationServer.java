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
package org.eclipse.osee.framework.core.client.internal;

import org.eclipse.osee.framework.core.data.OseeServerInfo;

public class ApplicationServer extends OseeServer {
   private OseeServerInfo serverInfo;
   private String serverAddress;

   public ApplicationServer() {
      super("Application Server");
   }

   public boolean isServerInfoValid() {
      return serverInfo != null;
   }

   public OseeServerInfo getServerInfo() {
      return serverInfo;
   }

   public void setServerInfo(OseeServerInfo serverInfo) {
      this.serverInfo = serverInfo;
      this.serverAddress = null;
   }

   public String getServerAddress() {
      if (serverAddress == null && serverInfo != null) {
         serverAddress = serverInfo.getUri().toString();
      }
      return serverAddress;
   }

}
