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
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeApplicationServerContext;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;

/**
 * This class is responsible for obtaining a Session and Managing its life-cycle through the life of the application.
 * 
 * @author Roberto E. Escobar
 */
public class ClientSessionManager {

   private static final ClientSessionManager instance = new ClientSessionManager();

   private OseeSession session;
   private OseeCredential credential;

   private ClientSessionManager() {
      session = null;
      credential = null;
   }

   public static ClientSessionManager getInstance() {
      return instance;
   }

   public Object getSession() {
      if (session == null) {
         // User Credentials -- get them from somewhere?;
         // Request session and Authenticate;

      }
      return session;
   }

   private void process() {

   }

   private ByteArrayInputStream asInputStream(OseeCredential credential) throws OseeWrappedException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      credential.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
   }

   private OseeSession createSession(OseeCredential credential) throws OseeAuthenticationException {
      OseeSession session = null;
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("newSession", "true");
      try {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.SESSION_CONTEXT,
                     parameters);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result =
               HttpProcessor.post(new URL(url), asInputStream(credential), "xml", "UTF-8", outputStream);
         if (result.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
            session = OseeSession.fromXml(new ByteArrayInputStream(outputStream.toByteArray()));
         }
      } catch (Exception ex) {
         throw new OseeAuthenticationException(ex);
      }
      return session;
   }
}
