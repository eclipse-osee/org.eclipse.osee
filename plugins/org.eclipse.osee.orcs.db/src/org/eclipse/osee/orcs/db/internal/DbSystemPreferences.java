/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.SystemPreferences;

public class DbSystemPreferences implements SystemPreferences {

   @Override
   public String getSystemUuid() throws OseeCoreException {
      return null;
   }

   @Override
   public String getValue(String key) throws OseeCoreException {
      return null;
   }

   @Override
   public String getCachedValue(String key) throws OseeCoreException {
      return null;
   }

   @Override
   public boolean isEnabled(String key) throws OseeCoreException {
      return false;
   }

   @Override
   public boolean isCacheEnabled(String key) throws OseeCoreException {
      return false;
   }

   @Override
   public void setEnabled(String key, boolean enabled) throws OseeCoreException {
   }

   @Override
   public void setBoolean(String key, boolean value) throws OseeCoreException {
   }

   @Override
   public boolean isBoolean(String key) throws OseeCoreException {
      return false;
   }

   @Override
   public void putValue(String key, String value) throws OseeCoreException {
   }

}
