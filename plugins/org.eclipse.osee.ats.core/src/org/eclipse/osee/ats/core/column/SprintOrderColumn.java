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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class SprintOrderColumn extends AbstractServicesColumn {

   public SprintOrderColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         ArtifactId sprintArt =
            atsApi.getRelationResolver().getRelatedOrNull(atsObject, AtsRelationTypes.AgileSprintToItem_Sprint);
         if (sprintArt != null) {
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
