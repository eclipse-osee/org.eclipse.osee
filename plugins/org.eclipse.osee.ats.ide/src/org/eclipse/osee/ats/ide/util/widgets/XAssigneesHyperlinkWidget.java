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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   Collection<AtsUser> assignees = new HashSet<>();
   private final IAtsTeamDefinition teamDef;

   public XAssigneesHyperlinkWidget(IAtsTeamDefinition teamDef) {
      super("Assignees", true, 50);
      this.teamDef = teamDef;
   }

   @Override
   public String getCurrentValue() {
      Collection<String> assigneeNames = new HashSet<>();
      for (AtsUser singleAssignee : assignees) {
         assigneeNames.add(singleAssignee.getName());
      }
      return Collections.toString("; ", assigneeNames);
   }

   @Override
   public boolean handleSelection() {
      UserCheckTreeDialog uld = new UserCheckTreeDialog("Select Assigness", "Select to assign.\nDeSelect to un-assign.",
         AtsClientService.get().getUserService().getUsers(Active.Active));
      uld.setTeamMembers(AtsClientService.get().getTeamDefinitionService().getMembersAndLeads(teamDef));

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

}
