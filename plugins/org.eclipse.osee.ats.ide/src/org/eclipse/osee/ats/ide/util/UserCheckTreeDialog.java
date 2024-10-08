/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.UserIdSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class UserCheckTreeDialog extends FilteredCheckboxTreeDialog<AtsUser> {

   private Collection<AtsUser> teamMembers;
   private boolean includeAutoSelectButtons = false;

   public UserCheckTreeDialog(Collection<AtsUser> users) {
      this("Select Users", "Select Users", users);
   }

   public UserCheckTreeDialog() {
      this("Select Users", "Select to assign.\nDeSelect to un-assign.",
         AtsApiService.get().getUserService().getUsers(Active.Active));
   }

   public UserCheckTreeDialog(String title, String message, Collection<AtsUser> users) {
      super(title, message, users, new ArrayTreeContentProvider(), new UserCheckTreeLabelProvider(), null);
   }

   public Collection<AtsUser> getUsersSelected() {
      return getChecked();
   }

   @Override
   protected void createPreCustomArea(Composite parent) {
      UserCheckTreeDialog fUld = this;
      if (includeAutoSelectButtons) {

         Composite comp = new Composite(parent, SWT.NONE);
         Button setUnAssigned = new Button(comp, SWT.PUSH);
         setUnAssigned.setText("Set as Un-Assigned and Close");
         setUnAssigned.setToolTipText("Set as Un-Assigned and close Dialog");
         setUnAssigned.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               getCheckboxTreeViewer().setSelection(new StructuredSelection(java.util.Collections.emptyList()));
               fUld.setInitialSelections(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER));
               okPressed();
            }
         });
         Button setAsMe = new Button(comp, SWT.PUSH);
         setAsMe.setText("Set as Me and Close");
         setAsMe.setToolTipText("Set as current user and close Dialog");
         setAsMe.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               getCheckboxTreeViewer().setSelection(new StructuredSelection(java.util.Collections.emptyList()));
               fUld.setInitialSelections(Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()));
               okPressed();
            }
         });
         Button addMe = new Button(comp, SWT.PUSH);
         addMe.setText("Add Me and Close");
         addMe.setToolTipText("Add Me to checked assignees and close Dialog");
         addMe.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               List<AtsUser> users = new LinkedList<>(fUld.getChecked());
               users.add(AtsApiService.get().getUserService().getCurrentUser());
               fUld.setInitialSelections(users);
               okPressed();
            }
         });
         comp.setLayout(ALayout.getZeroMarginLayout(3, false));
         comp.setLayoutData(new GridData());
      }
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);

      if (teamMembers != null) {
         ((UserCheckTreeLabelProvider) getTreeViewer().getViewer().getLabelProvider()).setTeamMembers(teamMembers);
      }
      getTreeViewer().setSorter(new UserIdSorter(getInitialSelections(), teamMembers));
      return c;
   }

   public Collection<AtsUser> getTeamMembers() {
      return teamMembers;
   }

   /**
    * If set, team members will be shown prior to rest of un-checked users
    */
   public void setTeamMembers(Collection<AtsUser> teamMembers) {
      if (this.teamMembers == null) {
         this.teamMembers = new HashSet<>();
      }
      this.teamMembers.addAll(teamMembers);
   }

   public static class UserCheckTreeLabelProvider implements ILabelProvider {
      private Collection<AtsUser> teamMembers;

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         if (teamMembers != null && teamMembers.contains(arg0)) {
            return ((AtsUser) arg0).getName() + " (Team)";
         }
         return ((AtsUser) arg0).getName();
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

      public void setTeamMembers(Collection<AtsUser> teamMembers) {
         this.teamMembers = teamMembers;
      }

   }

   public void setIncludeAutoSelectButtons(boolean includeAutoSelectButtons) {
      this.includeAutoSelectButtons = includeAutoSelectButtons;
   }

}
