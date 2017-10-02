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

import java.util.Map;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationProvider;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.ldap.LdapConfiguration;
import org.eclipse.osee.authentication.ldap.LdapConfigurationBuilder;
import org.eclipse.osee.authentication.ldap.internal.LdapClient.LdapConnectionFactory;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class LdapAuthenticationProvider implements AuthenticationProvider {

   private static final String LDAP_SCHEME = "LDAP";

   private Log logger;
   private LdapAuthenticationManager authenticator;
   private LdapConfiguration config;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(Map<String, Object> properties) {
      logger.trace("Starting LdapAuthenticationProvider...");

      LdapConnectionFactory factory = new LdapConnectionFactoryImpl(logger);
      LdapClient client = new LdapClient(factory);
      authenticator = new LdapAuthenticationManager(client);

      update(properties);
   }

   public void stop() {
      logger.trace("Stopping LdapAuthenticationProvider...");

      config = null;
      authenticator = null;
   }

   public void update(Map<String, Object> properties) {
      logger.trace("Configuring LdapAuthenticationProvider...");

      config = LdapConfigurationBuilder.newBuilder()//
         .properties(properties)//
         .build();

      if (authenticator != null) {
         authenticator.configure(config);
      }
   }

   @Override
   public String getAuthenticationScheme() {
      return LDAP_SCHEME;
   }

   @Override
   public AuthenticatedUser authenticate(AuthenticationRequest request) {
      return authenticator.authenticate(request);
   }

}
