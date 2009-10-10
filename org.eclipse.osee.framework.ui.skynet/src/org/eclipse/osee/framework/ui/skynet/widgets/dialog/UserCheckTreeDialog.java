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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class UserCheckTreeDialog extends ArtifactCheckTreeDialog {

   private Collection<User> initialSel;

   /**
    * @param parent
    * @param artifacts
    */
   public UserCheckTreeDialog(Collection<User> artifacts) {
      super(artifacts);
   }

   public UserCheckTreeDialog() throws OseeCoreException {
      this(UserManager.getUsers());
   }

   public void setInitialSelections(Collection<User> initialSel) {
      this.initialSel = initialSel;
      ArrayList<Object> objs = new ArrayList<Object>();
      for (Artifact sel : initialSel) {
         objs.add(sel);
      }
      super.setInitialSelections(objs.toArray(new Object[objs.size()]));
   }

   public Collection<User> getUsersSelected() {
      Set<User> selected = new HashSet<User>();
      for (Artifact art : getSelection()) {
         selected.add((User) art);
      }
      return selected;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
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
               return getComparator().compare(user1.getName(), user2.getName());
            } catch (OseeCoreException ex) {
               return -1;
            }
         }
      });
      return c;
   }
}
