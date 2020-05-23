/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.account.rest.model;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class AccountUtil {

   public static String updateSinglePreference(AccountWebPreferences allPreferences, String key, String id, String newValue) {
      try {
         JSONObject preferencesJObject = new JSONObject(allPreferences);
         JSONObject singlePreferenceObject = preferencesJObject.getJSONObject(key);

         JSONObject newValueAsObject;
         if (!Strings.isValid(id)) {
            newValueAsObject = createNewPreference(newValue);
            singlePreferenceObject.put(newValueAsObject.getString("id"), newValueAsObject);
         } else {
            newValueAsObject = new JSONObject(newValue);
            if (newValueAsObject.toString().equals("{}")) {
               singlePreferenceObject.remove(id);
            } else {
               singlePreferenceObject.put(id, newValueAsObject);
            }
         }

         preferencesJObject.put(key, singlePreferenceObject);

         return preferencesJObject.toString();
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
   }

   private static JSONObject createNewPreference(String newValue) {
      String newId = GUID.create();
      try {
         JSONObject newObject = new JSONObject(newValue);
         newObject.put("id", newId);
         return newObject;
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
   }
}
