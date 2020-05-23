/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jdbc;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__LOCAL_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__REMOTE_CONNECTIONS;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.internal.JdbcServerFactory;
import org.eclipse.osee.jdbc.internal.JdbcUtil;

/**
 * Class used to configure and build embedded database server instances.
 *
 * @author Roberto E. Escobar
 */
public final class JdbcServerBuilder extends JdbcServerConfig {

   public static JdbcServerBuilder newBuilder() {
      return new JdbcServerBuilder();
   }

   public static JdbcServerBuilder newBuilder(JdbcServerConfig config) {
      return newBuilder().withConfig(config);
   }

   public static JdbcServerBuilder newBuilder(Map<String, Object> properties) {
      return newBuilder().properties(properties);
   }

   public static JdbcServer fromConfig(JdbcServerConfig config) {
      return newBuilder(config).build();
   }

   public static JdbcServerBuilder hsql() {
      return hsql(null);
   }

   public static JdbcServerBuilder hsql(String dataPath) {
      JdbcServerBuilder builder = newBuilder();
      builder.dbUsername("public");
      builder.dbPassword("");
      builder.dbParam("hsqldb.tx", "MVCC");
      if (Strings.isValid(dataPath)) {
         builder.dbPath(dataPath);
      }
      return builder;
   }

   ////////////////////////////////////////////////////////////////////////////////

   private JdbcLogger logger;
   private boolean loggingEnabled;

   private JdbcServerBuilder() {
      // Builder
   }

   public JdbcServer build() {
      JdbcServerConfig cfg = copy();
      if (!Strings.isValid(cfg.getDbHost())) {
         if (cfg.acceptRemoteConnections()) {
            cfg.setDbAcceptAddress(JDBC_SERVER__REMOTE_CONNECTIONS);
         } else {
            cfg.setDbAcceptAddress(JDBC_SERVER__LOCAL_CONNECTIONS);
         }
      }
      if (cfg.useRandomPort()) {
         int port = JdbcUtil.getRandomPort();
         cfg.setDbListenPort(port);
      }

      if (JdbcConstants.DEFAULT_JDBC_SERVER__IMPL_CLASSNAME.equals(cfg.getServerImplClassName())) {
         if (!cfg.getProperties().containsKey("hsqldb.tx")) {
            cfg.addDbParam("hsqldb.tx", "MVCC");
         }
      }
      return JdbcServerFactory.newJbdcServer(cfg, loggingEnabled, logger);
   }

   public JdbcServerBuilder properties(Map<String, Object> src) {
      readProperties(src);
      return this;
   }

   public JdbcServerBuilder withConfig(JdbcServerConfig config) {
      this.copy(config);
      return this;
   }

   public JdbcServerBuilder dbListenOn(String value) {
      setDbAcceptAddress(value);
      return this;
   }

   public JdbcServerBuilder dbListenPort(int value) {
      setDbListenPort(value);
      return this;
   }

   public JdbcServerBuilder dbPath(String value) {
      setDbPath(value);
      return this;
   }

   public JdbcServerBuilder dbUsername(String value) {
      setDbUsername(value);
      return this;
   }

   public JdbcServerBuilder dbPassword(String value) {
      setDbPassword(value);
      return this;
   }

   public JdbcServerBuilder dbParam(String key, String value) {
      addDbParam(key, value);
      return this;
   }

   public JdbcServerBuilder startUpWaitTimeMillis(long startUpWaitTimeout) {
      setStartUpWaitTimeout(startUpWaitTimeout);
      return this;
   }

   public JdbcServerBuilder aliveWaitTimeMillis(long aliveWaitTimeout) {
      setAliveWaitTimeout(aliveWaitTimeout);
      return this;
   }

   public JdbcServerBuilder useRandomPort(boolean useRandomPort) {
      setUseRandomPort(useRandomPort);
      return this;
   }

   public JdbcServerBuilder logging(boolean loggingEnabled) {
      this.loggingEnabled = loggingEnabled;
      return this;
   }

   public JdbcServerBuilder logger(JdbcLogger logger) {
      this.logger = logger;
      return this;
   }

}