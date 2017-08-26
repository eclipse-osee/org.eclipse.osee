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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.impl.HttpHeadersImpl;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.AuthorizationUtils;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.LoginSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.eclipse.osee.jaxrs.server.internal.security.util.CustomSecurityContextImpl;

/**
 * @author Roberto E. Escobar
 */
public final class OAuthUtil {

   private OAuthUtil() {
      // Utility Class
   }

   public static String asExpirationValue(long expiresIn) {
      String msg = "minutes";
      long value = TimeUnit.MINUTES.convert(expiresIn, TimeUnit.MILLISECONDS);
      if (value <= 1) {
         value = TimeUnit.SECONDS.convert(expiresIn, TimeUnit.MILLISECONDS);
         msg = "seconds";
      }
      return String.format("%s %s", value, msg);
   }

   public static String[] decodeCredentials(String header) {
      String substring = header.substring((OAuthConstants.BASIC_SCHEME + " ").length());
      return AuthorizationUtils.getBasicAuthParts(substring);
   }

   public static String encodeCredentials(String username, String password) {
      String credentials = String.format("%s:%s", username, password);
      String encoded;
      try {
         encoded = Base64Utility.encode(credentials.getBytes(JaxRsUtils.UTF_8_ENCODING));
      } catch (UnsupportedEncodingException ex) {
         throw new OAuthServiceException("Error encoded credentials");
      }
      StringBuilder builder = new StringBuilder();
      builder.append(OAuthConstants.BASIC_SCHEME);
      builder.append(" ");
      builder.append(encoded);
      return builder.toString();
   }

   public static URI computeRedirectUri(URI redirectURI, boolean ignoreBasePath, Message m, ContainerRequestContext context) {
      URI finalRedirectURI = null;
      if (redirectURI != null) {
         if (!redirectURI.isAbsolute()) {
            String endpointAddress = HttpUtils.getEndpointAddress(m);

            Object basePathProperty = m.get(Message.BASE_PATH);
            if (ignoreBasePath && basePathProperty != null && !"/".equals(basePathProperty)) {
               int index = endpointAddress.lastIndexOf(basePathProperty.toString());
               if (index != -1) {
                  endpointAddress = endpointAddress.substring(0, index);
               }
            }
            UriInfo uriInfo = context.getUriInfo();
            URI requestUrl = uriInfo.getRequestUri();
            String scheme = requestUrl.getScheme();

            String forwardedServer = getForwarderServer();
            String server;
            if (Strings.isValid(forwardedServer)) {
               server = forwardedServer;
            } else {
               int port = requestUrl.getPort();
               server = String.format("%s:%s", requestUrl.getHost(), port);
            }

            URI requestUrlWithServer = UriBuilder.fromPath(server)//
            .path(requestUrl.getPath())//
            .replaceQuery(requestUrl.getRawQuery())//
            .scheme(scheme)//
            .build();
            String continueURL = requestUrlWithServer.toASCIIString();

            finalRedirectURI = UriBuilder.fromPath(server)//
            .scheme(scheme)//
            .path(redirectURI.toString())//
            .queryParam("continueTo", continueURL).build();
         } else {
            finalRedirectURI = redirectURI;
         }
      }
      return finalRedirectURI;
   }

   public static Response newAuthorizationRequiredResponse(Exception ex, URI redirectURI, boolean ignoreBasePath, String realmName, Message m, ContainerRequestContext context) {
      HttpHeaders headers = new HttpHeadersImpl(m);
      if (redirectURI != null && JaxRsUtils.isHtmlSupported(headers.getAcceptableMediaTypes())) {
         URI finalRedirectURI = computeRedirectUri(redirectURI, ignoreBasePath, m, context);
         ResponseBuilder toReturn = Response.temporaryRedirect(finalRedirectURI);

         if (ex != null) {
            toReturn.entity("Incorrect Username/Password");
         }
         return toReturn.build();
      } else {
         ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
         StringBuilder sb = new StringBuilder();
         List<String> authHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
         if (authHeader != null && authHeader.size() > 0) {
            String firstHeader = authHeader.get(0);
            String[] authValues = Strings.isValid(firstHeader) ? firstHeader.split(" ") : null;
            if (authValues != null && authValues.length > 0) {
               sb.append(authValues[0]);
            }
         } else {
            sb.append("Basic");
         }
         if (realmName != null) {
            sb.append(" realm=\"").append(realmName).append('"');
         }
         builder.header(HttpHeaders.WWW_AUTHENTICATE, sb.toString());
         return builder.build();
      }
   }

   @SuppressWarnings("unchecked")
   public static String getForwarderServer() {
      Message currentMessage = PhaseInterceptorChain.getCurrentMessage();
      TreeMap<Object, Object> headers =
         (TreeMap<Object, Object>) currentMessage.get("org.apache.cxf.message.Message.PROTOCOL_HEADERS");
      List<String> forwarderServers = (List<String>) headers.get("X-Forwarded-Server");

      if (forwarderServers != null) {
         return forwarderServers.get(0);
      } else {
         return "";
      }
   }

   public static SecurityContext getSecurityContext(AccessTokenValidation accessTokenV, boolean useUserSubject) {
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

   public static SecurityContext newSecurityContext(UserSubject subject) {
      final OseePrincipal principal = newOseePrincipal(subject);
      return newSecurityContext(principal);
   }

   public static SecurityContext newSecurityContext(final OseePrincipal principal) {
      return new CustomSecurityContextImpl(principal);
   }

   private static final String SUBJECT_USERNAME = "username";
   private static final String SUBJECT_DISPLAY_NAME = "display.name";
   private static final String SUBJECT_EMAIL = "email";
   private static final String SUBJECT_IS_ACTIVE = "is.active";
   private static final String SUBJECT_IS_AUTHENTICATED = "is.authenticated";
   private static final String SUBJECT_OSEE_GUID = "osee.guid";

   public static UserSubject newUserSubject(OseePrincipal subject) {
      List<String> roles = new ArrayList<>();
      for (String role : subject.getRoles()) {
         roles.add(role);
      }
      UserSubject data = new UserSubject();
      data.setId(String.valueOf(subject.getGuid()));
      String userName = subject.getUserName();
      if (Strings.isValid(userName)) {
         data.setLogin(subject.getUserName());
      } else {
         data.setLogin(subject.getLogin());
      }
      data.setRoles(roles);

      Map<String, String> properties = new HashMap<>();
      properties.put(SUBJECT_USERNAME, subject.getUserName());
      String displayName = subject.getDisplayName();

      if (displayName.contains(",")) {
         String[] names = displayName.split(",");
         displayName = String.format("%s %s", names[1].trim(), names[0].trim());
      }
      properties.put(SUBJECT_DISPLAY_NAME, displayName);
      properties.put(SUBJECT_EMAIL, subject.getEmailAddress());
      properties.put(SUBJECT_IS_ACTIVE, Boolean.toString(subject.isActive()));
      properties.put(SUBJECT_IS_AUTHENTICATED, Boolean.toString(subject.isAuthenticated()));
      properties.put(SUBJECT_OSEE_GUID, subject.getOseeGuid());
      data.setProperties(properties);
      return data;
   }

   public static OseePrincipal newOseePrincipal(UserSubject subject) {
      Long id = getUserSubjectUuid(subject);
      Set<String> roles = new LinkedHashSet<>();
      List<String> sRoles = subject.getRoles();
      if (sRoles != null) {
         roles.addAll(sRoles);
      }
      return new UserSubjectWrapper(id, subject, roles);
   }

   public static Long getUserSubjectUuid(UserSubject subject) {
      String value = subject.getId();
      return Strings.isNumeric(value) ? Long.parseLong(value) : -1L;
   }

   public static String getDisplayName(UserSubject subject) {
      return getProperty(subject.getProperties(), SUBJECT_DISPLAY_NAME, subject.getLogin());
   }

   private static String getProperty(Map<String, String> props, String key, String defaultValue) {
      String toReturn = props.get(key);
      if (toReturn == null) {
         toReturn = defaultValue;
      }
      return toReturn;
   }

   public static UserSubject newSubject(SecurityContext securityContext) {
      Principal principal = securityContext.getUserPrincipal();
      UserSubject subject;
      if (principal instanceof OseePrincipal) {
         OseePrincipal oseePrincipal = (OseePrincipal) principal;
         subject = OAuthUtil.newUserSubject(oseePrincipal);
      } else {
         String name = principal != null ? principal.getName() : "UNKNOWN";
         List<String> roleNames = Collections.emptyList();
         if (securityContext instanceof LoginSecurityContext) {
            roleNames = new ArrayList<>();
            Set<Principal> roles = ((LoginSecurityContext) securityContext).getUserRoles();
            for (Principal p : roles) {
               roleNames.add(p.getName());
            }
         }
         subject = new UserSubject(name, roleNames);
      }
      return subject;
   }

   public static void saveSecurityContext(MessageContext mc, SecurityContext securityContext) {
      if (securityContext != null) {
         mc.put(SecurityContext.class, securityContext);
         mc.put(SecurityContext.class.getName(), securityContext);
      }
   }

   private static final class UserSubjectWrapper extends BaseIdentity<Long>implements OseePrincipal {

      private final UserSubject subject;
      private final Set<String> roles;

      public UserSubjectWrapper(Long id, UserSubject subject, Set<String> roles) {
         super(id);
         this.subject = subject;
         this.roles = roles;
      }

      @Override
      public String getName() {
         return getDisplayName();
      }

      @Override
      public String getLogin() {
         return subject.getLogin();
      }

      @Override
      public Set<String> getRoles() {
         return roles;
      }

      @Override
      public String getDisplayName() {
         return get(SUBJECT_DISPLAY_NAME, Strings.emptyString());
      }

      @Override
      public String getUserName() {
         return get(SUBJECT_USERNAME, Strings.emptyString());
      }

      @Override
      public String getEmailAddress() {
         return get(SUBJECT_EMAIL, Strings.emptyString());
      }

      @Override
      public boolean isActive() {
         return Boolean.valueOf(get(SUBJECT_IS_ACTIVE, Strings.emptyString()));
      }

      @Override
      public boolean isAuthenticated() {
         return Boolean.valueOf(get(SUBJECT_IS_AUTHENTICATED, Strings.emptyString()));
      }

      @Override
      public Map<String, String> getProperties() {
         return subject.getProperties();
      }

      private String get(String key, String defaultValue) {
         return getProperty(getProperties(), key, defaultValue);
      }

      @Override
      public String getOseeGuid() {
         return getProperty(getProperties(), SUBJECT_OSEE_GUID, "N/A");
      }
   }

}
