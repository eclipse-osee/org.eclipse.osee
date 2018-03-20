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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class UserListDialog extends FilteredTreeArtifactDialog {

   public UserListDialog(Shell parent, Active active) {
      this(parent, "Select User", active);
   }

   public UserListDialog(Shell parent, String title, Active active) {
      this(parent, title, getDefaultUsers(active));

   }

   private static Collection<User> getDefaultUsers(Active active) {
      List<User> users = null;
      if (active == Active.Both) {
         users = UserManager.getUsersAllSortedByName();
      } else if (active == Active.Active) {
         users = UserManager.getUsersSortedByName();
      } else {
         users = new ArrayList<>();
         for (User user : UserManager.getUsersAllSortedByName()) {
            if (!user.isActive()) {
               users.add(user);
            }
         }
      }
      return users;
   }

   public UserListDialog(Shell parent, String title, Collection<? extends Artifact> users) {
      super(title, title, users, new ArtifactContentProvider(null), new UserArtifactLabelProvider());
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public static class UserArtifactLabelProvider extends ArtifactLabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof User) {
            return ((User) element).getName();
         }
         return "Unknown Object";
      }
   }

   public User getSelection() {
      return (User) super.getSelectedFirst();
   }
}