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
package org.eclipse.osee.ats.ide.util.widgets.role;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.User;
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
   private XHyperlabelMemberSelection users;
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

   //both users and role is selected --> enable button
   private void updateButtons() {
      if (roleCombo.getSelected() != null && (!users.isEmpty())) {
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
      Collection<User> oseeUsers = AtsClientService.get().getUserServiceClient().getOseeUsers(
         AtsClientService.get().getUserService().getUsers(Active.Active));
      oseeUsers.remove(SystemUser.BootStrap);
      users = new XHyperlabelMemberSelection("Select User(s)", oseeUsers);
      users.createWidgets(comp, 2);
      users.addXModifiedListener(new XModifiedListener() {
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
         users.setTeamMembers(AtsClientService.get().getUserServiceClient().getOseeUsers(
            AtsClientService.get().getTeamDefinitionService().getMembersAndLeads(teamDef)));
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

   public Collection<IAtsUser> getUsers() {
      return AtsClientService.get().getUserServiceClient().getAtsUsers(users.getSelectedUsers());
   }

   public void setReview(PeerToPeerReviewArtifact reviewArt) {
      this.reviewArt = reviewArt;
   }

}
