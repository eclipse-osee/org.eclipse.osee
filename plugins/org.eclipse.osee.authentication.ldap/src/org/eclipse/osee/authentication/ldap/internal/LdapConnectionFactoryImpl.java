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

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.internal.LdapClient.LdapConnectionFactory;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class LdapConnectionFactoryImpl implements LdapConnectionFactory {

   private static final String KERBEROS_LOGIN = "KerberosLogin";
   private final Log logger;

   public LdapConnectionFactoryImpl(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public LdapConnection createConnection(ContextConfigProvider provider, LdapAuthenticationType authType, Hashtable<String, String> properties) throws NamingException, LoginException, PrivilegedActionException {
      DirContext context;
      if (LdapAuthenticationType.GSSAPI == authType) {
         context = doKerberosLogin(properties);
      } else {
         context = new InitialDirContext(properties);
      }
      return new LdapConnection(logger, provider, context);
   }

   private DirContext doKerberosLogin(final Hashtable<String, String> env) throws LoginException, PrivilegedActionException {
      LoginContext context = new LoginContext(KERBEROS_LOGIN);
      context.login();
      Subject subject = context.getSubject();
      try {
         return Subject.doAs(subject, new PrivilegedExceptionAction<DirContext>() {
            @Override
            public DirContext run() throws NamingException {
               return new InitialDirContext(env);
            }
         });
      } finally {
         context.logout();
      }
   }

}
