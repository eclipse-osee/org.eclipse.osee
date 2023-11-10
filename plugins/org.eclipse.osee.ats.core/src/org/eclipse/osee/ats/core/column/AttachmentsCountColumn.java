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
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class AttachmentsCountColumn extends AtsCoreCodeColumn {

   public AttachmentsCountColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.AttachmentsCountColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         int count = atsApi.getRelationResolver().getRelatedCount((IAtsWorkItem) atsObject,
            CoreRelationTypes.SupportingInfo_SupportingInfo);
         if (count > 0) {
            return String.valueOf(count);
         }
      }
      return result;
   }
}
