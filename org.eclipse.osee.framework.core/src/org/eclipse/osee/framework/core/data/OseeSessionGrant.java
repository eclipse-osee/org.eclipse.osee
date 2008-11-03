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
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeSessionGrant extends BaseExchangeData {

   private static final long serialVersionUID = -7236201704435470272L;
   private static final String SESSION_ID = "sessionId";
   private static final String DB_CONNECTION_DRIVER = "dbDriver";
   private static final String DB_CONNECTION_URL = "dbUrl";
   private static final String DB_CONNECT_PROPERTIES = "dbConnectionProperties";
   private static final String USER_ARTIFACT_ID = "userArtifactId";
   private static final String DB_CONNECTION_NAME = "dbConnectionName";
   private static final String SQL_PROPERTIES = "slqProperty";

   protected OseeSessionGrant() {
      super();
   }

   public OseeSessionGrant(String sessionId) {
      super();
      this.properties.put(SESSION_ID, sessionId);
   }

   public void setDbDriver(String driver) {
      this.properties.put(DB_CONNECTION_DRIVER, driver);
   }

   public void setDbUrl(String dbUrl) {
      this.properties.put(DB_CONNECTION_URL, dbUrl);
   }

   public void setDbConnectionName(String dbName) {
      this.properties.put(DB_CONNECTION_NAME, dbName);
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

   public String getDbConnectionName() {
      return getString(DB_CONNECTION_NAME);
   }

   public String getDbDriver() {
      return getString(DB_CONNECTION_DRIVER);
   }

   public String getDbUrl() {
      return getString(DB_CONNECTION_URL);
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

   public void setDbConnectionProperties(Properties connectProperties) {
      this.properties.put(DB_CONNECT_PROPERTIES, connectProperties.toString());
   }

   public Properties getDbConnectionProperties() throws OseeWrappedException {
      String values = getString(DB_CONNECT_PROPERTIES);
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

}
