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

package org.eclipse.osee.ats.ide.agile.jira;

import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class JiraAssigneeColumnUI extends AbstractJiraSyncColumnUI {

   public static JiraAssigneeColumnUI instance = new JiraAssigneeColumnUI();

   public static JiraAssigneeColumnUI getInstance() {
      return instance;
   }

   private JiraAssigneeColumnUI() {
      super(AtsColumnTokensDefault.JiraAssigneeColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public JiraAssigneeColumnUI copy() {
      JiraAssigneeColumnUI newXCol = new JiraAssigneeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      if (workItem.isTeamWorkflow()) {
         IAgileSprint sprint = AtsApiService.get().getAgileService().getSprint((IAtsTeamWorkflow) workItem);
         if (sprint != null) {
            AtsUser user = AtsApiService.get().getJiraService().getJiraAssignee(workItem);
            if (user != null) {
               return user.getName();
            } else {
               return "Not in JIRA";
            }
         }
      }
      return "";
   }

}
