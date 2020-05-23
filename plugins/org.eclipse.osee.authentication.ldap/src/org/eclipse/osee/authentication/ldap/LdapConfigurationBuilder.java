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

package org.eclipse.osee.authentication.ldap;

import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_BASE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_DISPLAY_NAME;
import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_EMAIL_ADDRESS;
import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_PATTERN;
import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_SCOPE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.ACCOUNT_USERNAME_PATTERN;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_AUTHENTICATION_TYPE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_GROUP_NAMESPACE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_LDAP_CREDENTIALS_SOURCE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_LDAP_READ_TIMEOUT_IN_MILLIS;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_LDAP_REFERRAL_HANDLING;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_PASSWORD;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_SEARCH_SCOPE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_SSL_CERTIFICATE_VERIFICATION;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_USERNAME;
import static org.eclipse.osee.authentication.ldap.LdapConstants.DEFAULT_USERNAME_TO_LOWERCASE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_BASE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_BY_GROUP_MEMBER_PATTERN;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_MEMBER_OF;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_NAMESPACE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_NAME_PATTERN;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_PATTERN;
import static org.eclipse.osee.authentication.ldap.LdapConstants.GROUP_SCOPE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_AUTHENTICATION_TYPE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_CREDENTIALS_SOURCE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_PASSWORD;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_READ_TIMEOUT;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_REFERRAL_HANDLING;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_SERVER_ADDRESS;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_SSL_VERIFY;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_USERNAME_TO_LOWERCASE;
import static org.eclipse.osee.authentication.ldap.LdapConstants.LDAP_USER_NAME;
import static org.eclipse.osee.authentication.ldap.LdapConstants.PATTERN_DEFAULT;
import static org.eclipse.osee.authentication.ldap.LdapConstants.USERNAME_VARIABLE_NAME;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Class to build an LdapConfiguration Object
 * 
 * <pre>
 *  serverAddress = ldap://ldap.company.com
 *  ldapAuthentication = simple
 *  ldapUserName [blank if no authentication]
 *  ldapPassword [blank if no authentication]
 * 
 * 
 *  accountScope = subtree
 *  accountBase = dc=company,dc=com
 *  accountSearchPattern = (&(objectClass=person)(uid=${username}))
 *  accountFullName = cn
 *  accountEmailAddress = mail
 * 
 *  groupScope = subtree
 *  groupBase = ou=Groups,dc=company,dc=com
 *  groupSearchPattern = (cn=${groupname})
 * 
 *  credentialsSource
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class LdapConfigurationBuilder {

   private final LdapConfigurationImpl config = new LdapConfigurationImpl();

   private LdapConfigurationBuilder() {
      //Builder class
   }

   public static LdapConfigurationBuilder newBuilder() {
      return new LdapConfigurationBuilder();
   }

   public LdapConfigurationBuilder properties(Map<String, Object> props) {
      config.loadProperties(props);
      return this;
   }

   public LdapConfiguration build() {
      return config.clone();
   }

   public LdapConfigurationBuilder userNameVariableName(String userNameVariableName) {
      config.setUserNameVariableName(userNameVariableName);
      return this;
   }

   public LdapConfigurationBuilder accountBase(String accountBase) {
      config.setAccountBase(accountBase);
      return this;
   }

   public LdapConfigurationBuilder accountEmailAddress(String accountEmail) {
      config.setAccountEmailAddress(accountEmail);
      return this;
   }

   public LdapConfigurationBuilder accountDisplayName(String accountDisplayName) {
      config.setAccountDisplayName(accountDisplayName);
      return this;
   }

   public LdapConfigurationBuilder accountPattern(String accountPattern) {
      config.setAccountPattern(accountPattern);
      return this;
   }

   public LdapConfigurationBuilder accountScope(LdapSearchScope accountScope) {
      config.setAccountScope(accountScope);
      return this;
   }

   public LdapConfigurationBuilder accountUserName(String accountUserName) {
      config.setAccountUserName(accountUserName);
      return this;
   }

   public LdapConfigurationBuilder authenticationType(LdapAuthenticationType authType) {
      config.setAuthenticationType(authType);
      return this;
   }

   public LdapConfigurationBuilder groupBase(String groupBase) {
      config.setGroupBase(groupBase);
      return this;
   }

   public LdapConfigurationBuilder groupPattern(String groupPattern) {
      config.setGroupPattern(groupPattern);
      return this;
   }

   public LdapConfigurationBuilder groupScope(LdapSearchScope scope) {
      config.setGroupScope(scope);
      return this;
   }

   public LdapConfigurationBuilder password(String password) {
      config.setPassword(password);
      return this;
   }

   public LdapConfigurationBuilder readTimeoutInMillis(long readTimeout) {
      config.setReadTimeoutInMillis(readTimeout);
      return this;
   }

   public LdapConfigurationBuilder referralHandlingType(LdapReferralHandlingType referralHandlingType) {
      config.setReferralHandlingType(referralHandlingType);
      return this;
   }

   public LdapConfigurationBuilder serverAddress(String serverAddress) {
      config.setServerAddress(serverAddress);
      return this;
   }

   public LdapConfigurationBuilder sslVerify(boolean sslVerify) {
      config.setSslVerifyEnabled(sslVerify);
      return this;
   }

   public LdapConfigurationBuilder userName(String userName) {
      config.setUserName(userName);
      return this;
   }

   public LdapConfigurationBuilder userNameToLowerCase(boolean usernameToLowercase) {
      config.setUserNameToLowerCase(usernameToLowercase);
      return this;
   }

   public LdapConfigurationBuilder groupName(String groupNamePattern) {
      config.setGroupName(groupNamePattern);
      return this;
   }

   public LdapConfigurationBuilder groupByGroupMemberPattern(String groupMemberPattern) {
      config.setGroupByGroupMemberPattern(groupMemberPattern);
      return this;
   }

   public LdapConfigurationBuilder credentialSource(LdapCredentialsSource source) {
      config.setCredentialsSource(source);
      return this;
   }

   public LdapConfigurationBuilder groupMembersOf(String memberOfGroups) {
      config.setGroupMembersOf(memberOfGroups);
      return this;
   }

   public LdapConfigurationBuilder groupNamespace(String groupNamespace) {
      config.setGroupNamespace(groupNamespace);
      return this;
   }

   private static final class LdapConfigurationImpl implements LdapConfiguration, Cloneable {

      private String serverAddress;
      private String userName;
      private String password;

      private LdapCredentialsSource credentialsSource;
      private LdapReferralHandlingType referralHandlingType;
      private long readTimeout;
      private boolean usernameToLowercase;
      private LdapAuthenticationType authType;
      private boolean sslVerify;

      private String userNameVariableName;
      private String accountBase;
      private LdapSearchScope accountScope;
      private String accountSearchPattern;
      private String accountUserName;
      private String accountDisplayName;
      private String accountEmail;

      private String groupBase;
      private LdapSearchScope groupScope;
      private String groupSearchPattern;
      private String groupNamePattern;
      private String groupByGroupMemberPattern;
      private String membersOfGroups;
      private String groupNamespace;

      @Override
      public LdapCredentialsSource getCredentialsSource() {
         return credentialsSource;
      }

      public void setCredentialsSource(LdapCredentialsSource credentialsSource) {
         this.credentialsSource = credentialsSource;
      }

      @Override
      public String getAccountBase() {
         return accountBase;
      }

      @Override
      public String getAccountEmailAddress() {
         return accountEmail;
      }

      @Override
      public String getAccountDisplayName() {
         return accountDisplayName;
      }

      @Override
      public String getAccountPattern() {
         return accountSearchPattern;
      }

      @Override
      public LdapSearchScope getAccountSearchScope() {
         return accountScope;
      }

      @Override
      public String getUserNameVariableName() {
         return userNameVariableName;
      }

      @Override
      public String getGroupMembersOf() {
         return membersOfGroups;
      }

      @Override
      public LdapAuthenticationType getAuthenticationType() {
         return authType;
      }

      @Override
      public String getGroupBase() {
         return groupBase;
      }

      @Override
      public String getGroupPattern() {
         return groupSearchPattern;
      }

      @Override
      public LdapSearchScope getGroupSearchScope() {
         return groupScope;
      }

      @Override
      public String getPassword() {
         return password;
      }

      @Override
      public long getReadTimeoutInMillis() {
         return readTimeout;
      }

      @Override
      public LdapReferralHandlingType getReferralHandlingType() {
         return referralHandlingType;
      }

      @Override
      public String getServerAddress() {
         return serverAddress;
      }

      @Override
      public String getUsername() {
         return userName;
      }

      @Override
      public boolean isSslVerifyEnabled() {
         return sslVerify;
      }

      @Override
      public boolean isUserNameToLowerCaseEnabled() {
         return usernameToLowercase;
      }

      public void setAccountBase(String accountBase) {
         this.accountBase = accountBase;
      }

      public void setAccountEmailAddress(String accountEmail) {
         this.accountEmail = accountEmail;
      }

      public void setAccountDisplayName(String accountDisplayName) {
         this.accountDisplayName = accountDisplayName;
      }

      public void setAccountPattern(String accountPattern) {
         this.accountSearchPattern = accountPattern;
      }

      public void setAccountScope(LdapSearchScope accountScope) {
         this.accountScope = accountScope;
      }

      public void setUserNameVariableName(String userNameVariableName) {
         this.userNameVariableName = userNameVariableName;
      }

      public void setGroupMembersOf(String membersOfGroups) {
         this.membersOfGroups = membersOfGroups;
      }

      @Override
      public String getAccountUserName() {
         return accountUserName;
      }

      public void setAccountUserName(String accountUserName) {
         this.accountUserName = accountUserName;
      }

      public void setAuthenticationType(LdapAuthenticationType authType) {
         this.authType = authType;
      }

      public void setGroupBase(String groupBase) {
         this.groupBase = groupBase;
      }

      public void setGroupPattern(String groupPattern) {
         this.groupSearchPattern = groupPattern;
      }

      public void setGroupScope(LdapSearchScope scope) {
         this.groupScope = scope;
      }

      public void setPassword(String password) {
         this.password = password;
      }

      public void setReadTimeoutInMillis(long readTimeout) {
         this.readTimeout = readTimeout;
      }

      public void setReferralHandlingType(LdapReferralHandlingType referralHandlingType) {
         this.referralHandlingType = referralHandlingType;
      }

      public void setServerAddress(String serverAddress) {
         this.serverAddress = serverAddress;
      }

      public void setSslVerifyEnabled(boolean sslVerify) {
         this.sslVerify = sslVerify;
      }

      public void setUserName(String userName) {
         this.userName = userName;
      }

      public void setUserNameToLowerCase(boolean usernameToLowercase) {
         this.usernameToLowercase = usernameToLowercase;
      }

      @Override
      public String getGroupName() {
         return groupNamePattern;
      }

      public void setGroupName(String groupNamePattern) {
         this.groupNamePattern = groupNamePattern;
      }

      @Override
      public String getGroupByGroupMemberPattern() {
         return groupByGroupMemberPattern;
      }

      public void setGroupByGroupMemberPattern(String groupByGroupMemberPattern) {
         this.groupByGroupMemberPattern = groupByGroupMemberPattern;
      }

      @Override
      public String getGroupNamespace() {
         return groupNamespace;
      }

      public void setGroupNamespace(String groupNamespace) {
         this.groupNamespace = groupNamespace;
      }

      @Override
      public synchronized LdapConfiguration clone() {
         LdapConfigurationImpl cloned = new LdapConfigurationImpl();
         cloned.serverAddress = this.serverAddress;
         cloned.userName = this.userName;
         cloned.password = this.password;
         cloned.authType = this.authType;
         cloned.credentialsSource = this.credentialsSource;
         cloned.referralHandlingType = this.referralHandlingType;
         cloned.readTimeout = this.readTimeout;
         cloned.usernameToLowercase = this.usernameToLowercase;
         cloned.sslVerify = this.sslVerify;
         cloned.accountScope = this.accountScope;
         cloned.accountBase = this.accountBase;
         cloned.accountSearchPattern = this.accountSearchPattern;
         cloned.accountUserName = this.accountUserName;
         cloned.accountDisplayName = this.accountDisplayName;
         cloned.accountEmail = this.accountEmail;
         cloned.userNameVariableName = this.userNameVariableName;
         cloned.membersOfGroups = this.membersOfGroups;
         cloned.groupScope = this.groupScope;
         cloned.groupBase = this.groupBase;
         cloned.groupSearchPattern = this.groupSearchPattern;
         cloned.groupNamePattern = this.groupNamePattern;
         cloned.groupByGroupMemberPattern = this.groupByGroupMemberPattern;
         cloned.groupNamespace = this.groupNamespace;
         return cloned;
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null && !props.isEmpty()) {
            setServerAddress(get(props, LDAP_SERVER_ADDRESS, LdapAuthenticationType.NONE));

            setAuthenticationType(getAuthType(props, LDAP_AUTHENTICATION_TYPE, DEFAULT_AUTHENTICATION_TYPE));
            setCredentialsSource(getSrc(props, LDAP_CREDENTIALS_SOURCE, DEFAULT_LDAP_CREDENTIALS_SOURCE));
            setUserName(get(props, LDAP_USER_NAME, DEFAULT_USERNAME));
            setPassword(get(props, LDAP_PASSWORD, DEFAULT_PASSWORD));

            setReadTimeoutInMillis(getLong(props, LDAP_READ_TIMEOUT, DEFAULT_LDAP_READ_TIMEOUT_IN_MILLIS));
            setReferralHandlingType(getRefType(props, LDAP_REFERRAL_HANDLING, DEFAULT_LDAP_REFERRAL_HANDLING));
            setSslVerifyEnabled(getBoolean(props, LDAP_SSL_VERIFY, DEFAULT_SSL_CERTIFICATE_VERIFICATION));
            setUserNameToLowerCase(getBoolean(props, LDAP_USERNAME_TO_LOWERCASE, DEFAULT_USERNAME_TO_LOWERCASE));

            setUserNameVariableName(get(props, USERNAME_VARIABLE_NAME, PATTERN_DEFAULT));

            setAccountBase(get(props, ACCOUNT_BASE, PATTERN_DEFAULT));
            setAccountDisplayName(get(props, ACCOUNT_DISPLAY_NAME, PATTERN_DEFAULT));
            setAccountEmailAddress(get(props, ACCOUNT_EMAIL_ADDRESS, PATTERN_DEFAULT));
            setAccountPattern(get(props, ACCOUNT_PATTERN, PATTERN_DEFAULT));
            setAccountScope(getScope(props, ACCOUNT_SCOPE, DEFAULT_SEARCH_SCOPE));
            setAccountUserName(get(props, ACCOUNT_USERNAME_PATTERN, PATTERN_DEFAULT));

            setGroupBase(get(props, GROUP_BASE, PATTERN_DEFAULT));
            setGroupPattern(get(props, GROUP_PATTERN, PATTERN_DEFAULT));
            setGroupScope(getScope(props, GROUP_SCOPE, DEFAULT_SEARCH_SCOPE));
            setGroupName(get(props, GROUP_NAME_PATTERN, PATTERN_DEFAULT));
            setGroupByGroupMemberPattern(get(props, GROUP_BY_GROUP_MEMBER_PATTERN, PATTERN_DEFAULT));
            setGroupMembersOf(get(props, GROUP_MEMBER_OF, PATTERN_DEFAULT));
            setGroupNamespace(get(props, GROUP_NAMESPACE, DEFAULT_GROUP_NAMESPACE));
         }
      }

      private Long getLong(Map<String, Object> props, String key, Long defaultValue) {
         String toReturn = get(props, key, String.valueOf(defaultValue));
         return Strings.isNumeric(toReturn) ? Long.parseLong(toReturn) : -1L;
      }

      private LdapSearchScope getScope(Map<String, Object> props, String key, LdapSearchScope defaultValue) {
         String toReturn = get(props, key, defaultValue);
         return LdapSearchScope.parse(toReturn);
      }

      private LdapReferralHandlingType getRefType(Map<String, Object> props, String key, LdapReferralHandlingType defaultValue) {
         String toReturn = get(props, key, defaultValue);
         return LdapReferralHandlingType.parse(toReturn);
      }

      private LdapCredentialsSource getSrc(Map<String, Object> props, String key, LdapCredentialsSource defaultValue) {
         String toReturn = get(props, key, defaultValue);
         return LdapCredentialsSource.parse(toReturn);
      }

      private LdapAuthenticationType getAuthType(Map<String, Object> props, String key, LdapAuthenticationType defaultValue) {
         String toReturn = get(props, key, defaultValue);
         return LdapAuthenticationType.parse(toReturn);
      }

      private boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
         String toReturn = get(props, key, String.valueOf(defaultValue));
         return Boolean.parseBoolean(toReturn);
      }

      private String get(Map<String, Object> props, String key, Enum<?> defaultValue) {
         return get(props, key, defaultValue != null ? defaultValue.name() : null);
      }

      private String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props.get(key);
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }

   }
}
