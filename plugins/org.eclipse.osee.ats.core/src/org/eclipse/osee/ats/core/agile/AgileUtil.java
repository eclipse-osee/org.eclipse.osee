/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
         atsAgileUser = atsApi.getUserGroupService().isUserMember(CoreUserGroups.AgileUser,
            atsApi.getUserService().getCurrentUser().getId());
      }
      return atsAgileUser;
   }

   public static boolean isEarnedValueUser(AtsApi atsApi) {
      if (atsEarnedValueUser == null) {
         atsEarnedValueUser = atsApi.getUserGroupService().isUserMember(CoreUserGroups.EarnedValueUser,
            atsApi.getUserService().getCurrentUser().getId());
      }
      return atsEarnedValueUser;
   }

}
