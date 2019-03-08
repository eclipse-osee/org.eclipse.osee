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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.framework.server.ide.api.model.IdeVersion;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;

/**
 * @author Donald G Dunne
 */
public class DbConnectionUtility {

   private static Boolean supported;
   private static Boolean applicationServerAlive;
   private static ClientEndpoint clientEp;

   public static Result areOSEEServicesAvailable() {
      Result toReturn = Result.FalseResult;
      if (!isVersionSupported()) {
         toReturn = new Result(
            "This OSEE client version [%s] is not supported by the current application server(s).\n\nDatabase capability disabled.",
            OseeCodeVersion.getVersion());
      } else {
         toReturn = OseeUiActivator.areOSEEServicesAvailable();
      }
      return toReturn;
   }

   public static Result dbConnectionIsOkResult() {
      Result result = Result.TrueResult;
      if (!isApplicationServerAlive()) {
         result = new Result("The OSEE Application Server is not available.\n\nDatabase capability disabled.");
      } else {
         result = areOSEEServicesAvailable();
      }
      return result;
   }

   public static boolean dbConnectionIsOk() {
      return dbConnectionIsOkResult().isTrue();
   }

   public static boolean isApplicationServerAlive() {
      if (applicationServerAlive == null) {
         String address = OseeClientProperties.getOseeApplicationServer();
         if (Strings.isValid(address)) {
            try {
               getIdeClientSupportedVersions();
               applicationServerAlive = true;
            } catch (Exception ex) {
               applicationServerAlive = false;
            }
         } else {
            applicationServerAlive = false;
         }
      }
      return applicationServerAlive;
   }

   public static boolean isVersionSupported() {
      if (supported == null) {
         try {
            String address = OseeClientProperties.getOseeApplicationServer();
            if (Strings.isValid(address)) {
               String clientVersion = OseeCodeVersion.getVersion();
               if (Strings.isValid(clientVersion)) {
                  if (clientVersion.contains("Development")) {
                     supported = true;
                  } else {
                     if (clientVersion.endsWith("qualifier")) {
                        clientVersion = clientVersion.substring(0, clientVersion.length() - "qualifier".length());
                     }
                     supported = isVersionSupported(clientVersion);
                  }
               }
            } else {
               supported = false;
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return supported;
   }

   private static boolean isVersionSupported(String clientVersion) {
      for (String serverVersion : getIdeClientSupportedVersions()) {
         if (serverVersion.startsWith(clientVersion)) {
            return true;
         }
      }
      return false;
   }

   private static Collection<String> getIdeClientSupportedVersions() {
      IdeVersion clientResult = null;
      ClientEndpoint client = getClientEndpoint();
      if (client != null) {
         try {
            clientResult = client.getSupportedVersions();
         } catch (Exception ex) {
            throw JaxRsExceptions.asOseeException(ex);
         }
      }
      return clientResult != null ? clientResult.getVersions() : Collections.<String> emptySet();
   }

   private static ClientEndpoint getClientEndpoint() {
      if (clientEp == null) {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         String orcsUri = String.format("%s/ide", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().build();
         clientEp = jaxRsClient.target(orcsUri).newProxy(ClientEndpoint.class);
      }
      return clientEp;
   }

}
