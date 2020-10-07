/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.config;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;

/**
 * @author Ajay Chandrahasan
 */
public class CustomAuthentificator extends AbstractLdapAuthenticator {

   DefaultSpringSecurityContextSource contextSource1;

   /**
    * @param contextSource
    */
   public CustomAuthentificator(final DefaultSpringSecurityContextSource contextSource) {
      super(contextSource);
      this.contextSource1 = contextSource;
   }

   /**
    * {@inheritDoc}
    */
   public DirContextOperations authenticate(final Authentication authentication) {
      DirContextOperations user;

      String username = authentication.getName();
      String password = (String) authentication.getCredentials();
      BindAuthenticator bindAuthenticatorser = new BindAuthenticator(this.contextSource1);
      String dnArray[] = new String[] {"ou=sss"};
      bindAuthenticatorser.setUserDnPatterns(dnArray);
      bindAuthenticatorser.setUserSearch(getLdapSerarch());
      user = bindAuthenticatorser.authenticate(authentication);
      return user;
   }

   public LdapUserSearch getLdapSerarch() {
      LdapUserSearch ldapUserSearch =
         new FilterBasedLdapUserSearch("", "(&(objectCategory=Person)(sAMAccountName={0}))", this.contextSource1);
      ((FilterBasedLdapUserSearch) ldapUserSearch).setSearchSubtree(true);
      ((FilterBasedLdapUserSearch) ldapUserSearch).setSearchTimeLimit(0);
      ((FilterBasedLdapUserSearch) ldapUserSearch).setDerefLinkFlag(false);
      return ldapUserSearch;
   }
}
