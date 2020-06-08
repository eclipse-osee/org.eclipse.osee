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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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

   private XComboViewer roleCombo;
   private XHyperlabelMemberSelection usersLink;
   private PeerToPeerReviewArtifact reviewArt;
   private Button okButton;

   public NewRoleDialog() {
      super(Displays.getActiveShell(), "New Role", null, "Enter New Roles", MessageDialog.QUESTION,
         new String[] {"OK", "Cancel"}, 0);
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   //both usersLink and role is selected --> enable button
   private void updateButtons() {
      if (roleCombo.getSelected() != null && usersLink != null && (!usersLink.isEmpty())) {
         okButton.setEnabled(true);
      } else {
         okButton.setEnabled(false);
      }
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      Control customArea = super.createCustomArea(parent);
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

      //dropdown selection
      roleCombo = new XComboViewer("Select Role", SWT.NONE);
      Set<Object> roles = new HashSet<>();
      for (Enum<Role> e : Role.values()) {
         roles.add(e);
      }
      roleCombo.setInput(roles);
      roleCombo.createWidgets(comp, 2);

      //add event listeners for enabling OK button
      roleCombo.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            updateButtons();
         }

      });
      Collection<AtsUser> atsUsers = AtsClientService.get().getUserService().getUsers(Active.Active);
      List<User> users = new ArrayList<>();
      for (AtsUser aUser : atsUsers) {
         User user = UserManager.getUserByArtId(aUser);
         if (user != null) {
            users.add(user);
         }
      }
      users.remove(SystemUser.BootStrap);
      usersLink = new XHyperlabelMemberSelection("Select User(s)", users);
      usersLink.createWidgets(comp, 2);
      usersLink.addXModifiedListener(new XModifiedListener() {
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
         for (AtsUser aUser : AtsClientService.get().getTeamDefinitionService().getMembersAndLeads(teamDef)) {
            User user = UserManager.getUserByArtId(aUser);
            if (user != null) {
               users.add(user);
            }
         }
         usersLink.setTeamMembers(users);
      }

      return customArea;
   }

   public Role getRole() {
      Role role = null;
      try {
         role = (Role) roleCombo.getSelected();
      } catch (Exception ex) {
         // do nothing
      }
      return role;
   }

   public Collection<AtsUser> getUsers() {
      List<AtsUser> selected = new ArrayList<AtsUser>();
      for (User user : usersLink.getSelectedUsers()) {
         AtsUser aUser = AtsClientService.get().getUserService().getUserById(user);
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
