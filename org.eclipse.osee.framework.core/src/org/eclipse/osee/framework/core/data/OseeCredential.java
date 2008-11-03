/*
 * Created on Oct 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class OseeCredential extends OseeClientInfo {
   private static final long serialVersionUID = 4583587251351958961L;
   private static final String USER_ID = "userId";
   private static final String PASSWORD = "password";
   private static final String DOMAIN = "domain";

   public OseeCredential() {
      super();
   }

   public void setUserId(String userId) {
      this.properties.put(USER_ID, userId);
   }

   public void setPassword(String password) {
      this.properties.put(PASSWORD, password);
   }

   public void setDomain(String domain) {
      this.properties.put(DOMAIN, domain);
   }

   public String getUserId() {
      return getString(USER_ID);
   }

   public String getPassword() {
      return getString(PASSWORD);
   }

   public String getDomain() {
      return getString(DOMAIN);
   }

   /**
    * Create new instance from XML input
    * 
    * @param OseeCredential the new instance
    * @throws OseeWrappedException
    */
   public static OseeCredential fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeCredential session = new OseeCredential();
      session.loadfromXml(inputStream);
      return session;
   }
}
