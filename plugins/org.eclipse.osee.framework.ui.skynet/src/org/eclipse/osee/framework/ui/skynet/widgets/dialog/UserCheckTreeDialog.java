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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class UserCheckTreeDialog extends FilteredCheckboxTreeArtifactDialog {

   private Collection<User> teamMembers;

   public UserCheckTreeDialog(Collection<? extends User> users) {
      this("Select Users", "Select Users", users);
   }

   public UserCheckTreeDialog() throws OseeCoreException {
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

}
