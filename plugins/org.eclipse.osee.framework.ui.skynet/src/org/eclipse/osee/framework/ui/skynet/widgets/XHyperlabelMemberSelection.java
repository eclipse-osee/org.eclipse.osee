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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.UserIdSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelMemberSelection extends XHyperlinkLabelCmdValueSelection {

   protected Set<OseeUser> selectedUsers = new HashSet<>();
   private final Collection<OseeUser> users;
   private final Collection<OseeUser> teamMembers = new HashSet<>();

   public XHyperlabelMemberSelection(String label) {
      this(label, OseeApiService.userSvc().getUsers());
   }

   public XHyperlabelMemberSelection(String label, Collection<OseeUser> users) {
      super(label, false, 80);
      this.users = users;
   }

   /**
    * If set, team members will be shown prior to rest of un-checked users
    */
   public void setTeamMembers(Collection<OseeUser> teamMembers) {
      this.teamMembers.addAll(teamMembers);
   }

   public Set<OseeUser> getSelectedUsers() {
      return selectedUsers;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString("; ", selectedUsers);
   }

   public void setSelectedUsers(Set<OseeUser> selectedUsers) {
      this.selectedUsers = selectedUsers;
      refresh();
   }

   @Override
   public Object getData() {
      return getSelectedUsers();
   }

   @Override
   public boolean handleSelection() {
      try {
         FilteredCheckboxTreeDialog<OseeUser> uld = new FilteredCheckboxTreeDialog<>("Select Users",
            "Select to assign.\nDeSelect to un-assign.", users, new ArrayTreeContentProvider(),
            new ArtifactLabelProvider(), new UserIdSorter(selectedUsers, teamMembers));
         uld.setInitialSelections(selectedUsers);
         if (uld.open() != 0) {
            return false;
         }
         selectedUsers.clear();
         selectedUsers.addAll(uld.getChecked());
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean isEmpty() {
      return selectedUsers.isEmpty();
   }

}
