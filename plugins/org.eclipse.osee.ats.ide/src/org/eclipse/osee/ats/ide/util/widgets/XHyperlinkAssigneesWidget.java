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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelCmdValueSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkAssigneesWidget extends XAbstractHyperlinkLabelCmdValueSelWidget {

   public static WidgetId ID = WidgetIdAts.XHyperlinkAssigneesWidget;
   Collection<AtsUser> assignees = new HashSet<>();
   private IAtsTeamDefinition teamDef;

   public XHyperlinkAssigneesWidget() {
      this(null);
   }

   public XHyperlinkAssigneesWidget(IAtsTeamDefinition teamDef) {
      super(ID, "Assignees", true, 50);
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

   public void setAssignees(Collection<AtsUser> assignees) {
      this.assignees.clear();
      this.assignees.addAll(assignees);
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

}
