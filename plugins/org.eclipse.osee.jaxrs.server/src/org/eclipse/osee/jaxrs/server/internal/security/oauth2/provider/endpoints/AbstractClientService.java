/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import java.util.UUID;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.ResourceOwnerNameProvider;
import org.apache.cxf.rs.security.oauth2.provider.SessionAuthenticityTokenProvider;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientProvider;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractClientService {

   private MessageContext mc;
   private final Log logger;
   private ClientProvider provider;

   private boolean blockUnsecureRequests;
   private SessionAuthenticityTokenProvider sessionAuthenticityTokenProvider;
   private SubjectCreator subjectCreator;
   private ResourceOwnerNameProvider resourceOwnerNameProvider;

   public AbstractClientService(Log logger) {
      this.logger = logger;
   }

   public void setDataProvider(ClientProvider provider) {
      this.provider = provider;
   }

   public void setResourceOwnerNameProvider(ResourceOwnerNameProvider resourceOwnerNameProvider) {
      this.resourceOwnerNameProvider = resourceOwnerNameProvider;
   }

   public void setSessionAuthenticityTokenProvider(SessionAuthenticityTokenProvider sessionAuthenticityTokenProvider) {
      this.sessionAuthenticityTokenProvider = sessionAuthenticityTokenProvider;
   }

   public void setSubjectCreator(SubjectCreator creator) {
      this.subjectCreator = creator;
   }

   public void setBlockUnsecureRequests(boolean blockUnsecureRequests) {
      this.blockUnsecureRequests = blockUnsecureRequests;
   }

   @Context
   public void setMessageContext(MessageContext context) {
      this.mc = context;
   }

   public MessageContext getMessageContext() {
      return mc;
   }

   protected ClientProvider getDataProvider() {
      return provider;
   }

   protected UserSubject createUserSubject(SecurityContext securityContext) {
      UserSubject subject = null;
      if (subjectCreator != null) {
         subject = subjectCreator.createUserSubject(getMessageContext(), null);
         if (subject != null) {
            return subject;
         }
      }

      subject = getMessageContext().getContent(UserSubject.class);
      if (subject != null) {
         return subject;
      } else {
         return OAuthUtil.newSubject(securityContext);
      }
   }

   protected SecurityContext getAndValidateSecurityContext() {
      MessageContext mc = getMessageContext();

      SecurityContext securityContext = (SecurityContext) mc.get(SecurityContext.class);
      if (securityContext == null) {
         securityContext = (SecurityContext) mc.get(SecurityContext.class.getName());
      }
      if (securityContext == null || securityContext.getUserPrincipal() == null) {
         throw ExceptionUtils.toNotAuthorizedException(null, null);
      }
      checkTransportSecurity();
      return securityContext;
   }

   protected void checkTransportSecurity() {
      if (!mc.getSecurityContext().isSecure()) {
         logger.warn("Unsecure HTTP, Transport Layer Security is recommended");
         if (blockUnsecureRequests) {
            throw ExceptionUtils.toBadRequestException(null, null);
         }
      }
   }

   protected void personalizeData(ClientRegistrationData data, UserSubject userSubject) {
      if (resourceOwnerNameProvider != null) {
         data.setEndUserName(resourceOwnerNameProvider.getName(userSubject));
      }
   }

   protected void addAuthenticityTokenToSession(ClientRegistrationData data, MultivaluedMap<String, String> params, UserSubject subject) {
      String sessionToken;
      if (sessionAuthenticityTokenProvider != null) {
         sessionToken = sessionAuthenticityTokenProvider.createSessionToken(getMessageContext(), params, subject, null);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (!Strings.isValid(sessionToken)) {
            sessionToken = UUID.randomUUID().toString();
            session.setAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, sessionToken);
         }
      }
      data.setAuthenticityToken(sessionToken);
   }

   protected boolean compareRequestAndSessionTokens(String requestToken, UserSubject subject) {
      MessageContext mc = getMessageContext();
      String sessionToken;
      if (sessionAuthenticityTokenProvider != null) {
         sessionToken =
            sessionAuthenticityTokenProvider.removeSessionToken(mc, mc.getHttpHeaders().getRequestHeaders(), subject);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (sessionToken != null) {
            session.removeAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         }
      }

      if (sessionToken == null || !Strings.isValid(sessionToken)) {
         return false;
      }
      return sessionToken.equals(requestToken);
   }

}