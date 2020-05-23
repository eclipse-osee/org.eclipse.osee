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

package org.eclipse.osee.authentication.ldap.internal.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class IgnoreCertValidationSSLSocketFactory extends SSLSocketFactory {

   private static final String SSL_CONTEXT = "SSL";
   private static final IgnoreCertValidationSSLSocketFactory INSTANCE = createInstance();

   private final SSLSocketFactory sslFactory;

   private IgnoreCertValidationSSLSocketFactory(final SSLSocketFactory sslFactory) {
      this.sslFactory = sslFactory;
   }

   public static SocketFactory getInstance() {
      return INSTANCE;
   }

   @Override
   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
      return sslFactory.createSocket(socket, host, port, autoClose);
   }

   @Override
   public String[] getDefaultCipherSuites() {
      return sslFactory.getDefaultCipherSuites();
   }

   @Override
   public String[] getSupportedCipherSuites() {
      return sslFactory.getSupportedCipherSuites();
   }

   @Override
   public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
      return sslFactory.createSocket(host, port);
   }

   @Override
   public Socket createSocket(InetAddress host, int port) throws IOException {
      return sslFactory.createSocket(host, port);
   }

   @Override
   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
      return sslFactory.createSocket(host, port, localHost, localPort);
   }

   @Override
   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      return sslFactory.createSocket(address, port, localAddress, localPort);
   }

   private static IgnoreCertValidationSSLSocketFactory createInstance() {
      try {
         SSLContext context = SSLContext.getInstance(SSL_CONTEXT);
         KeyManager[] keyManager = null; // no key manager needed
         TrustManager[] trustManagers = {newNoopTrustManager()};
         SecureRandom secureRandom = new SecureRandom();
         context.init(keyManager, trustManagers, secureRandom);
         return new IgnoreCertValidationSSLSocketFactory(context.getSocketFactory());
      } catch (GeneralSecurityException ex) {
         throw new OseeCoreException(ex, "Error creating IgnoreCertsSSLSocketFactory");
      }
   }

   private static TrustManager newNoopTrustManager() {
      return new X509TrustManager() {
         @Override
         public X509Certificate[] getAcceptedIssuers() {
            // Do nothing
            return null;
         }

         @Override
         public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Do nothing
         }

         @Override
         public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Do nothing
         }
      };
   }
}