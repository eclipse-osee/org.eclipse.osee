/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authentication.ldap.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.security.PrivilegedActionException;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.LdapConfiguration;
import org.eclipse.osee.authentication.ldap.LdapCredentialsSource;
import org.eclipse.osee.authentication.ldap.LdapReferralHandlingType;
import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * Test Case for {@link LdapAuthenticationManager}
 *
 * @author Roberto E. Escobar
 */
public class LdapAuthenticationManagerTest {

   private static final LdapAuthenticationType AUTHENTICATION_TYPE = LdapAuthenticationType.GSSAPI;
   private static final LdapCredentialsSource CREDENTIALS_SOURCE = LdapCredentialsSource.SYSTEM_CREDENTIALS;
   private static final String LDAP_PASSWORD = "systemPassword";
   private static final long READ_TIMEOUT = 21309123L;
   private static final LdapReferralHandlingType REFERRAL_HANDLING = LdapReferralHandlingType.FOLLOW;
   private static final String SERVER_ADDRESS = "http://address.com";
   private static final String LDAP_USERNAME = "systemUser";
   private static final boolean SSL_VERIFY = false;
   private static final boolean USERNAME_TO_LOWERCASE = false;

   private static final LdapSearchScope ACCOUNT_SCOPE = LdapSearchScope.OBJECT_SCOPE;
   private static final String ACCOUNT_BASE = "account base";
   private static final String ACCOUNT_PATTERN = "account pattern";
   private static final String ACCOUNT_USERNAME_PATTERN = "username pattern";
   private static final String ACCOUNT_DISPLAY_NAME = "display name pattern";
   private static final String ACCOUNT_EMAIL_ADDRESS = "email address pattern";
   private static final String GROUP_MEMBER_OF = "member of pattern";
   private static final String USERNAME_VARIABLE_NAME = "username variable name";

   private static final LdapSearchScope GROUP_SCOPE = LdapSearchScope.OBJECT_SCOPE;
   private static final String GROUP_BASE = "group base";
   private static final String GROUP_PATTERN = "group pattern";
   private static final String GROUP_NAME_PATTERN = "group name pattern";
   private static final String GROUP_MEMBER_PATTERN = "group member of pattern";

   private static final String USERNAME = "USER_1";
   private static final String PASSWORD = "0123456789";
   private static final String EMAIL = "user1@email.com";
   private static final String DISPLAY_NAME = "Billy Bob";
   private static final String LOWER_CASED_USERNAME = USERNAME.toLowerCase();

   //@formatter:off
   @Mock private LdapClient client;

   @Mock private LdapConfiguration config;
   @Mock private AuthenticationRequest request;
   @Mock private LdapConnection connection;
   @Mock private LdapAccount account;
   @Captor private ArgumentCaptor<LdapFilter> filterCaptor;
   //@formatter:on

   private LdapAuthenticationManager manager;

   @Before
   public void setUp() {
      initMocks(this);

      manager = new LdapAuthenticationManager(client);

      when(config.getAuthenticationType()).thenReturn(AUTHENTICATION_TYPE);
      when(config.getCredentialsSource()).thenReturn(CREDENTIALS_SOURCE);
      when(config.getPassword()).thenReturn(LDAP_PASSWORD);
      when(config.getReadTimeoutInMillis()).thenReturn(READ_TIMEOUT);
      when(config.getReferralHandlingType()).thenReturn(REFERRAL_HANDLING);
      when(config.getServerAddress()).thenReturn(SERVER_ADDRESS);
      when(config.getUsername()).thenReturn(LDAP_USERNAME);
      when(config.isSslVerifyEnabled()).thenReturn(SSL_VERIFY);
      when(config.isUserNameToLowerCaseEnabled()).thenReturn(USERNAME_TO_LOWERCASE);

      when(config.getUserNameVariableName()).thenReturn(USERNAME_VARIABLE_NAME);

      when(config.getAccountSearchScope()).thenReturn(ACCOUNT_SCOPE);
      when(config.getAccountBase()).thenReturn(ACCOUNT_BASE);
      when(config.getAccountPattern()).thenReturn(ACCOUNT_PATTERN);
      when(config.getAccountUserName()).thenReturn(ACCOUNT_USERNAME_PATTERN);
      when(config.getAccountDisplayName()).thenReturn(ACCOUNT_DISPLAY_NAME);
      when(config.getAccountEmailAddress()).thenReturn(ACCOUNT_EMAIL_ADDRESS);

      when(config.getGroupSearchScope()).thenReturn(GROUP_SCOPE);
      when(config.getGroupBase()).thenReturn(GROUP_BASE);
      when(config.getGroupPattern()).thenReturn(GROUP_PATTERN);
      when(config.getGroupName()).thenReturn(GROUP_NAME_PATTERN);
      when(config.getGroupByGroupMemberPattern()).thenReturn(GROUP_MEMBER_PATTERN);
      when(config.getGroupMembersOf()).thenReturn(GROUP_MEMBER_OF);

      manager.configure(config);
   }

   @Test
   public void testLdapAuthenticationWithSystemCredentials() throws LoginException, NamingException, PrivilegedActionException {
      when(request.getUserName()).thenReturn(USERNAME);
      when(request.getPassword()).thenReturn(PASSWORD);

      when(account.getDistinguishedName()).thenReturn(USERNAME);
      try (LdapConnection LConn = client.getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
         when(LConn).thenReturn(connection);
      }
      when(connection.findAccount(any(LdapFilter.class), eq(USERNAME))).thenReturn(account);
      when(connection.authenticate(USERNAME, PASSWORD)).thenReturn(true);
      when(account.getDisplayName()).thenReturn(DISPLAY_NAME);
      when(account.getEmailAddress()).thenReturn(EMAIL);
      when(account.getUserName()).thenReturn(USERNAME);

      AuthenticatedUser actual = manager.authenticate(request);

      verify(connection).findAccount(filterCaptor.capture(), eq(USERNAME));
      verify(connection).authenticate(USERNAME, PASSWORD);
      try (LdapConnection LConn2 = client.getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
         verify(LConn2);
      }
      verify(connection).close();

      LdapFilter filter = filterCaptor.getValue();
      assertEquals(USERNAME_VARIABLE_NAME, filter.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, filter.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, filter.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, filter.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, filter.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, filter.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, filter.getAccountEmailAddress());
      assertEquals(GROUP_MEMBER_OF, filter.getGroupMembersOf());

      assertEquals(DISPLAY_NAME, actual.getName());
      assertEquals(DISPLAY_NAME, actual.getDisplayName());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(EMAIL, actual.getEmailAddress());
      assertEquals(true, actual.isActive());
      assertEquals(true, actual.isAuthenticated());

      assertEquals(false, actual.getRoles().iterator().hasNext());
   }

   @Test
   public void testLdapAuthenticationWithSystemCredentialsFailedPassword() throws LoginException, NamingException, PrivilegedActionException {
      when(request.getUserName()).thenReturn(USERNAME);
      when(request.getPassword()).thenReturn(PASSWORD);

      when(account.getDistinguishedName()).thenReturn(USERNAME);
      try (LdapConnection LConn = client.getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
         when(LConn).thenReturn(connection);
      }
      when(connection.findAccount(any(LdapFilter.class), eq(USERNAME))).thenReturn(account);
      when(connection.authenticate(USERNAME, PASSWORD)).thenReturn(false);
      when(account.getDisplayName()).thenReturn(DISPLAY_NAME);
      when(account.getEmailAddress()).thenReturn(EMAIL);
      when(account.getUserName()).thenReturn(USERNAME);

      AuthenticatedUser actual = manager.authenticate(request);

      verify(connection).findAccount(filterCaptor.capture(), eq(USERNAME));
      verify(connection).authenticate(USERNAME, PASSWORD);

      try (LdapConnection LConn2 = client.getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
         verify(LConn2);
      }
      verify(connection).close();

      LdapFilter filter = filterCaptor.getValue();
      assertEquals(USERNAME_VARIABLE_NAME, filter.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, filter.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, filter.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, filter.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, filter.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, filter.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, filter.getAccountEmailAddress());
      assertEquals(GROUP_MEMBER_OF, filter.getGroupMembersOf());

      assertEquals(AuthenticationAdmin.ANONYMOUS_USER, actual);
      assertEquals("Anonymous", actual.getDisplayName());
      assertEquals("", actual.getUserName());
      assertEquals("", actual.getEmailAddress());
      assertEquals(true, actual.isActive());
      assertEquals(false, actual.isAuthenticated());

      assertEquals(false, actual.getRoles().iterator().hasNext());
   }

   @Test
   public void testLdapAuthenticationWithUserCredentials() throws LoginException, NamingException, PrivilegedActionException {
      when(request.getUserName()).thenReturn(USERNAME);
      when(request.getPassword()).thenReturn(PASSWORD);

      // Change Configuration
      when(config.getCredentialsSource()).thenReturn(LdapCredentialsSource.USER_CREDENTIALS);
      manager.configure(config);
      try (LdapConnection LConn = client.getConnection(LdapAuthenticationType.SIMPLE, USERNAME, PASSWORD)) {
         when(LConn).thenReturn(connection);
      }
      when(connection.findAccount(any(LdapFilter.class), eq(USERNAME))).thenReturn(account);

      when(account.getDisplayName()).thenReturn(DISPLAY_NAME);
      when(account.getEmailAddress()).thenReturn(EMAIL);
      when(account.getUserName()).thenReturn(USERNAME);

      AuthenticatedUser actual = manager.authenticate(request);

      try (LdapConnection LConn2 = client.getConnection(LdapAuthenticationType.SIMPLE, USERNAME, PASSWORD)) {
         verify(LConn2);
      }
      verify(connection).findAccount(filterCaptor.capture(), eq(USERNAME));
      verify(connection, times(0)).authenticate(USERNAME, PASSWORD);
      verify(connection).close();

      LdapFilter filter = filterCaptor.getValue();
      assertEquals(USERNAME_VARIABLE_NAME, filter.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, filter.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, filter.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, filter.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, filter.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, filter.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, filter.getAccountEmailAddress());
      assertEquals(GROUP_MEMBER_OF, filter.getGroupMembersOf());

      assertEquals(DISPLAY_NAME, actual.getName());
      assertEquals(DISPLAY_NAME, actual.getDisplayName());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(EMAIL, actual.getEmailAddress());
      assertEquals(true, actual.isActive());
      assertEquals(true, actual.isAuthenticated());

      assertEquals(false, actual.getRoles().iterator().hasNext());
   }

   @Test
   public void testLdapAuthenticationConvertUserNameToLowerCase() throws LoginException, NamingException, PrivilegedActionException {
      when(request.getUserName()).thenReturn(USERNAME);
      when(request.getPassword()).thenReturn(PASSWORD);

      when(config.isUserNameToLowerCaseEnabled()).thenReturn(true);
      manager.configure(config);

      when(account.getDistinguishedName()).thenReturn(LOWER_CASED_USERNAME);

      try (LdapConnection LConn = client.getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
         when(LConn).thenReturn(connection);
      }
      when(connection.findAccount(any(LdapFilter.class), eq(LOWER_CASED_USERNAME))).thenReturn(account);
      when(connection.authenticate(LOWER_CASED_USERNAME, PASSWORD)).thenReturn(true);
      when(account.getDisplayName()).thenReturn(DISPLAY_NAME);
      when(account.getEmailAddress()).thenReturn(EMAIL);
      when(account.getUserName()).thenReturn(LOWER_CASED_USERNAME);

      AuthenticatedUser actual = manager.authenticate(request);

      verify(connection).findAccount(filterCaptor.capture(), eq(LOWER_CASED_USERNAME));
      verify(connection).authenticate(LOWER_CASED_USERNAME, PASSWORD);
      try (LdapConnection conn =
         verify(client).getConnection(LdapAuthenticationType.GSSAPI, LDAP_USERNAME, LDAP_PASSWORD)) {
      }
      verify(connection).close();

      LdapFilter filter = filterCaptor.getValue();
      assertEquals(USERNAME_VARIABLE_NAME, filter.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, filter.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, filter.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, filter.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, filter.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, filter.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, filter.getAccountEmailAddress());
      assertEquals(GROUP_MEMBER_OF, filter.getGroupMembersOf());

      assertEquals(DISPLAY_NAME, actual.getName());
      assertEquals(DISPLAY_NAME, actual.getDisplayName());
      assertEquals(LOWER_CASED_USERNAME, actual.getUserName());
      assertEquals(EMAIL, actual.getEmailAddress());
      assertEquals(true, actual.isActive());
      assertEquals(true, actual.isAuthenticated());

      assertEquals(false, actual.getRoles().iterator().hasNext());
   }

}
