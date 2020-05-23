/*********************************************************************
 * Copyright (c) 2017 Boeing
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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CancelledByColumn extends AbstractServicesColumn {

   public CancelledByColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         AtsUser user = getCancelledBy(atsObject, atsApi);
         if (user != null) {
            return user.getName();
         }
      }
      return null;
   }

   public static AtsUser getCancelledBy(Object obj, AtsApi atsApi) {
      String userId = null;
      if (obj instanceof IAtsWorkItem) {
         userId = atsApi.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj, AtsAttributeTypes.CancelledBy,
            null);
         if (Strings.isValid(userId)) {
            return atsApi.getUserService().getUserByUserId(userId);
         }
      }
      return null;
   }
}
