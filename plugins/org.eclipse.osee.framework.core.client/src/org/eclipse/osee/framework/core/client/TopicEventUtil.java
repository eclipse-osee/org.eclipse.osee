/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.client;

import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.service.event.Event;

/**
 * @author Donald G. Dunne
 */
public class TopicEventUtil {

   public static final String SENDER_SESSION_PROPERTY = "senderSession";

   public static String getSessionJson() {
      String sessionJson = JsonUtil.toJson(ClientSessionManager.getSession());
      return sessionJson;
   }

   public static IdeClientSession getSessionJson(String sessionJson) {
      try {
         IdeClientSession oseeSession = JsonUtil.readValue(sessionJson, IdeClientSession.class);
         return oseeSession;
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static Boolean isRemoteOrNull(Event event) {
      Boolean isRemote = null;
      String sessionJson = (String) event.getProperty(SENDER_SESSION_PROPERTY);
      if (Strings.isValid(sessionJson)) {
         IdeClientSession oseeSession = getSessionJson(sessionJson);
         if (oseeSession != null) {
            isRemote = isRemote(oseeSession);
         }
      }
      return isRemote;
   }

   public static boolean isRemote(IdeClientSession oseeSession) {
      try {
         String sessionId = oseeSession.getId();
         if (!"Invalid".equalsIgnoreCase(sessionId)) {
            IdeClientSession session = ClientSessionManager.getSession();
            //Don't add version check here - can't assume events come from clients using the same version - could be old clients;
            return !sessionId.equals(session.getId());
         } else {
            return false;
         }
      } catch (OseeAuthenticationRequiredException ex) {
         return false;
      }
   }

}
