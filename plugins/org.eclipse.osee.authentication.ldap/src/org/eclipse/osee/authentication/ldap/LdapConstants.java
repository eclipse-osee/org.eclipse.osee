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

/**
 * @author Roberto E. Escobar
 */
public final class LdapConstants {

   private LdapConstants() {
      // Utility class
   }

   public static final String NAMESPACE = "ldap";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final LdapCredentialsSource DEFAULT_LDAP_CREDENTIALS_SOURCE = LdapCredentialsSource.SYSTEM_CREDENTIALS;
   public static final LdapAuthenticationType DEFAULT_AUTHENTICATION_TYPE = LdapAuthenticationType.SIMPLE;
   public static final LdapReferralHandlingType DEFAULT_LDAP_REFERRAL_HANDLING = LdapReferralHandlingType.IGNORE;
   public static final long DEFAULT_LDAP_READ_TIMEOUT_IN_MILLIS = -1L;
   public static final boolean DEFAULT_SSL_CERTIFICATE_VERIFICATION = true;
   public static final boolean DEFAULT_USERNAME_TO_LOWERCASE = false;
   public static final String DEFAULT_USERNAME = null;
   public static final String DEFAULT_PASSWORD = null;
   public static final String PATTERN_DEFAULT = null;
   public static final LdapSearchScope DEFAULT_SEARCH_SCOPE = LdapSearchScope.SUBTREE_SCOPE;
   public static final String DEFAULT_GROUP_NAMESPACE = NAMESPACE;

   public static final String LDAP_SERVER_ADDRESS = qualify("server.address");
   public static final String LDAP_CREDENTIALS_SOURCE = qualify("credential.source");
   public static final String LDAP_USER_NAME = qualify("username");
   public static final String LDAP_PASSWORD = qualify("password");

   public static final String LDAP_AUTHENTICATION_TYPE = qualify("authentication.type");
   public static final String LDAP_SSL_VERIFY = qualify("ssl.verify.enabled");

   public static final String LDAP_REFERRAL_HANDLING = qualify("referral.handling");
   public static final String LDAP_READ_TIMEOUT = qualify("read.timeout");
   public static final String LDAP_USERNAME_TO_LOWERCASE = qualify("username.to.lowercase.enabled");

   public static final String USERNAME_VARIABLE_NAME = qualify("account.username.bind.variable");

   public static final String ACCOUNT_BASE = qualify("account.search.base");
   public static final String ACCOUNT_DISPLAY_NAME = qualify("account.display.name");
   public static final String ACCOUNT_EMAIL_ADDRESS = qualify("account.email.address");
   public static final String ACCOUNT_PATTERN = qualify("account.search.pattern");
   public static final String ACCOUNT_SCOPE = qualify("account.search.scope");
   public static final String ACCOUNT_USERNAME_PATTERN = qualify("account.username.pattern");

   public static final String GROUP_BASE = qualify("group.search.base");
   public static final String GROUP_PATTERN = qualify("group.search.pattern");
   public static final String GROUP_SCOPE = qualify("group.search.scope");
   public static final String GROUP_NAME_PATTERN = qualify("group.name.pattern");
   public static final String GROUP_BY_GROUP_MEMBER_PATTERN = qualify("group.by.group.member.search.pattern");
   public static final String GROUP_MEMBER_OF = qualify("group.member.of.groups");
   public static final String GROUP_NAMESPACE = qualify("group.namespace");
}
