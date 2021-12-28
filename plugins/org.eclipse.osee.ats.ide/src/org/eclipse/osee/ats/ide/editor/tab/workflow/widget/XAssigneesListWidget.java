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

package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesListWidget extends AbstractXAssigneesListWidget {

   List<AtsUser> assignees = new LinkedList<>();
   IAtsTeamDefinition teamDef = null;

   public XAssigneesListWidget() {
      super("Assignees");
   }

   @Override
   public List<AtsUser> getCurrentAssignees() {
      return assignees;
   }

   @Override
   public void handleModifySelection() {
      try {
         UserCheckTreeDialog uld = new UserCheckTreeDialog("Select Assigness", "Select to assign.",
            AtsApiService.get().getUserService().getUsers(Active.Active));
         if (teamDef != null) {
            uld.setTeamMembers(AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(teamDef));
         }
         uld.setInitialSelections(assignees);

         if (uld.open() == Window.OK) {
            assignees.clear();
            assignees.addAll(uld.getUsersSelected());
            setInput(assignees);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void handleEmailSelection() {
      // do nothing
   }

   @Override
   public boolean includeEmailButton() {
      return false;
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

}
