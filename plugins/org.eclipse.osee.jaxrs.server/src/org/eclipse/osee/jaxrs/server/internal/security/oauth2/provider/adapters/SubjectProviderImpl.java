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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil.saveSecurityContext;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.type.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.SubjectProvider;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;
import org.eclipse.osee.jaxrs.server.security.JaxRsSessionProvider;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SubjectProviderImpl implements SubjectProvider {

   private static final String SESSION_SECURITY_CONTEXT = "jaxrs.server.session.authentication.object";

   private final Log logger;
   private final JaxRsAuthenticator authenticator;
   private final JaxRsSessionProvider sessionDelegate;

   public SubjectProviderImpl(Log logger, JaxRsSessionProvider sessionDelegate, JaxRsAuthenticator authenticator) {
      super();
      this.logger = logger;
      this.sessionDelegate = sessionDelegate;
      this.authenticator = authenticator;
   }

   @Override
   public long getSubjectId(UserSubject subject) {
      return OAuthUtil.getUserSubjectUuid(subject);
   }

   @Override
   public String getName(UserSubject subject) {
      return OAuthUtil.getDisplayName(subject);
   }

   @Override
   public String createSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Create Session Token - subject[%s]", subject);

      String sessionToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionToken = sessionDelegate.createSessionToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (!Strings.isValid(sessionToken)) {
            sessionToken = UUID.randomUUID().toString();
            session.setAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, sessionToken);
         }
      }
      return sessionToken;
   }

   @Override
   public String getSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Get Session Token - subject[%s]", subject);

      String sessionToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionToken = sessionDelegate.getSessionToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
      }
      return sessionToken;
   }

   @Override
   public String removeSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Remove Session Token - subject[%s]", subject);

      String sessionToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionToken = sessionDelegate.removeSessionToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (sessionToken != null) {
            session.removeAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         }
      }
      return sessionToken;
   }

   @Override
   public UserSubject createUserSubject(MessageContext mc) throws OAuthServiceException {
      UserSubject subject = mc.getContent(UserSubject.class);
      if (subject == null) {
         SecurityContext securityContext = getSecurityContext(mc);
         subject = OAuthUtil.newSubject(securityContext);
      }
      return subject;
   }

   @Override
   public SecurityContext getSecurityContextFromSession(MessageContext mc) {
      SecurityContext securityContext = null;
      if (sessionDelegate != null) {
         // Add security context resolution through session delegate
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession(false);
         if (session != null) {
            securityContext = (SecurityContext) session.getAttribute(SESSION_SECURITY_CONTEXT);
         }
      }
      saveSecurityContext(mc, securityContext);
      return securityContext;
   }

   @Override
   public void authenticate(MessageContext mc, String scheme, String username, String password) {
      OseePrincipal principal = authenticate(scheme, username, password);
      SecurityContext securityContext = OAuthUtil.newSecurityContext(principal);
      if (sessionDelegate != null) {
         // Add security context resolution through session delegate
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession(true);
         session.setAttribute(SESSION_SECURITY_CONTEXT, securityContext);
      }
      saveSecurityContext(mc, securityContext);
   }

   @Override
   public UserSubject createSubject(String username, String password) {
      OseePrincipal principal = authenticate(OAuthConstants.BASIC_SCHEME, username, password);
      return OAuthUtil.newUserSubject(principal);
   }

   private OseePrincipal authenticate(String scheme, String username, String password) {
      logger.debug("Authenticate  - scheme[%s] username[%s]", scheme, username);
      return authenticator.authenticate(scheme, username, password);
   }

   private SecurityContext getSecurityContext(MessageContext mc) {
      SecurityContext securityContext = (SecurityContext) mc.get(SecurityContext.class);
      if (securityContext == null) {
         securityContext = (SecurityContext) mc.get(SecurityContext.class.getName());
      }
      if (securityContext == null) {
         securityContext = mc.getContent(SecurityContext.class);
      }
      return securityContext;
   }

   @Override
   public UserSubject getSubjectById(long subjectId) {
      MessageContext mc = new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
      UserSubject subject = mc.getContent(UserSubject.class);
      if (subject == null) {
         SecurityContext securityContext = getSecurityContext(mc);
         if (securityContext != null) {
            subject = OAuthUtil.newSubject(securityContext);
         }
      }

      long subjectId2 = getSubjectId(subject);
      if (subjectId2 != subjectId) {
         if (sessionDelegate != null) {
            OseePrincipal principal = sessionDelegate.getSubjectById(subjectId);
            subject = OAuthUtil.newUserSubject(principal);
         }
      }
      return subject;
   }

}