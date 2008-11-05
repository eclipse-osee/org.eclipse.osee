/*
 * Created on Oct 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.CoreActivator;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeSessionGrant extends BaseExchangeData {

   private static final long serialVersionUID = -7236201704435470272L;
   private static final String SESSION_ID = "sessionId";
   private static final String USER_ARTIFACT_ID = "userArtifactId";
   private static final String SQL_PROPERTIES = "slqProperty";

   private static final String DB_DRIVER = "dbDriver";
   private static final String DB_CONNECTION_URL = "dbUrl";
   private static final String DB_CONNECT_PROPERTIES = "dbConnectionProperties";
   private static final String DB_DEFAULT_ARB_SERVER = "dbArbServer";
   private static final String DB_LOGIN_NAME = "dbLogin";
   private static final String DB_DATABASE_NAME = "dbDatabaseName";
   private static final String DB_IS_PRODUCTION = "dbIsProduction";
   private static final String DB_ID = "dbId";

   private IDatabaseInfo grantedDatabaseInfo;

   protected OseeSessionGrant() {
      super();
      this.grantedDatabaseInfo = new GrantedDatabaseInfo();
   }

   public OseeSessionGrant(String sessionId) {
      super();
      this.properties.put(SESSION_ID, sessionId);
   }

   public void setUserArtifactId(String userArtifactId) {
      this.properties.put(USER_ARTIFACT_ID, userArtifactId);
   }

   public String getUserArtifactId() {
      return getString(USER_ARTIFACT_ID);
   }

   public String getSessionId() {
      return getString(SESSION_ID);
   }

   public IDatabaseInfo getDatabaseInfo() {
      return grantedDatabaseInfo;
   }

   public void setDatabaseInfo(IDatabaseInfo dbInfo) {
      this.properties.put(DB_DRIVER, dbInfo.getDriver());
      this.properties.put(DB_CONNECTION_URL, dbInfo.getConnectionUrl());
      this.properties.put(DB_DEFAULT_ARB_SERVER, dbInfo.getDefaultArbitrationServer());
      this.properties.put(DB_LOGIN_NAME, dbInfo.getDatabaseLoginName());
      this.properties.put(DB_DATABASE_NAME, dbInfo.getDatabaseName());
      this.properties.put(DB_IS_PRODUCTION, Boolean.toString(dbInfo.isProduction()));
      this.properties.put(DB_ID, dbInfo.getId());
      this.properties.put(DB_CONNECT_PROPERTIES, dbInfo.getConnectionProperties().toString());
   }

   public void setSqlProperties(Properties connectProperties) {
      this.properties.put(SQL_PROPERTIES, connectProperties.toString());
   }

   public Properties getSqlProperties() throws OseeWrappedException {
      String values = getString(SQL_PROPERTIES);
      if (Strings.isValid(values)) {
         try {
            properties.load(new StringReader(values.substring(1, values.length() - 1)));
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
      return properties;
   }

   public static OseeSessionGrant fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeSessionGrant session = new OseeSessionGrant();
      session.loadfromXml(inputStream);
      return session;
   }

   private final class GrantedDatabaseInfo implements IDatabaseInfo {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionProperties()
       */
      @Override
      public Properties getConnectionProperties() {
         String values = getString(DB_CONNECT_PROPERTIES);
         if (Strings.isValid(values)) {
            try {
               properties.load(new StringReader(values.substring(1, values.length() - 1)));
            } catch (IOException ex) {
               OseeLog.log(CoreActivator.class, Level.SEVERE, ex);
            }
         }
         return properties;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionUrl()
       */
      @Override
      public String getConnectionUrl() {
         return getString(DB_CONNECTION_URL);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseLoginName()
       */
      @Override
      public String getDatabaseLoginName() {
         return getString(DB_LOGIN_NAME);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseName()
       */
      @Override
      public String getDatabaseName() {
         return getString(DB_DATABASE_NAME);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDefaultArbitrationServer()
       */
      @Override
      public String getDefaultArbitrationServer() {
         return getString(DB_DEFAULT_ARB_SERVER);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDriver()
       */
      @Override
      public String getDriver() {
         return getString(DB_DRIVER);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getId()
       */
      @Override
      public String getId() {
         return getString(DB_ID);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#isProduction()
       */
      @Override
      public boolean isProduction() {
         return Boolean.valueOf(getString(DB_IS_PRODUCTION));
      }

   }

}
