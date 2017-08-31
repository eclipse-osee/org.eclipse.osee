/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColorColumn;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class ColorTeamColumn {

   private static final String WHITE_HEX = "#FFFFFF";
   private static final String BLACK_HEX = "#000000";
   public static final String ATS_COLOR_TEAM_COLUMN_ID = "ats.Color Team";

   public static Pair<String, Boolean> getWorkItemColorTeam(IAtsWorkItem workItem, IAtsServices atsServices) {
      Pair<String, Boolean> result = new Pair<>(null, false);
      IAttributeResolver attributeResolver = atsServices.getAttributeResolver();
      ArtifactId workPackageArt =
         attributeResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference, ArtifactId.SENTINEL);
      if (workPackageArt.isInvalid()) {
         if (!workItem.isTeamWorkflow()) {
            IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
            if (teamWf != null) {
               workPackageArt = attributeResolver.getSoleAttributeValue(teamWf, AtsAttributeTypes.WorkPackageReference,
                  ArtifactId.SENTINEL);
               result.setSecond(true);
            }
         }
      }
      if (workPackageArt.isValid()) {
         String colorTeam = attributeResolver.getSoleAttributeValue(workPackageArt, AtsAttributeTypes.ColorTeam, "");
         result.setFirst(colorTeam);
      }
      if (result.getFirst() == null) {
         result.setFirst("");
      }
      return result;
   }

   public static ColorColumn getColor() {
      ColorColumn color = new ColorColumn();
      color.setColumnId(ATS_COLOR_TEAM_COLUMN_ID);
      color.setColor("Blood Red Team", WHITE_HEX, "#CC0000");
      color.setColor("Blue Crew Team", WHITE_HEX, "#0000CC");
      color.setColor("Mean Green Team", WHITE_HEX, "#003300");
      color.setColor("Purple Team", WHITE_HEX, "#2E002E");
      color.setColor("Burnt Orange Team", BLACK_HEX, "#CC5200");
      color.setColor("Bronze Team", BLACK_HEX, "#754719");
      color.setColor("Silver Team", BLACK_HEX, "#828268");
      color.setColor("Pirate Black Team", WHITE_HEX, BLACK_HEX);
      color.setColor("Gold Team", BLACK_HEX, "#CC9900");
      color.setColor("Plaid Team", BLACK_HEX, "#33CCFF");
      return color;
   }

}
