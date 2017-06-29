/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {

   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   public static final String DEFAULT_ATS_ID_VALUE = "0";
   public static final String USER_CREATION_DISABLED = "UserCreationDisabled2";
   public static final String ATS_CONFIG_ACTION_URL_KEY = "ActionUrl";
   public static final String ATS_DEFAULT_ACTION_URL = "/ats/ui/action/UUID";

   private static Map<Long, String> uuidToGuidMap = new HashMap<>(50);

   public static List<String> toGuids(Collection<? extends IAtsObject> atsObjects) {
      List<String> guids = new ArrayList<>(atsObjects.size());
      for (IAtsObject atsObj : atsObjects) {
         guids.add(AtsUtilCore.getGuid(atsObj));
      }
      return guids;
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static String getGuid(IAtsObject atsObject) {
      String guid = uuidToGuidMap.get(atsObject.getId());
      if (!Strings.isValid(guid) && atsObject.getStoreObject() != null) {
         guid = atsObject.getStoreObject().getGuid();
      }
      return guid;
   }

   public static String getGuid(Long uuid) {
      return uuidToGuidMap.get(uuid);
   }

   public static void putUuidToGuid(String guid, IAtsObject atsObject) {
      uuidToGuidMap.put(atsObject.getId(), guid);
   }

   public static Dictionary<String, ?> hashTable(String key, String value) {
      Hashtable<String, String> table = new Hashtable<>();
      table.put(key, value);
      return table;
   }

   public static String getActionUrl(String atsIdOrUuid, IAtsServices services) {
      return getActionUrl(atsIdOrUuid, ATS_DEFAULT_ACTION_URL, services);
   }

   public static String getActionUrl(String atsIdOrUuid, String defaultUrl, IAtsServices services) {
      String actionUrl = getBaseActionUiUrl(defaultUrl, services);
      actionUrl = actionUrl.replaceFirst("UUID", atsIdOrUuid);
      return actionUrl;
   }

   public static String getBaseActionUiUrl(String defaultUrl, IAtsServices services) {
      String actionUrl = services.getConfigValue(AtsUtilCore.ATS_CONFIG_ACTION_URL_KEY);
      if (!Strings.isValid(actionUrl)) {
         actionUrl = defaultUrl;
      }
      return actionUrl;
   }

}
