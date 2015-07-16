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

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class InsertionActivityColumn {

   public static String getInsertionActivityStr(IAtsObject atsObject, IAtsServices services) throws OseeCoreException {
      return getInsertionActivityStr(atsObject, services, CountryColumn.getUtil());
   }

   public static String getInsertionActivityStr(IAtsObject atsObject, IAtsServices services, WorkPackageUtility util) throws OseeCoreException {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         Pair<IAtsInsertionActivity, Boolean> insertionActivity = util.getInsertionActivity(services, workItem);
         if (insertionActivity.getFirst() != null) {
            result = String.format("%s%s", insertionActivity.getFirst().getName(),
               (insertionActivity.getSecond() ? " (I)" : ""));
         }
      }
      return result;
   }

}
