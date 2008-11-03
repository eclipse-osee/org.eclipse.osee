/*
 * Created on Oct 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class BaseExchangeData implements Serializable {
   private static final long serialVersionUID = -3844333805269321833L;
   private static final String EMPTY_STRING = "";
   protected final Properties properties;

   public BaseExchangeData() {
      super();
      this.properties = new Properties();
   }

   protected String getString(String key) {
      String toReturn = this.properties.getProperty(key);
      return toReturn != null ? toReturn : EMPTY_STRING;
   }

   /**
    * Set data from XML input stream
    * 
    * @param xml inputStream
    * @throws OseeWrappedException
    */
   protected void loadfromXml(InputStream inputStream) throws OseeWrappedException {
      try {
         this.properties.loadFromXML(inputStream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   /**
    * Write to output stream
    * 
    * @param outputStream
    * @throws OseeWrappedException
    */
   public void write(OutputStream outputStream) throws OseeWrappedException {
      try {
         properties.storeToXML(outputStream, String.format("Type: %s", this.getClass().getCanonicalName()), "UTF-8");
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public String toString() {
      return this.properties.toString();
   }
}
