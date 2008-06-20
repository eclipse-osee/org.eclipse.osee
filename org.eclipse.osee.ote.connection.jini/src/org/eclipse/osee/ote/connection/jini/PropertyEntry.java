package org.eclipse.osee.ote.connection.jini;
import java.util.Properties;
import net.jini.core.entry.Entry;

/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

/**
 * @author b1529404
 */
public class PropertyEntry implements Entry {

   private final Properties properties;
   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   public PropertyEntry() {
      this(new Properties());
   }

   public PropertyEntry(Properties properties) {
      this.properties = properties;
   }

   public void setProperty(String key, String value) {
      properties.setProperty(key, value);
   }

   public String getProperty(String key, String defaultValue) {
      return properties.getProperty(key, defaultValue);
   }
}
