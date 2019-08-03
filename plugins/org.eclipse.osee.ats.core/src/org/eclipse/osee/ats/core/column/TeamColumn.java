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
package org.eclipse.osee.ats.core.column;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TeamColumn extends AbstractServicesColumn {

   public TeamColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String result = "";
      if (atsObject instanceof IAtsTeamWorkflow) {
         result = ((IAtsTeamWorkflow) atsObject).getTeamDefinition().getName();
      } else if (atsObject instanceof IAtsWorkItem) {
         result = getColumnText(((IAtsWorkItem) atsObject).getParentTeamWorkflow());
      }
      if (!Strings.isValid(result) && atsObject instanceof IAtsPeerToPeerReview) {
         IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) atsObject;
         if (atsApi.getReviewService().isStandAloneReview(peerRev)) {
            List<IAtsTeamDefinition> teams = new ArrayList<>();
            for (IAtsActionableItem ai : peerRev.getActionableItems()) {
               if (ai.getTeamDefinitionInherited() != null) {
                  teams.add(ai.getTeamDefinition());
               }
            }
            if (!teams.isEmpty()) {
               result = Collections.toString(", ", teams);
            }
         }
      }
      return result;
   }

}
