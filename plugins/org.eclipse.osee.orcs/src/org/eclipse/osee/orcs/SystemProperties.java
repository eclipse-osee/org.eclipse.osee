/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs;

import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public interface SystemProperties {

   public static String OSEE_PERMANENT_URL = "osee.permanent.base.url";

   public Set<String> getKeys();

   public String getSystemUuid();

   public String getValue(String key);

   public String getCachedValue(String key);

   public String getCachedValue(String key, String defaultValue);

   public String getCachedValue(String key, long maxStaleness);

   /**
    * Return true if key is set and value = "true". Return false if key is either not set OR value != "true".<br>
    * <br>
    * Note: This call will hit the datastore every time, so shouldn't be used for often repeated calls. use
    * isCacheEnabled that will cache the value
    */
   public boolean isEnabled(String key);

   /**
    * Return true if key is set and value = "true". Return false if key is either not set OR value != "true".<br>
    * <br>
    * Return cached value (value only loaded once per session. Restart will reset value if changed
    */
   public boolean isCacheEnabled(String key);

   public void setEnabled(String key, boolean enabled);

   public void setBoolean(String key, boolean value);

   /**
    * Return true if key is set in osee_info table and value = "true". Return false if key is either not in osee_info
    * table OR value != "true".<br>
    * <br>
    * Note: This call will hit the database every time, so shouldn't be used for often repeated calls. use
    * isCacheEnabled that will cache the value
    */
   public boolean isBoolean(String key);

   public boolean isBooleanUsingCache(String key);

   public void putValue(String key, String value);
}
