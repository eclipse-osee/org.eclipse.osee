/*********************************************************************
 * Copyright (c) 2015 Boeing
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
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class ProgramColumn {

   public static String getProgramStr(IAtsObject atsObject, AtsApi atsApi) {
      return getProgramStr(atsObject, atsApi, CountryColumn.getUtil());
   }

   public static String getProgramStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility util) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         Pair<IAtsProgram, Boolean> program = util.getProgram(atsApi, workItem);
         if (program.getFirst() != null) {
            result = String.format("%s%s", program.getFirst().getName(), program.getSecond() ? " (I)" : "");
         }
      }
      return result;
   }

}
