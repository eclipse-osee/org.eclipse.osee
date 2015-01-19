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
package org.eclipse.osee.http.jetty.internal.session;

import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS;
import static org.eclipse.osee.http.jetty.JettyConstants.DEFAULT_JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY_JDBC_SESSION__CLUSTER_NAME;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.JettySessionManagerFactory;
import org.eclipse.osee.http.jetty.internal.JettyUtil;
import org.eclipse.osee.http.jetty.internal.jdbc.JdbcSessionStorageImpl;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Roberto E. Escobar
 */
public class JdbcJettySessionManagerFactory implements JettySessionManagerFactory {

   private final JdbcClient jdbcClient;

   public JdbcJettySessionManagerFactory(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public SessionManager newSessionManager(JettyLogger logger, Server server, Map<String, Object> props) {
      JdbcSessionStorageImpl jdbcStorage = new JdbcSessionStorageImpl(logger, jdbcClient);
      SessionIdManagerImpl sessionIdManager = new SessionIdManagerImpl(logger, jdbcStorage, server);

      String clusterName = getClusterName(props);
      logger.debug("Creating session manager for jetty-cluster - [%s]", clusterName);

      sessionIdManager.setWorkerName(clusterName);
      sessionIdManager.setScavengeInterval(getScavengeInterval(props));

      SessionManagerImpl sessionManager = new SessionManagerImpl(logger, jdbcStorage);
      sessionManager.setSessionIdManager(sessionIdManager);
      sessionManager.setSaveInterval(getSaveInterval(props));
      return sessionManager;
   }

   private String getClusterName(Map<String, Object> props) {
      String randomClusterName = String.valueOf(getRandomWorkerNumber());
      String clusterName = JettyUtil.get(props, JETTY_JDBC_SESSION__CLUSTER_NAME, randomClusterName);
      return normalizeClusterName(clusterName);
   }

   private String normalizeClusterName(String clusterName) {
      return Strings.isValid(clusterName) ? clusterName.replaceAll("\\.", "-") : clusterName;
   }

   private int getScavengeInterval(Map<String, Object> props) {
      return JettyUtil.getInt(props, JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS,
         DEFAULT_JETTY_JDBC_SESSION__SCANVENGE_INTERVAL_SECS);
   }

   private int getSaveInterval(Map<String, Object> props) {
      return JettyUtil.getInt(props, JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS,
         DEFAULT_JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS);
   }

   private int getRandomWorkerNumber() {
      long seed = new Date().getTime();
      Random rand = new Random(seed);
      return 1000 + rand.nextInt(8999);
   }
}