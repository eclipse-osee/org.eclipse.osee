/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.user.IAtsPreferences;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsPreferencesService {

   private static IAtsPreferences preferences = null;

   public static IAtsPreferences get() {
      return preferences;
   }

   public static String get(String key) {
      String value = null;
      if (preferences != null) {
         value = preferences.get(key);
      }
      return value;
   }

   public static void put(String key, String value) {
      if (preferences == null) {
         throw new OseeStateException("No Available Preference Service");
      }
      preferences.put(key, value);
   }

   public void setAtsPreferences(IAtsPreferences preferences) {
      AtsPreferencesService.preferences = preferences;
   }

   public static boolean isAvailable() {
      return preferences != null;
   }

}
