/*********************************************************************
 * Copyright (c) 2019 Boeing
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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * @author Jeremy A. Midvidy
 */
public class CancelledReasonDetailsColumn extends AbstractServicesColumn {

   public CancelledReasonDetailsColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String ret = "";
      if (atsObject instanceof IAtsWorkItem) {
         ret = atsApi.getAttributeResolver().getSoleAttributeValueAsString(atsObject,
            AtsAttributeTypes.CancelledReasonDetails, "");
      }
      return ret;
   }
}
