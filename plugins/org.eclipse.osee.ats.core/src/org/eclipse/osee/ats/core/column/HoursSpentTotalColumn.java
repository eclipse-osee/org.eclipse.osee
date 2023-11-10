/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.util.AtsUtil;

/**
 * @author Donald G. Dunne
 */
public class HoursSpentTotalColumn extends AbstractDerivedFromColumn {

   public HoursSpentTotalColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.HoursSpentTotalColumn, atsApi);
   }

   @Override
   protected String getText(IAtsObject atsObject) throws Exception {
      return getHoursSpentTotalColumn(atsObject, atsApi);
   }

   public static String getHoursSpentTotalColumn(Object element, AtsApi atsApi) {
      if (element instanceof IAtsWorkItem) {
         return AtsUtil.doubleToI18nString(
            atsApi.getWorkItemMetricsService().getHoursSpentTotal((IAtsWorkItem) element));
      }
      return "";
   }

}
