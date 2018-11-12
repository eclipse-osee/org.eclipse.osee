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
package org.eclipse.osee.account.rest.internal;

import static org.eclipse.osee.account.rest.internal.JaxRsAuthenticatorConstants.DEFAULT_JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION;
import static org.eclipse.osee.account.rest.internal.JaxRsAuthenticatorConstants.JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.admin.AuthenticationRequestBuilder;
import org.eclipse.osee.authorization.admin.Authorization;
import org.eclipse.osee.authorization.admin.AuthorizationAdmin;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.authorization.admin.AuthorizationRequestBuilder;
import org.eclipse.osee.authorization.admin.AuthorizationUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsAuthenticatorImpl implements JaxRsAuthenticator {

   private AuthenticationAdmin authenticationAdmin;
   private AuthorizationAdmin authorizationAdmin;
   private AccountAdmin accountAdmin;
   private volatile boolean automaticAccountCreationAllowed = DEFAULT_JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION;

   public void setAuthenticationAdmin(AuthenticationAdmin authenticationAdmin) {
      this.authenticationAdmin = authenticationAdmin;
   }

   public void setAccountAdmin(AccountAdmin accountAdmin) {
      this.accountAdmin = accountAdmin;
   }

   public void setAuthorizationAdmin(AuthorizationAdmin authorizationAdmin) {
      this.authorizationAdmin = authorizationAdmin;
   }

   public void start(Map<String, Object> props) {
      update(props);
   }

   public void stop() {
      //
   }

   public void update(Map<String, Object> props) {
      automaticAccountCreationAllowed = getBoolean(props, JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION,
         DEFAULT_JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION);
   }

   @Override
   public OseePrincipal authenticate(String scheme, String username, String password) {
      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder() //
         .userName(username)//
         .password(password)//
         .build();

      AuthenticatedUser subject = authenticationAdmin.authenticate(request);
      Account account = resolveAccount(username, subject, automaticAccountCreationAllowed);

      Set<String> roles = new LinkedHashSet<>();
      if (subject.isAuthenticated()) {
         roles.add(SystemRoles.ROLES_AUTHENTICATED);
      } else {
         roles.add(SystemRoles.ROLES_ANONYMOUS);
      }
      for (String role : subject.getRoles()) {
         roles.add(role);
      }
      // Get additional roles/permissions from authorization service;
      AuthorizationRequest authorizationRequest = AuthorizationRequestBuilder.newBuilder()//
         .secure(true) //
         .identifier(account.getId())//
         .build();

      Authorization authorize = authorizationAdmin.authorize(authorizationRequest);
      AuthorizationUser authUser = (AuthorizationUser) authorize.getPrincipal();

      for (String role : authUser.getRoles()) {
         roles.add(role);
      }

      // Preferences or other user specific properties
      return OseePrincipal.Anonymous;
   }

   private Account resolveAccount(String login, AuthenticatedUser subject, boolean accountCreationAllowed) {
      ResultSet<Account> result = accountAdmin.getAccountByEmail(subject.getEmailAddress());
      Account account = result.getOneOrDefault(Account.SENTINEL);
      if (account.getId() == Account.SENTINEL.getId()) {
         if (subject.isAuthenticated() && accountCreationAllowed) {
            CreateAccountRequest request =
               CreateAccountRequestBuilder.newBuilder().active(subject.isActive()).displayName(
                  subject.getDisplayName()).email(subject.getEmailAddress()).userName(subject.getUserName()).build();
            ArtifactId accountId = accountAdmin.createAccount(request);
            account = accountAdmin.getAccountById(accountId).getExactlyOne();
         } else {
            // or log in as anonymous ?
            throw new OseeCoreException("Account not found for [%s]", login);
         }
      }
      return account;
   }

   private static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      boolean toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         if (object instanceof String) {
            toReturn = Boolean.parseBoolean((String) object);
         } else if (object instanceof Boolean) {
            toReturn = (Boolean) object;
         }
      }
      return toReturn;
   }

}