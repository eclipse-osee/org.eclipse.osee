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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager.Resource;
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
public class OAuth2RequestFilter extends OAuthRequestFilter {

   private final Log logger;
   private final JaxRsResourceManager resourceManager;
   private final SubjectProvider subjectProvider;

   private volatile boolean useUserSubject;
   private volatile URI redirectURI;
   private volatile URI redirectErrorURI;
   private volatile boolean ignoreBasePath;

   public OAuth2RequestFilter(Log logger, JaxRsResourceManager resourceManager, SubjectProvider subjectProvider) {
      super();
      this.logger = logger;
      this.resourceManager = resourceManager;
      this.subjectProvider = subjectProvider;
   }

   @Override
   public void setUseUserSubject(boolean useUserSubject) {
      super.setUseUserSubject(useUserSubject);
      this.useUserSubject = useUserSubject;
   }

   public void setRedirectErrorURI(URI redirectErrorURI) {
      this.redirectErrorURI = redirectErrorURI;
   }

   public void setRedirectURI(URI redirectURI) {
      this.redirectURI = redirectURI;
   }

   public void setIgnoreBasePath(boolean ignoreBasePath) {
      this.ignoreBasePath = ignoreBasePath;
   }

   private boolean isPathSecure(ContainerRequestContext context) {
      boolean result = false;
      Resource resource = resourceManager.findResource(context);
      if (resource != null) {
         result = resource.isSecure();
      } else {
         //TODO Probably a JAX-RS endpoint -- use annotations;
         result = true;
      }
      return result;
   }

   @Override
   public void filter(ContainerRequestContext context) {
      boolean isSecurePath = isPathSecure(context);
      UriInfo uriInfo = context.getUriInfo();
      String path = Lib.getURIAbsolutePath(uriInfo);
      if (isSecurePath && path.contains("oauth2") || path.contains("accounts/self")) {
         if (isResourceOwnerRequest(context)) {
            handleResourceOwnerRequest(context);
         } else {
            super.filter(context);
         }
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
               jaxRsResponse = getAuthenticationException(ex, msg, context);
            }
         } else {
            jaxRsResponse = getAuthorizationRequired(msg, context);
         }
         // Abort processing if we already have a response
         if (jaxRsResponse != null) {
            context.abortWith(jaxRsResponse);
         }
      }
   }

   private Response getAuthorizationRequired(Message msg, ContainerRequestContext context) {
      logger.debug("authorizationRequiredResponse called");
      return newAuthorizationRequiredResponse(null, redirectURI, ignoreBasePath, realm, msg, context);
   }

   private Response getAuthenticationException(Exception ex, Message msg, ContainerRequestContext context) {
      logger.error(ex, "Authorization error [%s]", msg.toString());
      return newAuthorizationRequiredResponse(ex, redirectErrorURI, ignoreBasePath, realm, msg, context);
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