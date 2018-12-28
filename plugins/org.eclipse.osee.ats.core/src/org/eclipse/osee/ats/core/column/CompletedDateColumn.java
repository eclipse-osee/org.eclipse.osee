/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class CompletedDateColumn extends AbstractServicesColumn {

   public CompletedDateColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getCompletedDateStr(atsObject);
   }

   public static String getCompletedDateStr(Object object) {
      String result = "";
      Date date = getCompletedDate(object);
      if (date != null) {
         result = DateUtil.getMMDDYYHHMM(date);
      }
      return result;
   }

   public static Date getCompletedDate(Object object) {
      Date result = null;
      if (object instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) object;
         if (workItem.isCompleted()) {
            result = workItem.getCompletedDate();
         }
      }
      return result;
   }

}
