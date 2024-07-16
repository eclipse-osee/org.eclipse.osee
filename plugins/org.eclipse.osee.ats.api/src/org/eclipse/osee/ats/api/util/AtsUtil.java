/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsUtil {

   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   public static final String DEFAULT_ATS_ID_VALUE = "0";
   public static final String USER_CREATION_DISABLED = "UserCreationDisabled2";
   public static final String ATS_CONFIG_ACTION_URL_KEY = "ActionUrl_26_0";
   public static final String ATS_DEFAULT_ACTION_URL = "/ats/ui/action/ID";
   public static final String SINGLE_SERVER_DEPLOYMENT = "single_server_deployment";
   public static final String WORK_PKG_STATIC_ID = "Work Package Enum Art";
   private static boolean emailEnabled;

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static void setIsInTest(boolean isInText) {
      System.setProperty("osee.isInTest", String.valueOf(isInText));
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
      return getActionUrl(atsIdOrid, "", atsApi);
   }

   public static String getActionUrl(String atsIdOrid, String defaultUrl, AtsApi atsApi) {
      String actionUrl = getBaseActionUiUrl(defaultUrl, atsApi);
      actionUrl = actionUrl.replaceFirst("ID", atsIdOrid);
      return actionUrl;
   }

   public static String getBaseActionUiUrl(String defaultUrl, AtsApi atsApi) {
      String configUrl = atsApi.getConfigValue(AtsUtil.ATS_CONFIG_ACTION_URL_KEY, defaultUrl);
      if (Strings.isInValid(configUrl)) {
         configUrl = System.getProperty("osee.application.server") + ATS_DEFAULT_ACTION_URL;
      }
      return configUrl;
   }

   public static String resolveAjaxToBaseApplicationServer(String html, AtsApi atsApi) {
      String basePath = atsApi.getConfigValue(AtsConfigKey.AJaxBasePath, atsApi.getApplicationServerBase());
      if (Strings.isValid(basePath)) {
         return html.replaceFirst("\\/ajax", basePath + "/ajax");
      }
      return html;
   }

   public static void setEmailEnabled(boolean enabled) {
      AtsUtil.emailEnabled = enabled;
   }

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }
}