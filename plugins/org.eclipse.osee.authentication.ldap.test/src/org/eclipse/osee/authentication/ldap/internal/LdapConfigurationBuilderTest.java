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
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.LdapConfiguration;
import org.eclipse.osee.authentication.ldap.LdapConfigurationBuilder;
import org.eclipse.osee.authentication.ldap.LdapConstants;
import org.eclipse.osee.authentication.ldap.LdapCredentialsSource;
import org.eclipse.osee.authentication.ldap.LdapReferralHandlingType;
import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link LdapConfigurationBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class LdapConfigurationBuilderTest {

   private static final LdapAuthenticationType AUTHENTICATION_TYPE = LdapAuthenticationType.EXTERNAL;
   private static final LdapCredentialsSource CREDENTIALS_SOURCE = LdapCredentialsSource.USER_CREDENTIALS;
   private static final String PASSWORD = "password";
   private static final long READ_TIMEOUT = 21309123L;
   private static final LdapReferralHandlingType REFERRAL_HANDLING = LdapReferralHandlingType.FOLLOW;
   private static final String SERVER_ADDRESS = "http://address.com";
   private static final String USER_NAME = "username";
   private static final boolean SSL_VERIFY = false;
   private static final boolean USERNAME_TO_LOWERCASE = true;

   private static final LdapSearchScope ACCOUNT_SCOPE = LdapSearchScope.OBJECT_SCOPE;
   private static final String ACCOUNT_BASE = "account base";
   private static final String ACCOUNT_PATTERN = "account pattern";
   private static final String ACCOUNT_USERNAME_PATTERN = "username pattern";
   private static final String ACCOUNT_DISPLAY_NAME = "display name pattern";
   private static final String ACCOUNT_EMAIL_ADDRESS = "email address pattern";
   private static final String USERNAME_VARIABLE_NAME = "username variable name";

   private static final LdapSearchScope GROUP_SCOPE = LdapSearchScope.OBJECT_SCOPE;
   private static final String GROUP_BASE = "group base";
   private static final String GROUP_PATTERN = "group pattern";
   private static final String GROUP_NAME_PATTERN = "group name pattern";
   private static final String GROUP_BY_GROUP_MEMBER_PATTERN = "group member of pattern";
   private static final String GROUP_MEMBERS_OF = "member of pattern";
   private static final String GROUP_NAMESPACE = "namespace_blah";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private LdapConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = LdapConfigurationBuilder.newBuilder();
   }

   @Test
   public void testFields() {
      builder.authenticationType(AUTHENTICATION_TYPE);
      builder.credentialSource(CREDENTIALS_SOURCE);
      builder.password(PASSWORD);
      builder.readTimeoutInMillis(READ_TIMEOUT);
      builder.referralHandlingType(REFERRAL_HANDLING);
      builder.serverAddress(SERVER_ADDRESS);
      builder.userName(USER_NAME);
      builder.sslVerify(SSL_VERIFY);
      builder.userNameToLowerCase(USERNAME_TO_LOWERCASE);

      builder.userNameVariableName(USERNAME_VARIABLE_NAME);
      builder.accountScope(ACCOUNT_SCOPE);
      builder.accountBase(ACCOUNT_BASE);
      builder.accountPattern(ACCOUNT_PATTERN);
      builder.accountUserName(ACCOUNT_USERNAME_PATTERN);
      builder.accountDisplayName(ACCOUNT_DISPLAY_NAME);
      builder.accountEmailAddress(ACCOUNT_EMAIL_ADDRESS);

      builder.groupScope(GROUP_SCOPE);
      builder.groupBase(GROUP_BASE);
      builder.groupPattern(GROUP_PATTERN);
      builder.groupName(GROUP_NAME_PATTERN);
      builder.groupByGroupMemberPattern(GROUP_BY_GROUP_MEMBER_PATTERN);
      builder.groupMembersOf(GROUP_MEMBERS_OF);
      builder.groupNamespace(GROUP_NAMESPACE);

      LdapConfiguration actual = builder.build();

      assertEquals(AUTHENTICATION_TYPE, actual.getAuthenticationType());
      assertEquals(CREDENTIALS_SOURCE, actual.getCredentialsSource());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(READ_TIMEOUT, actual.getReadTimeoutInMillis());
      assertEquals(REFERRAL_HANDLING, actual.getReferralHandlingType());
      assertEquals(SERVER_ADDRESS, actual.getServerAddress());
      assertEquals(USER_NAME, actual.getUsername());
      assertEquals(SSL_VERIFY, actual.isSslVerifyEnabled());
      assertEquals(USERNAME_TO_LOWERCASE, actual.isUserNameToLowerCaseEnabled());

      assertEquals(USERNAME_VARIABLE_NAME, actual.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, actual.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, actual.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, actual.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, actual.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, actual.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, actual.getAccountEmailAddress());

      assertEquals(GROUP_SCOPE, actual.getGroupSearchScope());
      assertEquals(GROUP_BASE, actual.getGroupBase());
      assertEquals(GROUP_PATTERN, actual.getGroupPattern());
      assertEquals(GROUP_NAME_PATTERN, actual.getGroupName());
      assertEquals(GROUP_BY_GROUP_MEMBER_PATTERN, actual.getGroupByGroupMemberPattern());
      assertEquals(GROUP_MEMBERS_OF, actual.getGroupMembersOf());
      assertEquals(GROUP_NAMESPACE, actual.getGroupNamespace());
   }

   private static void add(Map<String, Object> props, String key, Object value) {
      props.put(key, String.valueOf(value));
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> props = new HashMap<>();

      add(props, LdapConstants.LDAP_AUTHENTICATION_TYPE, AUTHENTICATION_TYPE);
      add(props, LdapConstants.LDAP_CREDENTIALS_SOURCE, CREDENTIALS_SOURCE);
      add(props, LdapConstants.LDAP_PASSWORD, PASSWORD);
      add(props, LdapConstants.LDAP_READ_TIMEOUT, READ_TIMEOUT);
      add(props, LdapConstants.LDAP_REFERRAL_HANDLING, REFERRAL_HANDLING);
      add(props, LdapConstants.LDAP_SERVER_ADDRESS, SERVER_ADDRESS);
      add(props, LdapConstants.LDAP_USER_NAME, USER_NAME);
      add(props, LdapConstants.LDAP_SSL_VERIFY, SSL_VERIFY);
      add(props, LdapConstants.LDAP_USERNAME_TO_LOWERCASE, USERNAME_TO_LOWERCASE);

      add(props, LdapConstants.USERNAME_VARIABLE_NAME, USERNAME_VARIABLE_NAME);
      add(props, LdapConstants.ACCOUNT_SCOPE, ACCOUNT_SCOPE);
      add(props, LdapConstants.ACCOUNT_BASE, ACCOUNT_BASE);
      add(props, LdapConstants.ACCOUNT_PATTERN, ACCOUNT_PATTERN);
      add(props, LdapConstants.ACCOUNT_USERNAME_PATTERN, ACCOUNT_USERNAME_PATTERN);
      add(props, LdapConstants.ACCOUNT_DISPLAY_NAME, ACCOUNT_DISPLAY_NAME);
      add(props, LdapConstants.ACCOUNT_EMAIL_ADDRESS, ACCOUNT_EMAIL_ADDRESS);

      add(props, LdapConstants.GROUP_SCOPE, GROUP_SCOPE);
      add(props, LdapConstants.GROUP_BASE, GROUP_BASE);
      add(props, LdapConstants.GROUP_PATTERN, GROUP_PATTERN);
      add(props, LdapConstants.GROUP_NAME_PATTERN, GROUP_NAME_PATTERN);
      add(props, LdapConstants.GROUP_BY_GROUP_MEMBER_PATTERN, GROUP_BY_GROUP_MEMBER_PATTERN);
      add(props, LdapConstants.GROUP_MEMBER_OF, GROUP_MEMBERS_OF);
      add(props, LdapConstants.GROUP_NAMESPACE, GROUP_NAMESPACE);

      builder.properties(props);

      LdapConfiguration actual = builder.build();

      assertEquals(AUTHENTICATION_TYPE, actual.getAuthenticationType());
      assertEquals(CREDENTIALS_SOURCE, actual.getCredentialsSource());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(READ_TIMEOUT, actual.getReadTimeoutInMillis());
      assertEquals(REFERRAL_HANDLING, actual.getReferralHandlingType());
      assertEquals(SERVER_ADDRESS, actual.getServerAddress());
      assertEquals(USER_NAME, actual.getUsername());
      assertEquals(SSL_VERIFY, actual.isSslVerifyEnabled());
      assertEquals(USERNAME_TO_LOWERCASE, actual.isUserNameToLowerCaseEnabled());

      assertEquals(USERNAME_VARIABLE_NAME, actual.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, actual.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, actual.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, actual.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, actual.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, actual.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, actual.getAccountEmailAddress());

      assertEquals(GROUP_SCOPE, actual.getGroupSearchScope());
      assertEquals(GROUP_BASE, actual.getGroupBase());
      assertEquals(GROUP_PATTERN, actual.getGroupPattern());
      assertEquals(GROUP_NAME_PATTERN, actual.getGroupName());
      assertEquals(GROUP_BY_GROUP_MEMBER_PATTERN, actual.getGroupByGroupMemberPattern());
      assertEquals(GROUP_MEMBERS_OF, actual.getGroupMembersOf());
      assertEquals(GROUP_NAMESPACE, actual.getGroupNamespace());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.authenticationType(AUTHENTICATION_TYPE);
      builder.credentialSource(CREDENTIALS_SOURCE);
      builder.password(PASSWORD);
      builder.readTimeoutInMillis(READ_TIMEOUT);
      builder.referralHandlingType(REFERRAL_HANDLING);
      builder.serverAddress(SERVER_ADDRESS);
      builder.userName(USER_NAME);
      builder.sslVerify(SSL_VERIFY);
      builder.userNameToLowerCase(USERNAME_TO_LOWERCASE);

      builder.userNameVariableName(USERNAME_VARIABLE_NAME);
      builder.accountScope(ACCOUNT_SCOPE);
      builder.accountBase(ACCOUNT_BASE);
      builder.accountPattern(ACCOUNT_PATTERN);
      builder.accountUserName(ACCOUNT_USERNAME_PATTERN);
      builder.accountDisplayName(ACCOUNT_DISPLAY_NAME);
      builder.accountEmailAddress(ACCOUNT_EMAIL_ADDRESS);

      builder.groupScope(GROUP_SCOPE);
      builder.groupBase(GROUP_BASE);
      builder.groupPattern(GROUP_PATTERN);
      builder.groupName(GROUP_NAME_PATTERN);
      builder.groupByGroupMemberPattern(GROUP_BY_GROUP_MEMBER_PATTERN);
      builder.groupMembersOf(GROUP_MEMBERS_OF);
      builder.groupNamespace(GROUP_NAMESPACE);

      LdapConfiguration actual = builder.build();

      assertEquals(AUTHENTICATION_TYPE, actual.getAuthenticationType());
      assertEquals(CREDENTIALS_SOURCE, actual.getCredentialsSource());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(READ_TIMEOUT, actual.getReadTimeoutInMillis());
      assertEquals(REFERRAL_HANDLING, actual.getReferralHandlingType());
      assertEquals(SERVER_ADDRESS, actual.getServerAddress());
      assertEquals(USER_NAME, actual.getUsername());
      assertEquals(SSL_VERIFY, actual.isSslVerifyEnabled());
      assertEquals(USERNAME_TO_LOWERCASE, actual.isUserNameToLowerCaseEnabled());

      assertEquals(USERNAME_VARIABLE_NAME, actual.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, actual.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, actual.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, actual.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, actual.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, actual.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, actual.getAccountEmailAddress());

      assertEquals(GROUP_SCOPE, actual.getGroupSearchScope());
      assertEquals(GROUP_BASE, actual.getGroupBase());
      assertEquals(GROUP_PATTERN, actual.getGroupPattern());
      assertEquals(GROUP_NAME_PATTERN, actual.getGroupName());
      assertEquals(GROUP_BY_GROUP_MEMBER_PATTERN, actual.getGroupByGroupMemberPattern());
      assertEquals(GROUP_MEMBERS_OF, actual.getGroupMembersOf());
      assertEquals(GROUP_NAMESPACE, actual.getGroupNamespace());

      builder.authenticationType(LdapAuthenticationType.GSSAPI);
      builder.credentialSource(LdapCredentialsSource.SYSTEM_CREDENTIALS);
      builder.password("a");
      builder.readTimeoutInMillis(2131L);
      builder.referralHandlingType(LdapReferralHandlingType.IGNORE);
      builder.serverAddress("b");
      builder.userName("c");
      builder.sslVerify(true);
      builder.userNameToLowerCase(false);

      builder.userNameVariableName("j");
      builder.accountScope(LdapSearchScope.ONE_LEVEL_SCOPE);
      builder.accountBase("d");
      builder.accountPattern("e");
      builder.accountUserName("f");
      builder.accountDisplayName("g");
      builder.accountEmailAddress("h");

      builder.groupScope(LdapSearchScope.ONE_LEVEL_SCOPE);
      builder.groupBase("k");
      builder.groupPattern("l");
      builder.groupName("m");
      builder.groupByGroupMemberPattern("n");
      builder.groupMembersOf("i");
      builder.groupNamespace("n");

      assertEquals(AUTHENTICATION_TYPE, actual.getAuthenticationType());
      assertEquals(CREDENTIALS_SOURCE, actual.getCredentialsSource());
      assertEquals(PASSWORD, actual.getPassword());
      assertEquals(READ_TIMEOUT, actual.getReadTimeoutInMillis());
      assertEquals(REFERRAL_HANDLING, actual.getReferralHandlingType());
      assertEquals(SERVER_ADDRESS, actual.getServerAddress());
      assertEquals(USER_NAME, actual.getUsername());
      assertEquals(SSL_VERIFY, actual.isSslVerifyEnabled());
      assertEquals(USERNAME_TO_LOWERCASE, actual.isUserNameToLowerCaseEnabled());

      assertEquals(USERNAME_VARIABLE_NAME, actual.getUserNameVariableName());
      assertEquals(ACCOUNT_SCOPE, actual.getAccountSearchScope());
      assertEquals(ACCOUNT_BASE, actual.getAccountBase());
      assertEquals(ACCOUNT_PATTERN, actual.getAccountPattern());
      assertEquals(ACCOUNT_USERNAME_PATTERN, actual.getAccountUserName());
      assertEquals(ACCOUNT_DISPLAY_NAME, actual.getAccountDisplayName());
      assertEquals(ACCOUNT_EMAIL_ADDRESS, actual.getAccountEmailAddress());

      assertEquals(GROUP_SCOPE, actual.getGroupSearchScope());
      assertEquals(GROUP_BASE, actual.getGroupBase());
      assertEquals(GROUP_PATTERN, actual.getGroupPattern());
      assertEquals(GROUP_NAME_PATTERN, actual.getGroupName());
      assertEquals(GROUP_BY_GROUP_MEMBER_PATTERN, actual.getGroupByGroupMemberPattern());
      assertEquals(GROUP_MEMBERS_OF, actual.getGroupMembersOf());
      assertEquals(GROUP_NAMESPACE, actual.getGroupNamespace());
   }

}
