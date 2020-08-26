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

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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

   protected AtsUser user;
   protected AtsUser selectedUser;
   private Active active = Active.Active;
   private boolean useCurrentUser = true;

   public UserSearchItem(String name, AtsUser user) {
      super(name);
      this.user = user;
   }

   public UserSearchItem(String name, AtsUser user, KeyedImage oseeImage) {
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
      this.useCurrentUser = userSearchItem.useCurrentUser;
   }

   protected AtsUser getUser() {
      if (this.user == null && useCurrentUser) {
         this.user = AtsApiService.get().getUserService().getCurrentUser();
      }
      return this.user;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getUserSearchName());
   }

   public String getUserSearchName() {
      if (getUser() != null) {
         return getUser().getName();
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
      if (getUser() != null) {
         return searchIt(getUser());
      } else {
         return searchIt();
      }
   }

   protected Collection<Artifact> searchIt(AtsUser user) {
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
      if (getUser() != null) {
         return;
      }
      if (searchType == SearchType.ReSearch && selectedUser != null) {
         return;
      }
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select User", active);
      int result = ld.open();
      if (result == 0) {
         selectedUser = AtsApiService.get().getUserService().getUserById(ld.getSelection());
         return;
      }
      cancelled = true;
   }

   public void setSelectedUser(AtsUser selectedUser) {
      this.selectedUser = selectedUser;
   }

   public AtsUser getDefaultUser() {
      return getUser();
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

   public void setUseCurrentUser(boolean useCurrentUser) {
      this.useCurrentUser = useCurrentUser;
   }

}
