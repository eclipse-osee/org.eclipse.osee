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
package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class UserSearchItem extends WorldUISearchItem {

   protected final IAtsUser user;
   protected IAtsUser selectedUser;
   private Active active = Active.Active;

   public UserSearchItem(String name, IAtsUser user) {
      super(name);
      this.user = user;
   }

   public UserSearchItem(String name, IAtsUser user, KeyedImage oseeImage) {
      super(name, oseeImage);
      this.user = user;
   }

   public UserSearchItem(UserSearchItem userSearchItem) {
      this(userSearchItem, null);
   }

   public UserSearchItem(UserSearchItem userSearchItem, KeyedImage oseeImage) {
      super(userSearchItem, oseeImage);
      this.user = userSearchItem.user;
      this.selectedUser = userSearchItem.selectedUser;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getUserSearchName());
   }

   public String getUserSearchName() {
      if (user != null) {
         return user.getName();
      } else if (selectedUser != null) {
         return selectedUser.getName();
      }
      return "";
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      if (isCancelled()) {
         return EMPTY_SET;
      }
      if (user != null) {
         return searchIt(user);
      } else {
         return searchIt();
      }
   }

   protected Collection<Artifact> searchIt(IAtsUser user) {
      return EMPTY_SET;
   }

   private Collection<Artifact> searchIt() {
      if (isCancelled()) {
         return EMPTY_SET;
      }
      if (selectedUser != null) {
         return searchIt(selectedUser);
      }
      return EMPTY_SET;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (user != null) {
         return;
      }
      if (searchType == SearchType.ReSearch && selectedUser != null) {
         return;
      }
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select User",
         AtsClientService.get().getUserServiceClient().getOseeUsersSorted(active));
      int result = ld.open();
      if (result == 0) {
         selectedUser = AtsClientService.get().getUserServiceClient().getUserFromOseeUser(ld.getSelection());
         return;
      }
      cancelled = true;
   }

   public void setSelectedUser(IAtsUser selectedUser) {
      this.selectedUser = selectedUser;
   }

   public IAtsUser getDefaultUser() {
      return user;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof UserSearchItem) {
         UserSearchItem wsi = (UserSearchItem) obj;
         try {
            if (!getClass().equals(obj.getClass()) || !wsi.getName().equals(
               getName()) || wsi.getLoadView() != getLoadView()) {
               return false;
            }
         } catch (OseeCoreException ex) {
            return false;
         }
         if (getDefaultUser() != null && wsi.getDefaultUser() != null && !wsi.getDefaultUser().equals(
            getDefaultUser())) {
            return false;
         }
         return true;
      }
      return false;
   }

   @Override
   public int hashCode() {
      try {
         return getName().hashCode() + (getDefaultUser() != null ? getDefaultUser().hashCode() : 0) * 13;
      } catch (OseeCoreException ex) {
         return 0;
      }
   }

   @Override
   public WorldSearchItem copy() {
      return null;
   }

   @Override
   public Image getImage() {
      Image image = super.getImage();
      if (image != null) {
         return image;
      }
      return ImageManager.getImage(FrameworkImage.USER);
   }

   public void setActive(Active active) {
      this.active = active;
   }

}
