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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public abstract class UserSearchItem extends WorldSearchItem {

   protected final User user;
   protected User selectedUser;

   public UserSearchItem(String name) {
      this(name, null);
   }

   public UserSearchItem(String name, User user) {
      super(name);
      this.user = user;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
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
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      if (isCancelled()) return EMPTY_SET;
      if (user != null)
         return searchIt(user);
      else
         return searchIt();
   }

   protected Collection<Artifact> searchIt(User user) throws SQLException, IllegalArgumentException {
      return EMPTY_SET;
   }

   private Collection<Artifact> searchIt() throws SQLException, IllegalArgumentException {
      if (isCancelled()) return EMPTY_SET;
      if (selectedUser != null) return searchIt(selectedUser);
      return EMPTY_SET;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (user != null) return;
      if (searchType == SearchType.ReSearch && selectedUser != null) return;
      UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell());
      int result = ld.open();
      if (result == 0) {
         selectedUser = (User) ld.getSelection();
         return;
      }
      cancelled = true;
   }
}
