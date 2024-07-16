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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class SprintOrderColumn extends AtsCoreCodeColumn {

   public SprintOrderColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.SprintOrderColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         ArtifactId sprintArt = atsApi.getRelationResolver().getRelatedOrSentinel(atsObject,
            AtsRelationTypes.AgileSprintToItem_AgileSprint);
         if (sprintArt.isValid()) {
            Collection<ArtifactToken> items =
               atsApi.getRelationResolver().getRelatedArtifacts(sprintArt, AtsRelationTypes.AgileSprintToItem_AtsItem);
            int x = 1;
            ArtifactToken artifact = atsApi.getQueryService().getArtifact(atsObject);
            for (ArtifactId item : items) {
               if (item.equals(artifact)) {
                  result = String.valueOf(x);
                  break;
               }
               x++;
            }
         }
      }
      return result;
   }

}
