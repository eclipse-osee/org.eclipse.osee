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
import java.net.URI;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
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
public class OAuth2RequestFilter extends OAuthRequestFilter {

   private final Log logger;
   private final SubjectProvider subjectProvider;

   private volatile boolean useUserSubject;
   private volatile URI redirectURI;
   private volatile boolean ignoreBasePath;

   public OAuth2RequestFilter(Log logger, SubjectProvider subjectProvider) {
      super();
      this.logger = logger;
      this.subjectProvider = subjectProvider;
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
      MessageContext mc = getMessageContext();

      SecurityContext sc = subjectProvider.getSecurityContextFromSession(mc);
      if (sc == null) {
         String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);

         Response jaxRsResponse = null;
         if (isAuthenticationSchemeSupported(authorizationHeader)) {
            try {
               doBasicAuthentication(mc, authorizationHeader);
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

   private void doBasicAuthentication(MessageContext mc, String header) {
      logger.debug("doBasicAuthentication called");
      String[] basicAuthParts = OAuthUtil.decodeCredentials(header);
      String username = basicAuthParts[0];
      String password = basicAuthParts[1];
      subjectProvider.authenticate(mc, OAuthConstants.BASIC_SCHEME, username, password);
   }

   @Override
   protected SecurityContext createSecurityContext(HttpServletRequest request, AccessTokenValidation accessTokenV) {
      return OAuthUtil.getSecurityContext(accessTokenV, useUserSubject);
   }
}