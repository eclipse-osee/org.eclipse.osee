/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class CountryColumn {

   private static WorkPackageUtility util;

   public static WorkPackageUtility getUtil() {
      if (util == null) {
         util = new WorkPackageUtility();
      }
      return util;
   }

   public static String getCountryStr(IAtsObject atsObject, AtsApi atsApi) {
      return getCountryStr(atsObject, atsApi, getUtil());
   }

   public static String getCountryStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility util) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         Pair<IAtsCountry, Boolean> country = util.getCountry(atsApi, workItem);
         if (country.getFirst() != null) {
            result = String.format("%s%s", country.getFirst().getName(), country.getSecond() ? " (I)" : "");
         }
      }
      return result;
   }

}
