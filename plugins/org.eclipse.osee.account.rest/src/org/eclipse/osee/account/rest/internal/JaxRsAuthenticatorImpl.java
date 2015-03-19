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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.admin.AuthenticationRequestBuilder;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsAuthenticatorImpl implements JaxRsAuthenticator {

   private AuthenticationAdmin authenticationAdmin;
   private AccountAdmin accountAdmin;

   private volatile boolean automaticAccountCreationAllowed = DEFAULT_JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION;

   public void setAuthenticationAdmin(AuthenticationAdmin authenticationAdmin) {
      this.authenticationAdmin = authenticationAdmin;
   }

   public void setAccountAdmin(AccountAdmin accountAdmin) {
      this.accountAdmin = accountAdmin;
   }

   public void start(Map<String, Object> props) {
      update(props);
   }

   public void stop() {
      //
   }

   public void update(Map<String, Object> props) {
      automaticAccountCreationAllowed =
         getBoolean(props, JAXRS_AUTH__ALLOW_AUTOMATIC_ACCOUNT_CREATION,
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

      Set<String> roles = new LinkedHashSet<String>();
      if (subject.isAuthenticated()) {
         roles.add(SystemRoles.ROLES_AUTHENTICATED);
      } else {
         roles.add(SystemRoles.ROLES_ANONYMOUS);
      }
      for (String role : subject.getRoles()) {
         roles.add(role);
      }
      // Get additional roles/permissions from authorization service;

      // Preferences or other user specific properties
      Map<String, String> properties = Collections.emptyMap();
      return new OseePrincipalImpl(username, account, subject.isAuthenticated(), roles, properties);
   }

   private Account resolveAccount(String login, AuthenticatedUser subject, boolean accountCreationAllowed) {
      ResultSet<Account> result = accountAdmin.getAccountByUserName(subject.getUserName());
      Account account = result.getOneOrNull();
      if (account == null) {
         if (subject.isAuthenticated() && accountCreationAllowed) {
            CreateAccountRequest request = CreateAccountRequestBuilder.newBuilder()//
            .active(subject.isActive())//
            .displayName(subject.getDisplayName())//
            .email(subject.getEmailAddress())//
            .userName(subject.getUserName())//
            .build();
            Identifiable<String> id = accountAdmin.createAccount(request);
            account = accountAdmin.getAccountById(id).getExactlyOne();
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

   private static final class OseePrincipalImpl extends BaseIdentity<Long> implements OseePrincipal {
      private final String login;
      private final Account data;
      private final boolean authenticated;
      private final Set<String> roles;
      private final Map<String, String> properties;

      public OseePrincipalImpl(String login, Account data, boolean authenticated, Set<String> roles, Map<String, String> properties) {
         super(data.getId());
         this.login = login;
         this.data = data;
         this.authenticated = authenticated;
         this.roles = roles;
         this.properties = properties;
      }

      @Override
      public String getDisplayName() {
         return data.getName();
      }

      @Override
      public String getUserName() {
         return data.getUserName();
      }

      @Override
      public String getEmailAddress() {
         return data.getEmail();
      }

      @Override
      public boolean isActive() {
         return data.isActive();
      }

      @Override
      public Set<String> getRoles() {
         return roles;
      }

      @Override
      public boolean isAuthenticated() {
         return authenticated;
      }

      @Override
      public String getName() {
         return getDisplayName();
      }

      @Override
      public String getLogin() {
         return login;
      }

      @Override
      public Map<String, String> getProperties() {
         return properties;
      }
   }

}