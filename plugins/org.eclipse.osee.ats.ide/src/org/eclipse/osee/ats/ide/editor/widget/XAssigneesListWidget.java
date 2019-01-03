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
package org.eclipse.osee.ats.ide.editor.widget;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesListWidget extends AbstractXAssigneesListWidget {

   public static final String WIDGET_ID = XAssigneesListWidget.class.getSimpleName();
   List<IAtsUser> assignees = new LinkedList<>();
   IAtsTeamDefinition teamDef = null;

   public XAssigneesListWidget() {
      super("Assignees");
   }

   @Override
   public List<IAtsUser> getCurrentAssignees() {
      return assignees;
   }

   @Override
   public void handleModifySelection() {
      try {
         UserCheckTreeDialog uld = new UserCheckTreeDialog("Select Assigness", "Select to assign.",
            AtsClientService.get().getUserService().getUsers(Active.Active));
         if (teamDef != null) {
            uld.setTeamMembers(teamDef.getMembersAndLeads());
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
