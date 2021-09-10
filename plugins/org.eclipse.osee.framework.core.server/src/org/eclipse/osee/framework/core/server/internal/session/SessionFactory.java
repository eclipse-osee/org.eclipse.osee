/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Date;
import java.util.Properties;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class SessionFactory implements IOseeTypeFactory {
   private final Log logger;
   private final JdbcService jdbcService;

   public SessionFactory(Log logger, JdbcService jdbcService) {
      this.logger = logger;
      this.jdbcService = jdbcService;
   }

   public Session createLoadedSession(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
      Session toReturn =
         createNewSession(guid, userId, creationDate, clientVersion, clientMachineName, clientAddress, clientPort);
      return toReturn;
   }

   public Session createNewSession(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
      Session toReturn =
         new Session(guid, userId, creationDate, clientVersion, clientMachineName, clientAddress, clientPort);
      return toReturn;
   }

   public OseeSessionGrant createSessionGrant(Session session, UserToken userToken, String authenticationType) {
      Conditions.checkNotNull(session, "Session");
      Conditions.checkNotNull(userToken, "IUserToken");

      final JdbcClientConfig config = jdbcService.getClient().getConfig();

      OseeSessionGrant sessionGrant = new OseeSessionGrant(session.getGuid());
      sessionGrant.setAuthenticationProtocol(authenticationType);
      sessionGrant.setUserToken(userToken);
      sessionGrant.setDbIsProduction(config.isProduction());
      sessionGrant.setDbLogin(config.getDbUsername());
      sessionGrant.setDbId(jdbcService.getId());
      sessionGrant.setDbDriver(config.getDbDriver());
      sessionGrant.setDbLogin(config.getDbUsername());
      sessionGrant.setDbUrl(config.getDbUri());
      boolean useOracleHints = OseeSql.useOracleHints(config.getDbProps());
      sessionGrant.setDbConnectionProperties(config.getDbProps());
      sessionGrant.setDbDatabaseName(jdbcService.hasServer() ? jdbcService.getServerConfig().getDbName() : "");
      sessionGrant.setDbDatabasePath(jdbcService.hasServer() ? jdbcService.getServerConfig().getDbPath() : "");

      Properties properties =
         OseeSql.getSqlProperties(jdbcService.getClient().getDbType().areHintsSupported(), useOracleHints);
      sessionGrant.setSqlProperties(properties);

      sessionGrant.setDataStorePath(OseeServerProperties.getOseeApplicationServerData(logger));
      return sessionGrant;
   }

}
