/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.session;

import java.util.Collections;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.security.JaxRsSessionProvider;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class JaxRsSessionProviderImpl implements JaxRsSessionProvider {

   private AuthenticitySessionStorage authenticitySessionStorage;
   private SessionStorage sessionStorage;
   private Log logger;
   private JdbcClient jdbcClient;

   public JaxRsSessionProviderImpl() {

   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      JdbcClient jdbcClient = jdbcService.getClient();
      this.jdbcClient = jdbcClient;
   }

   public void start() {
      authenticitySessionStorage = new AuthenticitySessionStorage(logger, jdbcClient);
      sessionStorage = new SessionStorage(logger, jdbcClient);
   }

   @Override
   public String createAuthenticitySessionToken(Long subjectId) {
      String token = getSessionAuthenticityToken(subjectId);

      if (Strings.isValid(token)) {
         authenticitySessionStorage.removeAuthenticitySessionTokens(
            Collections.singletonList(authenticitySessionStorage.getSession(subjectId)));
      }

      AuthenticityToken authenticityToken = new AuthenticityToken();
      token = UUID.randomUUID().toString();
      authenticityToken.setSubjectId(subjectId);
      authenticityToken.setToken(token);
      authenticitySessionStorage.insertAuthenticityTokens(authenticityToken);

      return token;
   }

   @Override
   public String removeSessionAuthenticityToken(Long subjectId) {
      String toReturn = "";

      AuthenticityToken authenticityToken = authenticitySessionStorage.getSession(subjectId);
      if (authenticityToken != null) {
         authenticitySessionStorage.removeAuthenticitySessionTokens(Collections.singletonList(authenticityToken));
         toReturn = authenticityToken.getToken();
      }
      return toReturn;
   }

   @Override
   public String getSessionAuthenticityToken(Long subjectId) {
      AuthenticityToken authenticityToken = authenticitySessionStorage.getSession(subjectId);
      return authenticityToken == null ? "" : authenticityToken.getToken();
   }

   @Override
   public OseePrincipal getSubjectById(Long subjectId) {
      return null;
   }

   @Override
   public HttpSession getSession() {
      return null;
   }

   @Override
   public void storeSession(SessionData session) {
      sessionStorage.insertSessions(session);
   }

   @Override
   public SessionData getSession(String sessionId) {
      return sessionStorage.getSession(sessionId);
   }

   @Override
   public SessionData removeSession(String sessionId) {
      SessionData session = sessionStorage.getSession(sessionId);
      if (session != null) {
         sessionStorage.deleteSessioin(Collections.singletonList(session));
      }
      return session;
   }

}
