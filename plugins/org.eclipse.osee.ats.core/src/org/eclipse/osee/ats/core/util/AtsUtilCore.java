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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {

   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   public static final String DEFAULT_ATS_ID_VALUE = "0";
   public static final String USER_CREATION_DISABLED = "UserCreationDisabled2";
   public static final String ATS_CONFIG_ACTION_URL_KEY = "ActionUrl_26_0";
   public static final String ATS_DEFAULT_ACTION_URL = "/ats/ui/action/ID";
   public final static String SERVER_CONFIG_RELOAD_MIN_KEY = "server_config_reload_min";
   public final static long SERVER_CONFIG_RELOAD_MIN_DEFAULT = 30;
   public static final String SINGLE_SERVER_DEPLOYMENT = "single_server_deployment";

   private static Map<Long, String> idToGuidMap = new HashMap<>(50);

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

   public static Dictionary<String, ?> hashTable(String key, String value) {
      Hashtable<String, String> table = new Hashtable<>();
      table.put(key, value);
      return table;
   }

   public static String getActionUrl(String atsIdOrid, AtsApi atsApi) {
      return getActionUrl(atsIdOrid, ATS_DEFAULT_ACTION_URL, atsApi);
   }

   public static String getActionUrl(String atsIdOrid, String defaultUrl, AtsApi atsApi) {
      String actionUrl = getBaseActionUiUrl(defaultUrl, atsApi);
      actionUrl = actionUrl.replaceFirst("ID", atsIdOrid);
      return actionUrl;
   }

   public static String getBaseActionUiUrl(String defaultUrl, AtsApi atsApi) {
      return atsApi.getConfigValue(AtsUtilCore.ATS_CONFIG_ACTION_URL_KEY, defaultUrl);
   }

   public static String resolveAjaxToBaseApplicationServer(String html, AtsApi atsApi) {
      String basePath = atsApi.getConfigValue(AtsConfigKey.AJaxBasePath, atsApi.getApplicationServerBase());
      if (Strings.isValid(basePath)) {
         return html.replaceFirst("\\/ajax", basePath + "/ajax");
      }
      return html;
   }
}