/*
 * Created on Apr 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

import java.util.Properties;

/**
 * @author Roberto E. Escobar
 */
public class Options {

   private Properties properties;

   public Options() {
      this.properties = new Properties();
   }

   public boolean getBoolean(String key) {
      return new Boolean(getString(key));
   }

   public String getString(String key) {
      return this.properties.getProperty(key, "");
   }

   public void put(String key, String value) {
      if (value != null && value.length() > 0) {
         this.properties.put(key, value);
      }
   }

   public void put(String key, boolean value) {
      this.properties.put(key, Boolean.toString(value));
   }
}
