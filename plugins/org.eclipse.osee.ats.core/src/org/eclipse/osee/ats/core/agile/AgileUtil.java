/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.core.agile;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class AgileUtil {

   public static final String AGILE_DATE_FORMAT = DateUtil.MMDDYY;
   public static Boolean atsAgileUser;
   public static Boolean atsEarnedValueUser;

   /**
    * @return date string in format used throughout Agile Web
    */
   public static String getDateStr(Date date) {
      return DateUtil.getDateNow(date, AGILE_DATE_FORMAT);
   }

   public static boolean isAgileUser(AtsApi atsApi) {
      if (atsAgileUser == null) {
         atsAgileUser = atsApi.getUserService().getCurrentUser().getUserGroups().contains(CoreUserGroups.AgileUser);
      }
      return atsAgileUser;
   }

   public static boolean isEarnedValueUser(AtsApi atsApi) {
      if (atsEarnedValueUser == null) {
         atsEarnedValueUser =
            atsApi.getUserService().getCurrentUser().getUserGroups().contains(CoreUserGroups.EarnedValueUser);
      }
      return atsEarnedValueUser;
   }

}
