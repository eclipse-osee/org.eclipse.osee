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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {

   private static final String ATS_BRANCH_NAME = "ats.branch.name";
   private static final String ATS_BRANCH_UUID = "ats.branch.uuid";
   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   public static final String DEFAULT_ATS_ID_VALUE = "0";
   public static final String USER_CREATION_DISABLED = "UserCreationDisabled2";
   public static final String ATS_CONFIG_ACTION_URL_KEY = "ActionUrl";
   public static final String ATS_DEFAULT_ACTION_URL = "/ats/ui/action/UUID";

   private static final Object lock = new Object();
   private volatile static BranchId atsBranch;
   private volatile static String atsConfigName;
   private static Map<Long, String> uuidToGuidMap = new HashMap<>(50);
   private static Map<String, Long> guidToUuidMap = new HashMap<>(50);

   public static String getAtsConfigName() {
      getAtsBranch();
      return atsConfigName;
   }

   public static BranchId getAtsBranch() {
      synchronized (lock) {
         if (atsBranch == null) {
            // Preference store overrides all
            if (AtsPreferencesService.isAvailable()) {
               try {
                  String atsBranchUuid = AtsPreferencesService.get(ATS_BRANCH_UUID);
                  setConfig(atsBranchUuid, AtsPreferencesService.get(ATS_BRANCH_NAME));
               } catch (Exception ex) {
                  OseeLog.log(AtsUtilCore.class, Level.SEVERE, "Error processing stored ATS Branch.", ex);
               }
            }
            // osee.ini -D option overrides default
            if (atsBranch == null) {
               String atsBranchUuid = System.getProperty(ATS_BRANCH_UUID);
               if (Strings.isValid(atsBranchUuid)) {
                  setConfig(atsBranchUuid, System.getProperty(ATS_BRANCH_NAME));
               }
            }
            // default is always common
            if (atsBranch == null) {
               atsBranch = CoreBranches.COMMON;
               atsConfigName = CoreBranches.COMMON.getName();
            }
         }
      }
      return atsBranch;
   }

   private static void setConfig(String branchUuid, String name) {
      if (!Strings.isValid(name)) {
         name = "unknown";
      }
      if (Strings.isValid(branchUuid) && branchUuid.matches("\\d+")) {
         atsBranch = TokenFactory.createBranch(Long.valueOf(branchUuid), name);
         atsConfigName = name;
      }
   }

   public static List<String> toGuids(Collection<? extends IAtsObject> atsObjects) {
      List<String> guids = new ArrayList<>(atsObjects.size());
      for (IAtsObject atsObj : atsObjects) {
         guids.add(AtsUtilCore.getGuid(atsObj));
      }
      return guids;
   }

   public static void storeAtsBranch(BranchId branch, String name) {
      AtsPreferencesService.get().put(ATS_BRANCH_UUID, String.valueOf(branch.getUuid()));
      AtsPreferencesService.get().put(ATS_BRANCH_NAME, name);
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

   public static String toStringWithId(Identifiable<String> obj) {
      Conditions.checkNotNull(obj, "object");
      return String.format("[%s][%s]", obj.getGuid(), obj.getName());
   }

   public static String toStringWithId(IAtsObject obj) {
      Conditions.checkNotNull(obj, "object");
      return String.format("[%s][%s]", obj.getId(), obj.getName());
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
      guidToUuidMap.put(guid, atsObject.getId());
   }

   public static Long getUuidFromGuid(String guid) {
      return guidToUuidMap.get(guid);
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
