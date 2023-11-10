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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;

/**
 * @author Donald G. Dunne
 */
public class CheckColumn extends AtsCoreCodeColumn {

   public CheckColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.CheckColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return atsObject.getIdString();
   }
}
