/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.ats.api.column.AtsCoreCodeColumnToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class UserArtIdColumn extends AtsCoreCodeColumn {

   private final AtsCoreCodeColumnToken colToken;

   public UserArtIdColumn(AtsCoreCodeColumnToken colToken, AtsApi atsApi) {
      super(colToken, atsApi);
      this.colToken = colToken;
   }

   @Override
   protected String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         AtsUser user = getUserArtIdName(colToken.getAttrType(), atsObject, atsApi);
         if (user != null) {
            return user.getName();
         }
      }
      return null;
   }

   public static AtsUser getUserArtIdName(AttributeTypeToken attrType, Object obj, AtsApi atsApi) {
      if (obj instanceof IAtsWorkItem) {
         if (attrType.isLong()) {
            Long userArtId = atsApi.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj, attrType, 0L);
            if (userArtId > 0) {
               return atsApi.getUserService().getUserById(ArtifactId.valueOf(userArtId));
            }
         } else if (attrType.isArtifactId()) {
            ArtifactId userArtId =
               atsApi.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj, attrType, ArtifactId.SENTINEL);
            if (userArtId.isValid()) {
               return atsApi.getUserService().getUserById(userArtId);
            }
         } else if (attrType.isString()) {
            String userArtId = atsApi.getAttributeResolver().getSoleAttributeValue((IAtsWorkItem) obj, attrType, null);
            if (Strings.isNumeric(userArtId)) {
               return atsApi.getUserService().getUserById(ArtifactId.valueOf(userArtId));
            }
         }
         return null;
      }
      return null;
   }
}
