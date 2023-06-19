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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */

public class AccountUtil {

   public static String updateSinglePreference(AccountWebPreferences allPreferences, String key, String id,
      String newValue) {

      try {
         ObjectMapper OM = new ObjectMapper();
         JsonNode preferencesJObject = OM.readTree(allPreferences.toString());
         JsonNode singlePreferenceObject = OM.readTree(preferencesJObject.get(key).toString());
         HashMap jsonArray = new HashMap();
         JsonNode newValueAsObject;
         if (!Strings.isValid(id)) {
            newValueAsObject = createNewPreference(newValue);
            jsonArray.put(newValueAsObject.get("id"), newValueAsObject);
         } else {
            newValueAsObject = OM.readTree(newValue);
            if (newValueAsObject.toString().equals("{}")) {
               jsonArray.remove(id);
            } else {
               jsonArray.put(id, newValueAsObject);
            }
         }
         jsonArray.put(key, singlePreferenceObject);
         return preferencesJObject.toString();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private static JsonNode createNewPreference(String newValue) {
      String newId = GUID.create();
      try {
         ObjectMapper OM = new ObjectMapper();
         JsonNode newObject = OM.readTree(newValue);
         HashMap jsonArray = new HashMap();
         jsonArray.put("id", newId);
         return newObject;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

}