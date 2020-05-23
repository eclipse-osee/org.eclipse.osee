/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.server.integration.tests.util;

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClientStandaloneSetup;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.OseeClientStandaloneSetup;

public final class IntegrationUtil {

   private IntegrationUtil() {
      // Utility class
   }

   public static OseeClient createClient() {
      OseeClient oseeClient = OseeClientStandaloneSetup.createClient(createClientConfig());
      String serverAddress = OseeClientProperties.getOseeApplicationServer();

      if (!serverAddress.contains("localhost")) {
         throw new OseeStateException("This test should be run with local test server, not %s", serverAddress);
      }
      return oseeClient;
   }

   public static AccountClient createAccountClient() {
      return AccountClientStandaloneSetup.createClient(createClientConfig());
   }

   private static Map<String, Object> createClientConfig() {
      Map<String, Object> config = new HashMap<>();
      config.put(OSEE_APPLICATION_SERVER, org.eclipse.osee.framework.core.data.OseeClient.getOseeApplicationServer());
      return config;
   }
}