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
package org.eclipse.osee.x.server.integration.tests.util;

import static org.eclipse.osee.orcs.rest.client.OseeClient.OSEE_APPLICATION_SERVER;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClientStandaloneSetup;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.OseeClientStandaloneSetup;

public final class IntegrationUtil {

   private static final int PORT = 8089;
   private static final String DEFAULT_URL = "http://localhost:" + String.valueOf(PORT);

   private IntegrationUtil() {
      // Utility class
   }

   public static OseeClient createClient() {
      Map<String, Object> config = createClientConfig();
      return OseeClientStandaloneSetup.createClient(config);
   }

   private static String getOseeApplicationServer() {
      return System.getProperty(OSEE_APPLICATION_SERVER, DEFAULT_URL);
   }

   private static Map<String, Object> createClientConfig() {
      Map<String, Object> config = new HashMap<>();
      config.put(OSEE_APPLICATION_SERVER, getOseeApplicationServer());
      return config;
   }

   public static AccountClient createAccountClient() {
      Map<String, Object> config = createClientConfig();
      return AccountClientStandaloneSetup.createClient(config);
   }

   public static int getPort() {
      String[] splitForPort = getOseeApplicationServer().split(":");
      return (splitForPort.length == 3) ? Integer.valueOf(splitForPort[2]) : PORT;
   }

}
