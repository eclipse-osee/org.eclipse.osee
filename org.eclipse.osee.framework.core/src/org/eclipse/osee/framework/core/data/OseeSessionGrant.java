/*
 * Created on Oct 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.util.Properties;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class OseeSessionGrant extends BaseExchangeData {

   private static final long serialVersionUID = -7236201704435470272L;
   private static final String SESSION_ID = "sessionId";
   private static final String SQL_PROPERTIES = "slqProperty";

   private static final String DB_DRIVER = "dbDriver";
   private static final String DB_CONNECTION_URL = "dbUrl";
   private static final String DB_CONNECT_PROPERTIES = "dbConnectionProperties";
   private static final String DB_DEFAULT_ARB_SERVER = "dbArbServer";
   private static final String DB_LOGIN_NAME = "dbLogin";
   private static final String DB_DATABASE_NAME = "dbDatabaseName";
   private static final String DB_IS_PRODUCTION = "dbIsProduction";
   private static final String DB_ID = "dbId";

   private static final String OSEE_USER_IS_CREATION_REQUIRED = "oseeUserNeedsCreation";
   private static final String OSEE_USER_EMAIL = "oseeUserEmail";
   private static final String OSEE_USER_NAME = "oseeUserName";
   private static final String OSEE_USER_ID = "oseeUserId";
   private static final String OSEE_IS_USER_ACTIVE = "isOseeUserActive";

   private IDatabaseInfo grantedDatabaseInfo;
   private IOseeUser grantedOseeUserInfo;

   protected OseeSessionGrant() {
      super();
      this.grantedDatabaseInfo = new GrantedDatabaseInfo();
      this.grantedOseeUserInfo = new GrantedOseeUserInfo();
   }

   public OseeSessionGrant(String sessionId) {
      super();
      this.backingData.put(SESSION_ID, sessionId);
   }

   public String getSessionId() {
      return getString(SESSION_ID);
   }

   public IDatabaseInfo getDatabaseInfo() {
      return grantedDatabaseInfo;
   }

   public void setDatabaseInfo(IDatabaseInfo dbInfo) {
      this.backingData.put(DB_DRIVER, dbInfo.getDriver());
      this.backingData.put(DB_CONNECTION_URL, dbInfo.getConnectionUrl());
      this.backingData.put(DB_DEFAULT_ARB_SERVER, dbInfo.getDefaultArbitrationServer());
      this.backingData.put(DB_LOGIN_NAME, dbInfo.getDatabaseLoginName());
      this.backingData.put(DB_DATABASE_NAME, dbInfo.getDatabaseName());
      this.backingData.put(DB_IS_PRODUCTION, dbInfo.isProduction());
      this.backingData.put(DB_ID, dbInfo.getId());
      putProperties(DB_CONNECT_PROPERTIES, dbInfo.getConnectionProperties());
   }

   public void setSqlProperties(Properties sqlProperties) {
      putProperties(SQL_PROPERTIES, sqlProperties);
   }

   public Properties getSqlProperties() {
      return getPropertyString(SQL_PROPERTIES);
   }

   public void setOseeUserInfo(IOseeUser userInfo) {
      this.backingData.put(OSEE_USER_EMAIL, userInfo.getEmail());
      this.backingData.put(OSEE_USER_NAME, userInfo.getName());
      this.backingData.put(OSEE_USER_ID, userInfo.getUserID());
      this.backingData.put(OSEE_IS_USER_ACTIVE, userInfo.isActive());
   }

   public boolean isCreationRequired() {
      return backingData.getBoolean(OSEE_USER_IS_CREATION_REQUIRED);
   }

   public void setCreationRequired(boolean value) {
      this.backingData.put(OSEE_USER_IS_CREATION_REQUIRED, value);
   }

   public IOseeUser getOseeUserInfo() {
      return grantedOseeUserInfo;
   }

   public static OseeSessionGrant fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeSessionGrant session = new OseeSessionGrant();
      session.loadfromXml(inputStream);
      return session;
   }

   private final class GrantedOseeUserInfo implements IOseeUser {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getEmail()
       */
      @Override
      public String getEmail() {
         return getString(OSEE_USER_EMAIL);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getName()
       */
      @Override
      public String getName() {
         return getString(OSEE_USER_NAME);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getUserID()
       */
      @Override
      public String getUserID() {
         return getString(OSEE_USER_ID);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#isActive()
       */
      @Override
      public boolean isActive() {
         return backingData.getBoolean(OSEE_IS_USER_ACTIVE);
      }
   }

   private final class GrantedDatabaseInfo implements IDatabaseInfo {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionProperties()
       */
      @Override
      public Properties getConnectionProperties() {
         return getPropertyString(DB_CONNECT_PROPERTIES);
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
