/*******************************************************************************
 * Copyright (c) 2019 Boeing.
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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * @author Jeremy A. Midvidy
 */
public class CancelReasonColumn extends AbstractServicesColumn {

   public CancelReasonColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String ret = "";
      if (atsObject instanceof IAtsWorkItem) {
         ret =
            atsApi.getAttributeResolver().getSoleAttributeValueAsString(atsObject, AtsAttributeTypes.CancelReason, "");
      }
      return ret;
   }
}
