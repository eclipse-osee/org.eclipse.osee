/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mocks;

import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.SystemPreferences;

/**
 * @author Roberto E. Escobar
 */
public class MockSystemPreferences implements SystemPreferences {

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
      //
   }

   @Override
   public void setBoolean(String key, boolean value) throws OseeCoreException {
      //
   }

   @Override
   public boolean isBoolean(String key) throws OseeCoreException {
      return false;
   }

   @Override
   public void putValue(String key, String value) throws OseeCoreException {
      //
   }

   @Override
   public Set<String> getKeys() throws OseeCoreException {
      return null;
   }

   @Override
   public boolean isBooleanUsingCache(String key) throws OseeCoreException {
      return false;
   }

   @Override
   public String getCachedValue(String key, long maxStaleness) throws OseeCoreException {
      return Strings.emptyString();
   }

}
