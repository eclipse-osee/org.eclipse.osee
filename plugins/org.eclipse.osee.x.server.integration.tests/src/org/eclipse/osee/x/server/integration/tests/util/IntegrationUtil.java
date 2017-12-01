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

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClientStandaloneSetup;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.OseeClientStandaloneSetup;

public final class IntegrationUtil {

   private IntegrationUtil() {
      // Utility class
   }

   public static OseeClient createClient() {
      OseeClient oseeClient = OseeClientStandaloneSetup.createClient(createClientConfig());
      if (!oseeClient.isLocalHost()) {
         throw new OseeStateException("This test should be run with local test server, not %s",
            oseeClient.getBaseUri());
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