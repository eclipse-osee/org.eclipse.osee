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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
public class ImplementerColumn extends AtsCoreCodeColumn {

   public ImplementerColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.ImplementersColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      if (atsObject instanceof IAtsWorkItem) {
         return atsApi.getImplementerService().getImplementersStr((IAtsWorkItem) atsObject);
      }
      return "";
   }
}
