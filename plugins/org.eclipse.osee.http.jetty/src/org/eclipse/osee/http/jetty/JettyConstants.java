/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty;

/**
 * @author Roberto E. Escobar
 */
public final class JettyConstants {

   private JettyConstants() {
      // Constants class
   }

   public static final String SERVLET_ATTRIBUTE_KEY__INTERNAL_CONTEXT_CLASSLOADER =
      "org.eclipse.osee.http.jetty.internal.ContextClassLoader";
   public static final String SERVLET_ATTRIBUTE_KEY__CONTEXT_TEMPDIR = "javax.servlet.context.tempdir";

   public static final String SERVICE_VENDOR = "org.eclipse.osee.http.jetty";
   public static final String SERVICE_DESCRIPTION = "org.eclipse.osee Http Service";

   public static final String NAMESPACE = "jetty.server";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String JETTY__ACCEPT_LOCAL_CONNECTIONS = "127.0.0.1";
   public static final String JETTY__ACCEPT_REMOTE_CONNECTIONS = "0.0.0.0";

   public static final String JETTY__HTTP_NIO_ENABLED = qualify("http.nio.enabled");
   public static final String JETTY__HTTP_NIO_AUTO_DETECT = qualify("http.nio.enabled");

   public static final String JETTY__HTTP_ENABLED = qualify("http.enabled");
   public static final String JETTY__HTTP_PORT = qualify("http.port");
   public static final String JETTY__HTTP_HOST = qualify("http.host");
   public static final String JETTY__HTTP_USE_RANDOM_PORT = qualify("http.use.random.port");

   public static final String JETTY__HTTPS_ENABLED = qualify("https.enabled");
   public static final String JETTY__HTTPS_HOST = qualify("https.host");
   public static final String JETTY__HTTPS_PORT = qualify("https.port");
   public static final String JETTY__HTTPS_USE_RANDOM_PORT = qualify("https.use.random.port");

   public static final String JETTY__SSL_PROTOCOL = qualify("ssl.protocol");
   public static final String JETTY__SSL_KEYSTORE = qualify("ssl.keystore");
   public static final String JETTY__SSL_KEYSTORETYPE = qualify("ssl.keystoretype");
   public static final String JETTY__SSL_PASSWORD = qualify("ssl.password");
   public static final String JETTY__SSL_KEYPASSWORD = qualify("ssl.keypassword");
   public static final String JETTY__SSL_NEEDS_CLIENT_AUTH = qualify("ssl.needclientauth");
   public static final String JETTY__SSL_WANTS_CLIENT_AUTH = qualify("ssl.wantclientauth");

   public static final String JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL = qualify("context.session.inactive.interval");
   public static final String JETTY__CONTEXT_PATH = qualify("context.path");
   public static final String JETTY__OTHER_INFO = qualify("other.info");
   public static final String JETTY__WORKING_DIRECTORY = qualify("working.directory");

   public static final String JETTY__MULTIPLE_SLASH_TO_SINGLE = qualify("replace.multiple.slash.to.single");
   //////////////////////////////////// Defaults

   public static final boolean DEFAULT_JETTY__HTTP_NIO_ENABLED = false;
   public static final boolean DEFAULT_JETTY__HTTP_NIO_AUTO_DETECT = true;

   public static final boolean DEFAULT_JETTY__HTTP_ENABLED = true;
   public static final int DEFAULT_JETTY__HTTP_PORT = 80;
   public static final String DEFAULT_JETTY__HTTP_HOST = JETTY__ACCEPT_REMOTE_CONNECTIONS;
   public static final boolean DEFAULT_JETTY__HTTP_USE_RANDOM_PORT = false;

   public static final boolean DEFAULT_JETTY__HTTPS_ENABLED = false;
   public static final String DEFAULT_JETTY__HTTPS_HOST = JETTY__ACCEPT_REMOTE_CONNECTIONS;
   public static final int DEFAULT_JETTY__HTTPS_PORT = 443;
   public static final boolean DEFAULT_JETTY__HTTPS_USE_RANDOM_PORT = false;

   public static final String DEFAULT_JETTY__SSL_PROTOCOL = null;
   public static final String DEFAULT_JETTY__SSL_KEYSTORE = null;
   public static final String DEFAULT_JETTY__SSL_KEYSTORETYPE = null;
   public static final String DEFAULT_JETTY__SSL_PASSWORD = null;
   public static final String DEFAULT_JETTY__SSL_KEYPASSWORD = null;
   public static final boolean DEFAULT_JETTY__SSL_NEEDS_CLIENT_AUTH = false;
   public static final boolean DEFAULT_JETTY__SSL_WANTS_CLIENT_AUTH = false;

   public static final int DEFAULT_JETTY__CONTEXT_SESSION_INACTIVE_INTERVAL = 3600;
   public static final String DEFAULT_JETTY__CONTEXT_PATH = "/";
   public static final String DEFAULT_JETTY__OTHER_INFO = null;
   public static final String DEFAULT_JETTY__WORKING_DIRECTORY = null;

   public static final boolean DEFAULT_JETTY__MULTIPLE_SLASH_TO_SINGLE = true;

   ///////////// JDBC Jetty Session Manager
   public static final String JETTY_JDBC_SESSION__CLUSTER_NAME = "jetty.jdbc.cluster.name";
   public static final String JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS = "jetty.jdbc.scavenge.interval.secs";
   public static final String JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS = "jetty.jdbc.save.interval.secs";

   public static final int DEFAULT_JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS = 60;
   public static final int DEFAULT_JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS = 30;

   // OSGi HTTP Service suggest these JVM properties for setting the default ports
   public static final String ORG_OSGI_SERVICE_HTTP_PORT = "org.osgi.service.http.port";
   public static final String ORG_OSGI_SERVICE_HTTP_PORT_SECURE = "org.osgi.service.http.port.secure";

}
