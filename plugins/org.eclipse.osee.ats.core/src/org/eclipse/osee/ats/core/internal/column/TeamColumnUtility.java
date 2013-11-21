/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.column.IAtsColumnUtility;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TeamColumnUtility implements IAtsColumnUtility {

   private final IAtsWorkItemService workItemService;
   private final IAtsReviewService reviewService;

   public TeamColumnUtility(IAtsWorkItemService workItemService, IAtsReviewService reviewService) {
      this.workItemService = workItemService;
      this.reviewService = reviewService;
   }

   @Override
   public String getColumnText(Object object) {
      String result = "";
      try {
         if (object instanceof IAtsTeamWorkflow) {
            result = workItemService.getTeamName((IAtsTeamWorkflow) object);
         } else if (object instanceof IAtsWorkItem) {
            result = getColumnText(((IAtsWorkItem) object).getParentTeamWorkflow());
         }
         if (!Strings.isValid(result) && (object instanceof IAtsAbstractReview)) {
            IAtsAbstractReview review = (IAtsAbstractReview) object;
            if (reviewService.isStandAloneReview(review)) {
               List<IAtsTeamDefinition> teams = new ArrayList<IAtsTeamDefinition>();
               for (IAtsActionableItem ai : review.getActionableItems()) {
                  if (ai.getTeamDefinitionInherited() != null) {
                     teams.add(ai.getTeamDefinition());
                  }
               }
               if (!teams.isEmpty()) {
                  result = Collections.toString(", ", teams);
               }
            }
         }
      } catch (OseeCoreException ex) {
         return AtsColumnUtilities.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   @Override
   public String getDescription() {
      return "Team that has been assigned to work this Action";
   }

}
