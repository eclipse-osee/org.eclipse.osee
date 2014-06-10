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

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientConstants {

   private JaxRsClientConstants() {
      // Utility class
   }

   public static final String NAMESPACE = "jaxrs.client";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final boolean DEFAULT_JAXRS_CLIENT_FOLLOW_REDIRECTS = true;
   public static final int DEFAULT_JAXRS_CLIENT_THREADPOOL_SIZE = -1; // cached thread pool - no limit
   public static final int DEFAULT_JAXRS_CLIENT_CONNECT_TIMEOUT = 30 * 1000; // 30 seconds
   public static final int DEFAULT_JAXRS_CLIENT_READ_TIMEOUT = -1; // infinity

   public static final String JAXRS_CLIENT_SERVER_ADDRESS = qualify("server.address");
   public static final String JAXRS_CLIENT_PROXY_SERVER_ADDRESS = qualify("proxy.server.address");
   public static final String JAXRS_CLIENT_FOLLOW_REDIRECTS = qualify("follow.redirects");
   public static final String JAXRS_CLIENT_CONNECT_TIMEOUT = qualify("connect.timeout");
   public static final String JAXRS_CLIENT_READ_TIMEOUT = qualify("read.timeout");
   public static final String JAXRS_CLIENT_THREADPOOL_SIZE = qualify("threadpool.size");

}
