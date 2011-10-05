/*
 * Created on Oct 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.mock;

import org.eclipse.osee.orcs.core.SystemPreferences;

public class H2Preferences implements SystemPreferences {

   @Override
   public String getSystemUuid() {
      return null;
   }

   @Override
   public String getValue(String key) {
      return null;
   }

   @Override
   public String getCachedValue(String key) {
      return null;
   }

   @Override
   public boolean isEnabled(String key) {
      return false;
   }

   @Override
   public boolean isCacheEnabled(String key) {
      return false;
   }

   @Override
   public void setEnabled(String key, boolean enabled) {
      //doing nothing
   }

   @Override
   public void setBoolean(String key, boolean value) {
      //doing nothing
   }

   @Override
   public boolean isBoolean(String key) {
      return false;
   }

   @Override
   public void putValue(String key, String value) {
      //doing nothing
   }

}
