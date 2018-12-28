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
