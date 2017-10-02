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
   public String getSystemUuid()  {
      return null;
   }

   @Override
   public String getValue(String key)  {
      return null;
   }

   @Override
   public String getCachedValue(String key)  {
      return null;
   }

   @Override
   public boolean isEnabled(String key)  {
      return false;
   }

   @Override
   public boolean isCacheEnabled(String key)  {
      return false;
   }

   @Override
   public void setEnabled(String key, boolean enabled)  {
      //
   }

   @Override
   public void setBoolean(String key, boolean value)  {
      //
   }

   @Override
   public boolean isBoolean(String key)  {
      return false;
   }

   @Override
   public void putValue(String key, String value)  {
      //
   }

   @Override
   public Set<String> getKeys()  {
      return null;
   }

   @Override
   public boolean isBooleanUsingCache(String key)  {
      return false;
   }

   @Override
   public String getCachedValue(String key, long maxStaleness)  {
      return Strings.emptyString();
   }

}
