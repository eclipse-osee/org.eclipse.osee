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
package org.eclipse.osee.jaxrs.client;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientConstants {

   private JaxRsClientConstants() {
      // Constants
   }

   private static final String NAMESPACE = "jaxrs.client";

   static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static enum ProxyType {
      HTTP,
      SOCKS;

      public static ProxyType parse(String value) {
         ProxyType toReturn = ProxyType.HTTP;
         if (Strings.isValid(value)) {
            String toFind = value.toUpperCase().trim();
            for (ProxyType type : ProxyType.values()) {
               if (type.name().equals(toFind)) {
                  toReturn = type;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   public static enum ConnectionType {
      CLOSE,
      KEEP_ALIVE;

      public static ConnectionType parse(String value) {
         ConnectionType toReturn = ConnectionType.KEEP_ALIVE;
         if (Strings.isValid(value)) {
            String toFind = value.toUpperCase().trim();
            for (ConnectionType type : ConnectionType.values()) {
               if (type.name().equals(toFind)) {
                  toReturn = type;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   public static final String BASIC_AUTHENTICATION = "Basic";

   public static final String JAXRS_CLIENT_SERVER_USERNAME = qualify("server.username");
   public static final String JAXRS_CLIENT_SERVER_PASSWORD = qualify("server.password");
   public static final String JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE = qualify("server.authorization.type");
   public static final String JAXRS_CLIENT_FOLLOW_REDIRECTS = qualify("follow.redirects");
   public static final String JAXRS_CLIENT_CONNECTION_TIMEOUT = qualify("connection.timeout");
   public static final String JAXRS_CLIENT_RECEIVE_TIMEOUT = qualify("receive.timeout");
   public static final String JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT = qualify("async.exec.timeout");
   public static final String JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION = qualify("async.exec.timeout.rejection");
   public static final String JAXRS_CLIENT_MAX_RETRANSMITS = qualify("max.retransmits");
   public static final String JAXRS_CLIENT_CHUNKING_ALLOWED = qualify("is.chunking.allowed");
   public static final String JAXRS_CLIENT_CHUNKING_THRESHOLD = qualify("chunking.threshold");
   public static final String JAXRS_CLIENT_CHUNK_SIZE = qualify("chunk.size");
   public static final String JAXRS_CLIENT_CONNECTION_TYPE = qualify("connection.type");
   public static final String JAXRS_CLIENT_PROXY_ADDRESS = qualify("proxy.server.address");
   public static final String JAXRS_CLIENT_PROXY_TYPE = qualify("proxy.server.type");
   public static final String JAXRS_CLIENT_NON_PROXY_HOSTS = qualify("non.proxy.hosts");
   public static final String JAXRS_CLIENT_PROXY_USERNAME = qualify("proxy.username");
   public static final String JAXRS_CLIENT_PROXY_PASSWORD = qualify("proxy.password");
   public static final String JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE = qualify("proxy.authorization.type");
   public static final String JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS = qualify("create.threadsafe.proxy.clients");
   public static final String JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS =
      qualify("proxy.client.subresources.inherit.headers");

   public static final String DEFAULT_JAXRS_CLIENT_SERVER_USERNAME = null;
   public static final String DEFAULT_JAXRS_CLIENT_SERVER_PASSWORD = null;
   public static final String DEFAULT_JAXRS_CLIENT_SERVER_AUTHORIZATION_TYPE = BASIC_AUTHENTICATION;
   public static final boolean DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS = true;
   public static final long DEFAULT_JAXRS_CLIENT_CONNECTION_TIMEOUT = 30000L; // Long - 0 infinite default 30000L millis
   public static final long DEFAULT_JAXRS_CLIENT_RECEIVE_TIMEOUT = 0L; // Long - 0 infinite default 30000L millis
   public static final long DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT = 5000L;
   public static final boolean DEFAULT_JAXRS_CLIENT_ASYNC_EXECUTE_TIMEOUT_REJECTION = true;
   public static final int DEFAULT_JAXRS_CLIENT_MAX_RETRANSMITS = -1; // any negative number equals unlimited
   public static final boolean DEFAULT_JAXRS_CLIENT_CHUNKING_ALLOWED = true;
   public static final int DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD = 4096;
   public static final int DEFAULT_JAXRS_CLIENT_CHUNK_SIZE = 0; //If chunk length is less than or equal to zero, a default value will be used.
   public static final ConnectionType DEFAULT_JAXRS_CLIENT_CONNECTION_TYPE = ConnectionType.KEEP_ALIVE;
   public static final ProxyType DEFAULT_JAXRS_CLIENT_PROXY_TYPE = ProxyType.HTTP;
   public static final String DEFAULT_JAXRS_CLIENT_PROXY_ADDRESS = null;
   public static final int DEFAULT_JAXRS_CLIENT_PROXY_PORT = -1;
   public static final String DEFAULT_JAXRS_CLIENT_NON_PROXY_HOSTS = null;
   public static final String DEFAULT_JAXRS_CLIENT_PROXY_USERNAME = null;
   public static final String DEFAULT_JAXRS_CLIENT_PROXY_PASSWORD = null;
   public static final String DEFAULT_JAXRS_CLIENT_PROXY_AUTHORIZATION_TYPE = BASIC_AUTHENTICATION;
   public static final boolean DEFAULT_JAXRS_CLIENT_CREATE_THREADSAFE_PROXY_CLIENTS = false;
   public static final boolean DEFAULT_JAXRS_CLIENT_PROXY_CLIENT_SUB_RESOURCES_INHERIT_HEADERS = false;

   public static int RETRANSMIT_MIN_LIMIT = -1;
   public static int PORT_MIN_LIMIT = -1;
   public static long TIMEOUT_MIN_LIMIT = 0L;
   public static int CHUNK_LENGTH_MIN_LIMIT = 0;
   public static int CHUNK_THRESHOLD_MIN_LIMIT = DEFAULT_JAXRS_CLIENT_CHUNKING_THRESHOLD;

}
