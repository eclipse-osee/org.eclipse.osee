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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelection;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class NewRoleDialog extends MessageDialog {

   private XCombo roleCombo;
   private XHyperlabelMemberSelection users;
   private PeerToPeerReviewArtifact reviewArt;

   public NewRoleDialog() {
      super(Displays.getActiveShell(), "New Role", null, "Enter New Roles", MessageDialog.QUESTION,
         new String[] {"OK", "Cancel"}, 0);
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      Control customArea = super.createCustomArea(parent);

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

      roleCombo = new XCombo("Select Role");
      roleCombo.setDataStrings(Role.strValues().toArray(new String[Role.strValues().size()]));
      roleCombo.createWidgets(comp, 2);

      users =
         new XHyperlabelMemberSelection("Select User(s)", AtsClientService.get().getUserServiceClient().getOseeUsers(
            AtsClientService.get().getUserService().getUsers(Active.Active)));
      users.createWidgets(comp, 2);
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
         users.setTeamMembers(AtsClientService.get().getUserServiceClient().getOseeUsers(teamDef.getMembersAndLeads()));
      }

      return customArea;
   }

   public Role getRole() {
      Role role = null;
      try {
         role = Role.valueOf(roleCombo.get());
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
