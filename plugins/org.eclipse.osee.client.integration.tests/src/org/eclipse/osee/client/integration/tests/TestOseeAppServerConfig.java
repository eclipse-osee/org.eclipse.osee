/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests;

import org.eclipse.osee.client.integration.tests.OseeAppServerUtil.ServerConfig;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Assert;

public final class TestOseeAppServerConfig implements ServerConfig {

   private final String binaryDataPath;

   public TestOseeAppServerConfig(String binaryDataPath) {
      this.binaryDataPath = binaryDataPath;
   }

   @Override
   public String getJavaHome() {
      String javaHome = System.getProperty("java.home");
      Assert.assertTrue(Strings.isValid(javaHome));
      return javaHome;
   }

   @Override
   public String getServerHome() {
      String serverHome = System.getProperty("osee.application.server.home");
      Assert.assertTrue(Strings.isValid(serverHome));
      return serverHome;
   }

   @Override
   public int getServerPort() {
      String applicationServer = OseeClientProperties.getOseeApplicationServer();
      int index = applicationServer.indexOf(":");
      String portString = applicationServer.substring(index, applicationServer.length());
      if (portString.endsWith("/")) {
         portString = portString.substring(0, portString.length() - 1);
      }
      int serverPort = Integer.parseInt(portString);
      Assert.assertTrue(serverPort > 0);
      return serverPort;
   }

   @Override
   public String getServerBinaryDataPath() {
      return binaryDataPath;
   }
}