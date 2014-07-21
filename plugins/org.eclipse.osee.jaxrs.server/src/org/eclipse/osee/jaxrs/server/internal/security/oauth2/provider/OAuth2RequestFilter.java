/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil.newAuthorizationRequiredResponse;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil.newUserSubject;
import java.net.URI;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerLoginHandler;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator.Subject;
import org.eclipse.osee.jaxrs.server.security.JaxRsSessionProvider;
import org.eclipse.osee.logger.Log;

/**
 * <pre>
 * This filter allows for JAX-RS APIs to be shared between OAuth access
 * and when the resource owner accesses the API without using OAuth.
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class OAuth2RequestFilter extends OAuthRequestFilter implements ResourceOwnerLoginHandler {

   private final Log logger;
   private final JaxRsAuthenticator authenticator;
   private final JaxRsSessionProvider sessionProvider;

   private volatile boolean useUserSubject;
   private volatile URI redirectURI;
   private volatile boolean ignoreBasePath;

   public OAuth2RequestFilter(Log logger, JaxRsAuthenticator authenticator, JaxRsSessionProvider sessionProvider) {
      super();
      this.logger = logger;
      this.authenticator = authenticator;
      this.sessionProvider = sessionProvider;
   }

   @Override
   public void setUseUserSubject(boolean useUserSubject) {
      super.setUseUserSubject(useUserSubject);
      this.useUserSubject = useUserSubject;
   }

   public void setRedirectURI(URI redirectURI) {
      this.redirectURI = redirectURI;
   }

   public void setIgnoreBasePath(boolean ignoreBasePath) {
      this.ignoreBasePath = ignoreBasePath;
   }

   private @Context
   HttpServletRequest request;

   @Override
   public void filter(ContainerRequestContext context) {
      if (isResourceOwnerRequest(context)) {
         handleResourceOwnerRequest(context);
      } else {
         super.filter(context);
      }
   }

   private boolean isResourceOwnerRequest(ContainerRequestContext context) {
      String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);
      return !Strings.isValid(authorizationHeader) || isAuthenticationSchemeSupported(authorizationHeader);
   }

   private boolean isAuthenticationSchemeSupported(String header) {
      return header != null && header.startsWith(OAuthConstants.BASIC_SCHEME);
   }

   private void handleResourceOwnerRequest(ContainerRequestContext context) {
      Message msg = JAXRSUtils.getCurrentMessage();
      String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);
      Object sc = sessionProvider.getFromSession(request);
      if (sc != null) {
         msg.put(SecurityContext.class, (SecurityContext) sc);
      } else {
         Response jaxRsResponse = null;
         if (isAuthenticationSchemeSupported(authorizationHeader)) {
            try {
               doBasicAuthentication(context, msg, authorizationHeader);
            } catch (Exception ex) {
               jaxRsResponse = getAuthenticationException(ex, msg);
            }
         } else {
            jaxRsResponse = getAuthorizationRequired(msg);
         }
         // Abort processing if we already have a response
         if (jaxRsResponse != null) {
            context.abortWith(jaxRsResponse);
         }
      }
   }

   private Response getAuthorizationRequired(Message msg) {
      logger.debug("authorizationRequiredResponse called");
      return newAuthorizationRequiredResponse(redirectURI, ignoreBasePath, realm, msg);
   }

   private Response getAuthenticationException(Exception ex, Message msg) {
      logger.error(ex, "Authorization error [%s]", msg.toString());
      return newAuthorizationRequiredResponse(redirectURI, ignoreBasePath, realm, msg);
   }

   private void doBasicAuthentication(ContainerRequestContext context, Message msg, String header) {
      logger.debug("doBasicAuthentication called");
      String[] basicAuthParts = OAuthUtil.decodeCredentials(header);
      String username = basicAuthParts[0];
      String password = basicAuthParts[1];
      authenticate(context, OAuthConstants.BASIC_SCHEME, username, password, msg);
   }

   private void authenticate(ContainerRequestContext context, String scheme, String username, String password, Message msg) {
      UserSubject subject = authenticate(scheme, username, password);
      SecurityContext sc = OAuthUtil.newSecurityContext(subject);
      sessionProvider.createSession(request, scheme, sc);
      msg.put(SecurityContext.class, sc);
   }

   private UserSubject authenticate(String scheme, String username, String password) {
      Subject user = authenticator.authenticate(scheme, username, password);
      return newUserSubject(user);
   }

   @Override
   public UserSubject createSubject(String username, String password) {
      return authenticate(OAuthConstants.BASIC_SCHEME, username, password);
   }

   @Override
   protected SecurityContext createSecurityContext(HttpServletRequest request, AccessTokenValidation accessTokenV) {
      UserSubject resourceOwnerSubject = accessTokenV.getTokenSubject();
      UserSubject clientSubject = accessTokenV.getClientSubject();

      UserSubject subject;
      if (resourceOwnerSubject != null || useUserSubject) {
         subject = resourceOwnerSubject;
      } else {
         subject = clientSubject;
      }
      return OAuthUtil.newSecurityContext(subject);
   }
}