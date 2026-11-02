/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXAssigneesWidget;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class NewRoleDialog extends MessageDialog {
   private final WorkDefinition workDefinition;

   private XXStringsWidget roleWidget;
   private XXAssigneesWidget usersWidget;
   private PeerToPeerReviewArtifact reviewArt;
   private Button okButton;

   public NewRoleDialog(WorkDefinition workDefinition) {
      super(Displays.getActiveShell(), "New Role", null, "Enter New Roles", MessageDialog.QUESTION,
         new String[] {"OK", "Cancel"}, 0);
      this.workDefinition = workDefinition;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   // Both usersLink and role is selected --> enable button
   private void updateButtons() {
      if (okButton == null) {
         return;
      }
      if (roleWidget.getSelected() != null && usersWidget != null && (!usersWidget.isEmpty())) {
         okButton.setEnabled(true);
      } else {
         okButton.setEnabled(false);
      }
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      Control customArea = super.createCustomArea(parent);
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

      // Dropdown selection
      roleWidget = new XXStringsWidget("Select Role");
      List<String> roleNames = new ArrayList<>();
      for (ReviewRole role : workDefinition.getReviewRoles()) {
         roleNames.add(role.getName());
      }
      roleWidget.setSelectable(roleNames);
      roleWidget.createWidgets(comp, 1);

      //add event listeners for enabling OK button
      roleWidget.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            updateButtons();
         }

      });

      Collection<UserToken> users = OseeApiService.userSvc().getUsers();
      usersWidget = new XXAssigneesWidget("Select User(s)", users);
      usersWidget.createWidgets(comp, 1);
      usersWidget.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            updateButtons();
         }
      });
      IAtsTeamDefinition teamDef = null;
      if (reviewArt != null && reviewArt.getParentTeamWorkflow() != null) {
         teamDef = reviewArt.getParentTeamWorkflow().getTeamDefinition();
      }
      if (teamDef == null && !reviewArt.getActionableItems().isEmpty()) {
         for (IAtsActionableItem ai : reviewArt.getActionableItems()) {
            if (ai.getTeamDefinition() != null) {
               teamDef = ai.getTeamDefinition();
               break;
            }
         }
      }
      if (teamDef != null) {
         users = new ArrayList<>();
         for (AtsUser aUser : AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(teamDef)) {
            UserToken user = OseeApiService.userSvc().getUser(aUser);
            if (user != null) {
               users.add(user);
            }
         }
         usersWidget.setSelected(users, false);
      }

      return customArea;
   }

   public ReviewRole getRole() {
      String roleName = roleWidget.getSelectedFirst();
      return workDefinition.fromName(roleName);
   }

   public Collection<AtsUser> getUsers() {
      List<AtsUser> selected = new ArrayList<AtsUser>();
      for (UserToken user : usersWidget.getSelected()) {
         AtsUser aUser = AtsApiService.get().getUserService().getUserById(user);
         if (aUser != null) {
            selected.add(aUser);
         }
      }
      return selected;
   }

   public void setReview(PeerToPeerReviewArtifact reviewArt) {
      this.reviewArt = reviewArt;
   }
}