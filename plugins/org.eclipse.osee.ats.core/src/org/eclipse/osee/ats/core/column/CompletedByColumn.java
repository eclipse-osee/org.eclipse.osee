/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CompletedByColumn extends AbstractServicesColumn {

   public CompletedByColumn(IAtsServices services) {
      super(services);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsUser user = getCompletedBy(atsObject, services);
         if (user != null) {
            return user.getName();
         }
      }
      return null;
   }

   public static IAtsUser getCompletedBy(Object obj, IAtsServices services) {
      String userId = null;
      if (obj instanceof IAtsWorkItem) {
         userId = services.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj,
            AtsAttributeTypes.CompletedBy, null);
         if (Strings.isValid(userId)) {
            return services.getUserService().getUserById(userId);
         }
      }
      return null;
   }
}
