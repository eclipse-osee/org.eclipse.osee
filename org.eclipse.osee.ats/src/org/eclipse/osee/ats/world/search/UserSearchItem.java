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
package org.eclipse.osee.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public abstract class UserSearchItem extends WorldUISearchItem {

   protected final User user;
   protected User selectedUser;

   public UserSearchItem(String name, User user) {
      super(name);
      this.user = user;
   }

   public UserSearchItem(String name, User user, OseeImage oseeImage) {
      super(name, oseeImage);
      this.user = user;
   }

   public UserSearchItem(UserSearchItem userSearchItem) {
      this(userSearchItem, null);
   }

   public UserSearchItem(UserSearchItem userSearchItem, OseeImage oseeImage) {
      super(userSearchItem, oseeImage);
      this.user = userSearchItem.user;
      this.selectedUser = userSearchItem.selectedUser;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getUserSearchName());
   }

   public String getUserSearchName() {
      if (user != null)
         return user.getName();
      else if (selectedUser != null) return selectedUser.getName();
      return "";
   }

   public User getSearchUser() {
      if (user != null) return user;
      return selectedUser;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (isCancelled()) return EMPTY_SET;
      if (user != null)
         return searchIt(user);
      else
         return searchIt();
   }

   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {
      return EMPTY_SET;
   }

   private Collection<Artifact> searchIt() throws OseeCoreException {
      if (isCancelled()) return EMPTY_SET;
      if (selectedUser != null) return searchIt(selectedUser);
      return EMPTY_SET;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
      super.performUI(searchType);
      if (user != null) return;
      if (searchType == SearchType.ReSearch && selectedUser != null) return;
      UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell());
      int result = ld.open();
      if (result == 0) {
         selectedUser = ld.getSelection();
         return;
      }
      cancelled = true;
   }

   /**
    * @param selectedUser the selectedUser to set
    */
   public void setSelectedUser(User selectedUser) {
      this.selectedUser = selectedUser;
   }

   /**
    * @return the user
    */
   public User getDefaultUser() {
      return user;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof UserSearchItem) {
         UserSearchItem wsi = (UserSearchItem) obj;
         try {
            if (!getClass().equals(obj.getClass()) || !wsi.getName().equals(getName()) || wsi.getLoadView() != getLoadView()) {
               return false;
            }
         } catch (OseeCoreException ex) {
            return false;
         }
         if (getDefaultUser() != null && wsi.getDefaultUser() != null) {
            if (!wsi.getDefaultUser().equals(getDefaultUser())) return false;
         }
         return true;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      try {
         return getName().hashCode() + (getDefaultUser() != null ? getDefaultUser().hashCode() : 0) * 13;
      } catch (OseeCoreException ex) {
         return 0;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#copy()
    */
   @Override
   public WorldSearchItem copy() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#getImage()
    */
   @Override
   public Image getImage() {
      Image image = super.getImage();
      if (image != null) return image;
      return ImageManager.getImage(FrameworkImage.USER);
   }

}
