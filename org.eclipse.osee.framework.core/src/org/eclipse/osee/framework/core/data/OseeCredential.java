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
   private static final String USERNAME = "userName";
   private static final String PASSWORD = "password";
   private static final String DOMAIN = "domain";
   private static final String AUTHENTICATION_PROTOCOL = "authenticationProtocol";

   public OseeCredential() {
      super();
   }

   public void setUserName(String userName) {
      this.backingData.put(USERNAME, userName);
   }

   public void setPassword(String password) {
      this.backingData.put(PASSWORD, password);
   }

   public void setDomain(String domain) {
      this.backingData.put(DOMAIN, domain);
   }

   public String getUserName() {
      return getString(USERNAME);
   }

   public String getPassword() {
      return getString(PASSWORD);
   }

   public String getDomain() {
      return getString(DOMAIN);
   }

   public String getAuthenticationProtocol() {
      return getString(AUTHENTICATION_PROTOCOL);
   }

   public void setAuthenticationProtocol(String protocol) {
      this.backingData.put(AUTHENTICATION_PROTOCOL, protocol);
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
