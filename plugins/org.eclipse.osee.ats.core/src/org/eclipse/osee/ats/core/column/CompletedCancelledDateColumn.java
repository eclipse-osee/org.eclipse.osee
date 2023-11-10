/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class CompletedCancelledDateColumn extends AtsCoreCodeColumn {

   public CompletedCancelledDateColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.CompletedCancelledDateColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getCompletedCancelledDateStr(atsObject);
   }

   public static String getCompletedCancelledDateStr(Object object) {
      String result = "";
      Date date = getCompletedCancelledDate(object);
      if (date != null) {
         result = DateUtil.getMMDDYYHHMM(date);
      }
      return result;
   }

   public static Date getCompletedCancelledDate(Object object) {
      Date result = null;
      if (object instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) object;
         if (workItem.isCompleted()) {
            result = workItem.getCompletedDate();
         } else {
            result = workItem.getCancelledDate();
         }
      }
      return result;
   }

}
