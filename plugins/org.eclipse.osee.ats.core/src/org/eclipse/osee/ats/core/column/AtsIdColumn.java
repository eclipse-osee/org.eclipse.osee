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

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Return current list of assignees sorted if in Working state or string of implementors surrounded by ()
 *
 * @author Donald G. Dunne
 */
public class AtsIdColumn implements IAtsColumn {

   public static AtsIdColumn instance = new AtsIdColumn();

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsWorkItem) {
            result = ((IAtsWorkItem) atsObject).getAtsId();
         } else if (atsObject instanceof IAtsAction) {
            result = ((IAtsAction) atsObject).getAtsId();
         } else {
            result = String.valueOf(atsObject.getUuid());
         }
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

}
