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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   public static final Object WIDGET_ID = XAssigneesHyperlinkWidget.class.getSimpleName();
   Collection<AtsUser> assignees = new HashSet<>();
   private final IAtsTeamDefinition teamDef;

   public XAssigneesHyperlinkWidget(IAtsTeamDefinition teamDef) {
      super("Assignees", true, 50);
      this.teamDef = teamDef;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(assignees, "; ", Named::getName);
   }

   @Override
   public boolean handleSelection() {
      UserCheckTreeDialog uld = new UserCheckTreeDialog("Select Assigness", "Select to assign.\nDeSelect to un-assign.",
         AtsApiService.get().getUserService().getUsers(Active.Active));
      if (teamDef != null) {
         uld.setTeamMembers(AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(teamDef));
      }

      if (!assignees.isEmpty()) {
         uld.setInitialSelections(assignees);
      }
      if (uld.open() == Window.OK) {
         Collection<AtsUser> users = uld.getUsersSelected();
         assignees.clear();
         assignees.addAll(users);
         return true;
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      assignees.clear();
      return true;
   }

   public Collection<AtsUser> getSelected() {
      return assignees;
   }

   public Collection<AtsUser> getAssignees() {
      return assignees;
   }

}
