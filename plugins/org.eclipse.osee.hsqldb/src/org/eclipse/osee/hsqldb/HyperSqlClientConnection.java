package org.eclipse.osee.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;

/**
 * @author Roberto E. Escobar
 */
public class HyperSqlClientConnection implements IConnectionFactory {

   private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

   private final AtomicBoolean firstTime = new AtomicBoolean(true);

   @Override
   public Connection getConnection(IDatabaseInfo dbInfo) throws Exception {
      Class.forName(JDBC_DRIVER);

      if (firstTime.compareAndSet(true, false)) {
         Pair<String, Integer> addressAndPort = OseeProperties.getOseeDbEmbeddedServerAddress();
         if (addressAndPort != null) {
            int webPort = OseeProperties.getOseeDbEmbeddedWebServerPort();
            if (webPort < 0) {
               webPort = PortUtil.getInstance().getValidPort();
            }
            HyperSqlDbServer.startServer(addressAndPort.getFirst(), addressAndPort.getSecond(), webPort, dbInfo);
         }
      }

      Properties props = dbInfo.getConnectionProperties();

      StringBuilder builder = new StringBuilder();
      builder.append(dbInfo.getConnectionUrl());
      HyperSqlServerMgr.appendProperties(builder, props);

      String url = builder.toString();
      return DriverManager.getConnection(url, props);
   }

   @Override
   public String getDriver() {
      return JDBC_DRIVER;
   }

}
