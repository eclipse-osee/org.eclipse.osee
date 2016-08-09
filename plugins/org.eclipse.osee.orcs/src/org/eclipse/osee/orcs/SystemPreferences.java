/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs;

import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface SystemPreferences {

   public Set<String> getKeys() throws OseeCoreException;

   public String getSystemUuid() throws OseeCoreException;

   public String getValue(String key) throws OseeCoreException;

   public String getCachedValue(String key) throws OseeCoreException;

   public String getCachedValue(String key, long maxStaleness) throws OseeCoreException;

   /**
    * Return true if key is set and value = "true". Return false if key is either not set OR value != "true".<br>
    * <br>
    * Note: This call will hit the datastore every time, so shouldn't be used for often repeated calls. use
    * isCacheEnabled that will cache the value
    */
   public boolean isEnabled(String key) throws OseeCoreException;

   /**
    * Return true if key is set and value = "true". Return false if key is either not set OR value != "true".<br>
    * <br>
    * Return cached value (value only loaded once per session. Restart will reset value if changed
    */
   public boolean isCacheEnabled(String key) throws OseeCoreException;

   public void setEnabled(String key, boolean enabled) throws OseeCoreException;

   public void setBoolean(String key, boolean value) throws OseeCoreException;

   /**
    * Return true if key is set in osee_info table and value = "true". Return false if key is either not in osee_info
    * table OR value != "true".<br>
    * <br>
    * Note: This call will hit the database every time, so shouldn't be used for often repeated calls. use
    * isCacheEnabled that will cache the value
    */
   public boolean isBoolean(String key) throws OseeCoreException;

   public boolean isBooleanUsingCache(String key) throws OseeCoreException;

   public void putValue(String key, String value) throws OseeCoreException;
}
