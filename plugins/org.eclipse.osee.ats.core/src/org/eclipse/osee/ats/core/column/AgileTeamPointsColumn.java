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

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Display Points as either "ats.Points" or "ats.Points Numeric" as configured on Agile Team artifact
 *
 * @author Donald G. Dunne
 */
public class AgileTeamPointsColumn extends AbstractServicesColumn {

   public AgileTeamPointsColumn(IAtsServices services) {
      super(services);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         ArtifactToken sprintArt =
            services.getRelationResolver().getRelatedOrNull(atsObject, AtsRelationTypes.AgileSprintToItem_Sprint);
         Conditions.assertNotNull(sprintArt, "Sprint not found for item %s", atsObject.toStringWithId());
         if (sprintArt != null) {
            ArtifactToken agileTeamArt =
               services.getRelationResolver().getRelatedOrNull(sprintArt, AtsRelationTypes.AgileTeamToSprint_AgileTeam);
            Conditions.assertNotNull(agileTeamArt, "Agile Team not found for Stpring %s", sprintArt.toStringWithId());
            AttributeTypeId pointsAttrType = AtsAttributeTypes.Points;
            String pointsAttrTypeName = services.getAttributeResolver().getSoleAttributeValue(agileTeamArt,
               AtsAttributeTypes.PointsAttributeType, "");
            if (Strings.isValid(pointsAttrTypeName)) {
               AttributeTypeId type = services.getStoreService().getAttributeType(pointsAttrTypeName);
               if (type.isValid()) {
                  pointsAttrType = type;
               }
            }
            result = services.getAttributeResolver().getSoleAttributeValue(atsObject, pointsAttrType, "");
         }
      }
      return result;
   }

}
