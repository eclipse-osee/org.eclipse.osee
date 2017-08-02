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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.SubjectProvider;
import org.eclipse.osee.jaxrs.server.internal.security.util.OseePrincipalImpl;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;
import org.eclipse.osee.jaxrs.server.security.JaxRsSessionProvider;
import org.eclipse.osee.jaxrs.server.session.SessionData;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SubjectProviderImpl implements SubjectProvider {

   private static final String SESSION_SECURITY_CONTEXT = "jaxrs.server.session.authentication.object";

   private final Log logger;
   private final JaxRsAuthenticator authenticator;
   private final JaxRsSessionProvider sessionDelegate;
   private final OAuthEncryption serializer;

   private volatile SecretKey secretKey;
   private String secretKeyEncoded;
   private String secretKeyAlgorithm;
   private long sessionTokenExpiration;

   public SubjectProviderImpl(Log logger, JaxRsSessionProvider sessionDelegate, JaxRsAuthenticator authenticator, OAuthEncryption serializer) {
      super();
      this.logger = logger;
      this.sessionDelegate = sessionDelegate;
      this.authenticator = authenticator;
      this.serializer = serializer;
   }

   @Override
   public void setSessionTokenExpiration(long sessionTokenExpiration) {
      this.sessionTokenExpiration = sessionTokenExpiration;
   }

   @Override
   public void setSecretKeyEncoded(String secretKeyEncoded) {
      this.secretKeyEncoded = secretKeyEncoded;
   }

   @Override
   public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
      this.secretKeyAlgorithm = secretKeyAlgorithm;
   }

   public long getSessionTokenExpiration() {
      return sessionTokenExpiration;
   }

   @Override
   public long getSubjectId(UserSubject subject) {
      return OAuthUtil.getUserSubjectUuid(subject);
   }

   @Override
   public String getName(UserSubject subject) {
      return OAuthUtil.getDisplayName(subject);
   }

   private SecretKey getSecretKey() {
      if (secretKey == null) {
         secretKey = serializer.decodeSecretKey(secretKeyEncoded, secretKeyAlgorithm);
      }
      return secretKey;
   }

   // Create Authenticity Session Token
   @Override
   public String createSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Create Session Token - subject[%s]", subject);

      String sessionAuthenticityToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionAuthenticityToken = sessionDelegate.createAuthenticitySessionToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionAuthenticityToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (!Strings.isValid(sessionAuthenticityToken)) {
            sessionAuthenticityToken = UUID.randomUUID().toString();
            session.setAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, sessionAuthenticityToken);
         }
      }
      return sessionAuthenticityToken;
   }

   // Doesn't seem to get called, removeSessionAuthenticityToken is used to retrieve the token
   @Override
   public String getSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Get Session Token - subject[%s]", subject);

      String sessionAuthenticityToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionAuthenticityToken = sessionDelegate.getSessionAuthenticityToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionAuthenticityToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
      }
      return sessionAuthenticityToken;
   }

   // Get and remove Authenticity Session Token
   @Override
   public String removeSessionToken(MessageContext mc, MultivaluedMap<String, String> params, UserSubject subject) {
      logger.debug("Remove Session Token - subject[%s]", subject);

      String sessionAuthenticityToken = null;
      if (sessionDelegate != null) {
         Long subjectId = OAuthUtil.getUserSubjectUuid(subject);
         sessionAuthenticityToken = sessionDelegate.removeSessionAuthenticityToken(subjectId);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession();
         sessionAuthenticityToken = (String) session.getAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         if (sessionAuthenticityToken != null) {
            session.removeAttribute(OAuthConstants.SESSION_AUTHENTICITY_TOKEN);
         }
      }
      return sessionAuthenticityToken;
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
         Map<String, Cookie> cookies = mc.getHttpHeaders().getCookies();
         for (String cookieName : cookies.keySet()) {
            Cookie cookie = cookies.get(cookieName);
            if (cookie.getName().equalsIgnoreCase("JSESSIONID")) {
               SessionData session = sessionDelegate.getSession(cookie.getValue());
               if (session != null) {
                  boolean isExpired = OAuthUtils.isExpired(session.getIssuedAt(), session.getExpiresIn());
                  if (isExpired) {
                     sessionDelegate.removeSession(session.getGuid());
                  } else {
                     securityContext = recreateSecurityContext(session);
                  }
               }
               break;
            }
         }
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession(false);
         if (session != null) {
            securityContext = (SecurityContext) session.getAttribute(SESSION_SECURITY_CONTEXT);
         }
      }

      saveSecurityContext(mc, securityContext);
      return securityContext;
   }

   private SecurityContext recreateSecurityContext(SessionData sessionData) {
      SessionData decryptSessionToken = serializer.decryptSessionToken(sessionData.getSubjectToken(), getSecretKey());
      UserSubject subject = decryptSessionToken.getSubject();
      Set<String> roles = new HashSet<>();
      roles.addAll(subject.getRoles());
      OseePrincipal principal = new OseePrincipalImpl(decryptSessionToken.getAccountId(),
         decryptSessionToken.getAccountDisplayName(), decryptSessionToken.getAccountEmail(), subject.getLogin(),
         sessionData.getAccountName(), decryptSessionToken.getAccountUsername(), decryptSessionToken.getAccountActive(),
         true, roles, subject.getProperties());

      return OAuthUtil.newSecurityContext(principal);
   }

   @Override
   public void authenticate(MessageContext mc, String scheme, String username, String password) {
      OseePrincipal principal = authenticate(scheme, username, password);
      SecurityContext securityContext = OAuthUtil.newSecurityContext(principal);
      if (sessionDelegate != null) {
         HttpSession session = mc.getHttpServletRequest().getSession(true);
         SessionData sessionData = httpSessionToSessionData(session, securityContext);
         sessionDelegate.storeSession(sessionData);
         session.setAttribute(SESSION_SECURITY_CONTEXT, securityContext);
      } else {
         HttpSession session = mc.getHttpServletRequest().getSession(true);
         session.setAttribute(SESSION_SECURITY_CONTEXT, securityContext);
      }
      saveSecurityContext(mc, securityContext);
   }

   private SessionData httpSessionToSessionData(HttpSession session, SecurityContext securitContext) {
      OseePrincipal principal = (OseePrincipal) securitContext.getUserPrincipal();
      SessionData toReturn = new SessionData(session.getId());

      UserSubject subject = new UserSubject();
      subject.setLogin(principal.getLogin());
      subject.setProperties(principal.getProperties());
      List<String> roles = new ArrayList<>();
      roles.addAll(principal.getRoles());
      subject.setRoles(roles);
      toReturn.setSubject(subject);

      toReturn.setAccountActive(principal.isActive());
      toReturn.setAccountDisplayName(principal.getDisplayName());
      toReturn.setAccountEmail(principal.getEmailAddress());
      toReturn.setAccountId(principal.getGuid());
      toReturn.setAccountName(principal.getName());
      toReturn.setAccountUsername(principal.getUserName());

      toReturn.setIssuedAt(OAuthUtils.getIssuedAt());
      toReturn.setExpiresIn(getSessionTokenExpiration());

      String token = serializer.encryptSessionToken(toReturn, getSecretKey());
      toReturn.setSubjectToken(token);
      return toReturn;
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

      return subject;
   }
}