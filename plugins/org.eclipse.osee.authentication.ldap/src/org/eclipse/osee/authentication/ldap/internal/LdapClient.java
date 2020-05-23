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

import static org.eclipse.osee.authentication.ldap.internal.util.LdapUtil.getValue;
import java.security.PrivilegedActionException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.login.LoginException;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.LdapReferralHandlingType;
import org.eclipse.osee.authentication.ldap.internal.util.IgnoreCertValidationSSLSocketFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class LdapClient implements ContextConfigProvider {

   public interface LdapConnectionFactory {
      LdapConnection createConnection(ContextConfigProvider provider, LdapAuthenticationType authType, Hashtable<String, String> properties) throws NamingException, LoginException, PrivilegedActionException;
   }

   private static final String LDAP_SSL_SCHEME = "ldaps:";
   private static final String LDAP_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
   private static final String CONTEXT__LDAP_FACTORY_SOCKET = "java.naming.ldap.factory.socket";
   private static final String CONTEXT__LDAP_READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";

   private final LdapConnectionFactory connectionFactory;
   private String serverAddress;
   private long readTimeoutInMillis;
   private boolean isSslVerifyEnabled;
   private LdapReferralHandlingType referral;

   public LdapClient(LdapConnectionFactory connectionFactory) {
      super();
      this.connectionFactory = connectionFactory;
   }

   @Override
   public String getServerAddress() {
      return serverAddress;
   }

   public void setServerAddress(String serverAddress) {
      this.serverAddress = serverAddress;
   }

   public long getReadTimeoutInMillis() {
      return readTimeoutInMillis;
   }

   public void setReadTimeoutInMillis(long readTimeoutInMillis) {
      this.readTimeoutInMillis = readTimeoutInMillis;
   }

   public boolean isSslVerifyEnabled() {
      return isSslVerifyEnabled;
   }

   public void setSslVerifyEnabled(boolean isSslVerifyEnabled) {
      this.isSslVerifyEnabled = isSslVerifyEnabled;
   }

   public LdapReferralHandlingType getReferralHandling() {
      return referral;
   }

   public void setReferralHandling(LdapReferralHandlingType referral) {
      this.referral = referral;
   }

   public boolean isSslScheme() {
      return Strings.isValid(serverAddress) && serverAddress.startsWith(LDAP_SSL_SCHEME);
   }

   @Override
   public Hashtable<String, String> getContextConfig() {
      Hashtable<String, String> props = new Hashtable<>();
      props.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_INITIAL_CONTEXT_FACTORY);
      props.put(Context.PROVIDER_URL, getServerAddress());
      props.put(Context.REFERRAL, getReferralHandling().getContextReferralName());

      long timeout = getReadTimeoutInMillis();
      if (timeout >= 0) {
         props.put(CONTEXT__LDAP_READ_TIMEOUT, Long.toString(timeout));
      }
      if (isSslScheme() && !isSslVerifyEnabled()) {
         Class<? extends SSLSocketFactory> factory = IgnoreCertValidationSSLSocketFactory.class;
         props.put(CONTEXT__LDAP_FACTORY_SOCKET, factory.getName());
      }
      return props;
   }

   public LdapConnection getConnection(LdapAuthenticationType authType, String username, String password) throws NamingException, LoginException, PrivilegedActionException {
      Hashtable<String, String> env = getContextConfig();
      env.put(Context.SECURITY_AUTHENTICATION, authType.getContextAuthenticationName());
      if (LdapAuthenticationType.GSSAPI != authType) {
         env.put(Context.SECURITY_PRINCIPAL, getValue(username, ""));
         env.put(Context.SECURITY_CREDENTIALS, getValue(password, ""));
      }
      return connectionFactory.createConnection(this, authType, env);
   }

}
