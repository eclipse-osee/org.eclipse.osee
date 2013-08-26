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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

public class ArbitrationServer extends OseeServer {

   private static final int ONE_SEC_TIMEOUT = 1000;

   public ArbitrationServer() {
      super("Arbitration Server");
   }

   public OseeServerInfo getViaArbitration() {
      OseeServerInfo serverInfo = null;
      resetStatus();
      ByteArrayOutputStream outputStream = null;
      AcquireResult result = null;
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("version", OseeCodeVersion.getVersion());
         String url =
            HttpUrlBuilderClient.getInstance().getOsgiArbitrationServiceUrl(OseeServerContext.LOOKUP_CONTEXT,
               parameters);

         outputStream = new ByteArrayOutputStream();
         result = HttpProcessor.acquire(new URL(url), outputStream, ONE_SEC_TIMEOUT);
         setAlive(true);
         set(Level.INFO, null, HttpUrlBuilderClient.getInstance().getArbitrationServerPrefix());
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            serverInfo = getServerInfo(outputStream, result);
         } else {
            String arbitrationServerMessage = result.getResult();
            if (Strings.isValid(arbitrationServerMessage)) {
               set(Level.SEVERE, null, arbitrationServerMessage);
            } else {
               set(Level.SEVERE, null, "Error requesting application server for version [%s]",
                  OseeCodeVersion.getVersion());
            }
         }
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         set(Level.SEVERE, ex, "Error connecting to arbitration server - [%s]", ex.getLocalizedMessage());
      } finally {
         Lib.close(outputStream);
      }
      return serverInfo;
   }

   private OseeServerInfo getServerInfo(ByteArrayOutputStream outputStream, AcquireResult result) {
      OseeServerInfo serverInfo = null;
      try {
         ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
         serverInfo = OseeServerInfo.fromXml(inputStream);
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         set(Level.SEVERE, ex, "Error parsing arbitration server response - [%s]", ex.getLocalizedMessage());
      }
      return serverInfo;
   }

}
