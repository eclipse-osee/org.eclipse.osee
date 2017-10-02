/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
@SuppressWarnings("deprecation")
public class UserCheckTreeDialog extends FilteredCheckboxTreeArtifactDialog {

   private Collection<User> teamMembers;
   private boolean includeAutoSelectButtons = false;

   public UserCheckTreeDialog(Collection<? extends User> users) {
      this("Select Users", "Select Users", users);
   }

   public UserCheckTreeDialog() {
      this("Select Users", "Select to assign.\nDeSelect to un-assign.", UserManager.getUsers());
   }

   public UserCheckTreeDialog(String title, String message, Collection<? extends User> users) {
      super(title, message, toArtifacts(users), new UserCheckTreeLabelProvider());
   }

   private static Collection<? extends Artifact> toArtifacts(Collection<? extends User> users) {
      return Collections.castAll(users);
   }

   public Collection<User> getUsersSelected() {
      Set<User> selected = new HashSet<>();
      for (FullyNamed art : getChecked()) {
         selected.add((User) art);
      }
      return selected;
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
               fUld.setInitialSelections(Arrays.asList(SystemUser.UnAssigned));
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
               fUld.setInitialSelections(Arrays.asList(UserManager.getUser()));
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
               List<ArtifactId> users = new LinkedList<>(fUld.getChecked());
               users.add(UserManager.getUser());
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
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            User user1 = (User) e1;
            User user2 = (User) e2;
            try {
               if (UserManager.getUser().equals(user1)) {
                  return -1;
               }
               if (UserManager.getUser().equals(user2)) {
                  return 1;
               }
               Collection<? extends Object> initialSel = getInitialSelections();
               if (initialSel != null) {
                  if (initialSel.contains(user1) && initialSel.contains(user2)) {
                     return getComparator().compare(user1.getName(), user2.getName());
                  }
                  if (initialSel.contains(user1)) {
                     return -1;
                  }
                  if (initialSel.contains(user2)) {
                     return 1;
                  }
               }
               if (teamMembers != null) {
                  if (teamMembers.contains(user1) && teamMembers.contains(user2)) {
                     return getComparator().compare(user1.getName(), user2.getName());
                  }
                  if (teamMembers.contains(user1)) {
                     return -1;
                  }
                  if (teamMembers.contains(user2)) {
                     return 1;
                  }
               }
               return getComparator().compare(user1.getName(), user2.getName());
            } catch (OseeCoreException ex) {
               return -1;
            }
         }
      });
      return c;
   }

   public Collection<User> getTeamMembers() {
      return teamMembers;
   }

   /**
    * If set, team members will be shown prior to rest of un-checked users
    */
   public void setTeamMembers(Collection<? extends User> teamMembers) {
      if (this.teamMembers == null) {
         this.teamMembers = new HashSet<>();
      }
      this.teamMembers.addAll(teamMembers);
   }

   public static class UserCheckTreeLabelProvider implements ILabelProvider {
      private Collection<? extends User> teamMembers;

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         if (teamMembers != null && teamMembers.contains(arg0)) {
            return ((Artifact) arg0).getName() + " (Team)";
         }
         return ((Artifact) arg0).getName();
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

      public void setTeamMembers(Collection<? extends User> teamMembers) {
         this.teamMembers = teamMembers;
      }

   }

   public void setIncludeAutoSelectButtons(boolean includeAutoSelectButtons) {
      this.includeAutoSelectButtons = includeAutoSelectButtons;
   }

}
