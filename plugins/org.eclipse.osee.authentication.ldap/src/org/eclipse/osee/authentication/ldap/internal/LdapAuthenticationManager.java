/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.authentication.ldap.internal;

import static org.eclipse.osee.authentication.ldap.internal.util.LdapUtil.getValue;
import java.security.PrivilegedActionException;
import java.util.Collections;
import java.util.Locale;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.LdapConfiguration;
import org.eclipse.osee.authentication.ldap.LdapConstants;
import org.eclipse.osee.authentication.ldap.LdapCredentialsSource;
import org.eclipse.osee.authentication.ldap.LdapReferralHandlingType;
import org.eclipse.osee.authentication.ldap.internal.util.LdapUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public final class LdapAuthenticationManager {

   private final LdapClient client;

   private boolean isUserNameToLowerCaseEnabled = LdapConstants.DEFAULT_USERNAME_TO_LOWERCASE;
   private LdapCredentialsSource loginType = LdapConstants.DEFAULT_LDAP_CREDENTIALS_SOURCE;
   private LdapAuthenticationType systemAuthType = LdapConstants.DEFAULT_AUTHENTICATION_TYPE;
   private String systemUserName;
   private String systemPassword;
   private LdapFilter ldapFilter;

   public LdapAuthenticationManager(LdapClient client) {
      this.client = client;
   }

   public void configure(LdapConfiguration config) {
      String serverAddress = getValue(config.getServerAddress(), "");

      //@formatter:off
      long readTimeoutInMillis = getValue(config.getReadTimeoutInMillis(), LdapConstants.DEFAULT_LDAP_READ_TIMEOUT_IN_MILLIS);
      boolean isSslVerifyEnabled = getValue(config.isSslVerifyEnabled(), LdapConstants.DEFAULT_SSL_CERTIFICATE_VERIFICATION);
      LdapReferralHandlingType referral = getValue(config.getReferralHandlingType(), LdapConstants.DEFAULT_LDAP_REFERRAL_HANDLING);

      isUserNameToLowerCaseEnabled = getValue(config.isUserNameToLowerCaseEnabled(), LdapConstants.DEFAULT_USERNAME_TO_LOWERCASE);
      loginType = getValue(config.getCredentialsSource(), LdapConstants.DEFAULT_LDAP_CREDENTIALS_SOURCE);
      systemAuthType = getValue(config.getAuthenticationType(), LdapConstants.DEFAULT_AUTHENTICATION_TYPE);
      //@formatter:on

      systemUserName = getValue(config.getUsername(), null);
      systemPassword = getValue(config.getPassword(), null);

      client.setServerAddress(serverAddress);
      client.setReadTimeoutInMillis(readTimeoutInMillis);
      client.setSslVerifyEnabled(isSslVerifyEnabled);
      client.setReferralHandling(referral);

      ldapFilter = LdapUtil.newLdapFilter(config);
   }

   public AuthenticatedUser authenticate(AuthenticationRequest request) {
      String username = request.getUserName();
      String password = request.getPassword();
      if (isUserNameToLowerCaseEnabled) {
         username = username.toLowerCase(Locale.US);
      }
      AuthenticatedUser authenticatedUser = null;
      try {
         if (loginType.isSystemCredentials()) {
            authenticatedUser = authenticateConnectWithLdapSystemCredentials(username, password);
         } else {
            authenticatedUser = authenticateConnectWithUserCredentials(username, password);
         }
      } catch (PrivilegedActionException ex) {
         throw new OseeCoreException(ex, "Cannot connect to LDAP");
      } catch (NamingException ex) {
         throw new OseeCoreException(ex, "Cannot query LDAP for account");
      } catch (LoginException ex) {
         throw new OseeCoreException(ex, "Cannot authenticate via JAAS");
      }
      return authenticatedUser;
   }

   private AuthenticatedUser authenticateConnectWithLdapSystemCredentials(String username, String password) throws LoginException, NamingException, PrivilegedActionException {
      LdapConnection connection = null;
      try {
         connection = client.getConnection(systemAuthType, systemUserName, systemPassword);
         LdapAccount account = connection.findAccount(ldapFilter, username);
         boolean isAuthenticated = connection.authenticate(account.getDistinguishedName(), password);
         return createPrincipal(isAuthenticated, account);
      } finally {
         Lib.close(connection);
      }
   }

   private AuthenticatedUser authenticateConnectWithUserCredentials(String username, String password) throws LoginException, NamingException, PrivilegedActionException {
      LdapConnection connection = null;
      try {
         connection = client.getConnection(LdapAuthenticationType.SIMPLE, username, password);
         LdapAccount account = connection.findAccount(ldapFilter, username);
         return createPrincipal(true, account);
      } finally {
         Lib.close(connection);
      }
   }

   private AuthenticatedUser createPrincipal(boolean isAuthenticated, LdapAccount account) throws NamingException {
      AuthenticatedUser authenticated;
      if (isAuthenticated) {
         String displayName = account.getDisplayName();
         String emailAddress = account.getEmailAddress();
         String userName = account.getUserName();
         authenticated = newAuthenticated(displayName, emailAddress, userName);
      } else {
         authenticated = AuthenticationAdmin.ANONYMOUS_USER;
      }
      return authenticated;
   }

   private AuthenticatedUser newAuthenticated(final String displayName, final String emailAddress, final String userName) {
      return new AuthenticatedUser() {

         @Override
         public String getName() {
            return displayName;
         }

         @Override
         public String getDisplayName() {
            return displayName;
         }

         @Override
         public String getUserName() {
            return userName;
         }

         @Override
         public String getEmailAddress() {
            return emailAddress;
         }

         @Override
         public boolean isActive() {
            return true;
         }

         @Override
         public Iterable<String> getRoles() {
            return Collections.emptyList();
         }

         @Override
         public boolean isAuthenticated() {
            return true;
         }

      };
   }
}
