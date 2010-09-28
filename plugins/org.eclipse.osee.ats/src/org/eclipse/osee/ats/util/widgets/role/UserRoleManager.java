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
package org.eclipse.osee.ats.util.widgets.role;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.IReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class UserRoleManager {

   private final WeakReference<AbstractReviewArtifact> artifactRef;
   private boolean enabled = true;
   private static String ROLE_ITEM_TAG = "Role";
   private static final IAttributeType ATS_ROLE_STORAGE_TYPE = AtsAttributeTypes.Role;

   private final Matcher roleMatcher = java.util.regex.Pattern.compile(
      "<" + ROLE_ITEM_TAG + ">(.*?)</" + ROLE_ITEM_TAG + ">", Pattern.DOTALL | Pattern.MULTILINE).matcher("");

   public UserRoleManager(AbstractReviewArtifact artifact) {
      this.artifactRef = new WeakReference<AbstractReviewArtifact>(artifact);
   }

   public String getHtml() throws OseeCoreException {
      if (getUserRoles().isEmpty()) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Defects"));
      sb.append(getTable());
      return sb.toString();
   }

   public AbstractReviewArtifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   public Set<UserRole> getUserRoles() throws OseeCoreException {
      Set<UserRole> roles = new HashSet<UserRole>();
      for (String xml : getArtifact().getAttributesToStringList(ATS_ROLE_STORAGE_TYPE)) {
         roleMatcher.reset(xml);
         while (roleMatcher.find()) {
            UserRole item = new UserRole(roleMatcher.group());
            roles.add(item);
         }
      }
      return roles;
   }

   public Set<UserRole> getRoleUsersReviewComplete() throws OseeCoreException {
      Set<UserRole> cRoles = new HashSet<UserRole>();
      for (UserRole role : getUserRoles(Role.Reviewer)) {
         if (role.isCompleted()) {
            cRoles.add(role);
         }
      }
      return cRoles;
   }

   public Set<User> getRoleUsersAuthorModerator() throws OseeCoreException {
      Set<User> roles = getRoleUsers(Role.Author);
      if (roles.isEmpty()) {
         roles = getRoleUsers(Role.Moderator);
         roles.add(UserManager.getUser());
      }

      return roles;
   }

   public Set<UserRole> getUserRoles(Role role) throws OseeCoreException {
      Set<UserRole> roles = new HashSet<UserRole>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role) {
            roles.add(uRole);
         }
      }
      return roles;
   }

   public Set<User> getRoleUsers(Role role) throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role) {
            users.add(uRole.getUser());
         }
      }
      return users;
   }

   private void saveRoleItems(Set<UserRole> defectItems, boolean persist, SkynetTransaction transaction) {
      try {
         // Change existing ones
         for (Attribute<?> attr : getArtifact().getAttributes(ATS_ROLE_STORAGE_TYPE)) {
            UserRole dbPromoteItem = new UserRole((String) attr.getValue());
            for (UserRole pItem : defectItems) {
               if (pItem.equals(dbPromoteItem)) {
                  attr.setFromString(AXml.addTagData(ROLE_ITEM_TAG, pItem.toXml()));
               }
            }
         }
         Set<UserRole> dbPromoteItems = getUserRoles();
         // Remove deleted ones; items in dbPromoteItems that are not in promoteItems
         for (UserRole delPromoteItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
            dbPromoteItems, defectItems)) {
            for (Attribute<?> attr : getArtifact().getAttributes(ATS_ROLE_STORAGE_TYPE)) {
               UserRole dbPromoteItem = new UserRole((String) attr.getValue());
               if (dbPromoteItem.equals(delPromoteItem)) {
                  attr.delete();
               }
            }
         }
         // Add new ones: items in promoteItems that are not in dbPromoteItems
         for (UserRole newPromoteItem : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(defectItems,
            dbPromoteItems)) {
            getArtifact().addAttributeFromString(ATS_ROLE_STORAGE_TYPE,
               AXml.addTagData(ROLE_ITEM_TAG, newPromoteItem.toXml()));
         }
         updateAssignees();
         if (persist) {
            getArtifact().persist(transaction);
         }
         rollupHoursSpentToReviewState(persist, transaction);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats review role document", ex);
      }
   }

   public void addOrUpdateUserRole(UserRole userRole, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      Set<UserRole> roleItems = getUserRoles();
      boolean found = false;
      for (UserRole uRole : roleItems) {
         if (userRole.equals(uRole)) {
            uRole.update(userRole);
            found = true;
         }
      }
      if (!found) {
         roleItems.add(userRole);
      }
      saveRoleItems(roleItems, persist, transaction);
   }

   private void updateAssignees() throws OseeCoreException {
      // Set assignees based on roles that are not set as completed
      Set<User> assignees = new HashSet<User>();
      for (UserRole uRole : getUserRoles()) {
         if (!uRole.isCompleted() && uRole.getUser() != null) {
            assignees.add(uRole.getUser());
         }
      }
      // If roles are all completed, then still need to select a user to assign to SMA
      if (assignees.isEmpty()) {
         if (getUserRoles(Role.Author).size() > 0) {
            for (UserRole role : getUserRoles(Role.Author)) {
               assignees.add(role.getUser());
            }
         } else if (getUserRoles(Role.Moderator).size() > 0) {
            for (UserRole role : getUserRoles(Role.Moderator)) {
               assignees.add(role.getUser());
            }
         } else {
            assignees.add(UserManager.getUser());
         }
      }
      // Set assignees based on roles
      getArtifact().getStateMgr().setAssignees(assignees);
   }

   public void removeUserRole(UserRole userRole, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      Set<UserRole> roleItems = getUserRoles();
      roleItems.remove(userRole);
      saveRoleItems(roleItems, persist, transaction);
   }

   public String getTable() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Role</TH>" + "<TH>User</TH><TH>Hours</TH><TH>Major</TH><TH>Minor</TH><TH>Issues</TH>");
      for (UserRole item : getUserRoles()) {
         User user = item.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (!Strings.isValid(name)) {
               name = user.getName();
            }
         }
         builder.append("<TR>");
         builder.append("<TD>" + item.getRole().name() + "</TD>");
         builder.append("<TD>" + item.getUser().getName() + "</TD>");
         builder.append("<TD>" + item.getHoursSpentStr() + "</TD>");
         builder.append("<TD>" + getNumMajor(item.getUser()) + "</TD>");
         builder.append("<TD>" + getNumMinor(item.getUser()) + "</TD>");
         builder.append("<TD>" + getNumIssues(item.getUser()) + "</TD>");
         builder.append("</TR>");
      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   public int getNumMajor(User user) throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) getArtifact()).getDefectManager().getDefectItems()) {
         if (dItem.getSeverity() == Severity.Major && dItem.getUser() == user) {
            x++;
         }
      }
      return x;
   }

   public int getNumMinor(User user) throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) getArtifact()).getDefectManager().getDefectItems()) {
         if (dItem.getSeverity() == Severity.Minor && dItem.getUser() == user) {
            x++;
         }
      }
      return x;
   }

   public int getNumIssues(User user) throws OseeCoreException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) getArtifact()).getDefectManager().getDefectItems()) {
         if (dItem.getSeverity() == Severity.Issue && dItem.getUser() == user) {
            x++;
         }
      }
      return x;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void rollupHoursSpentToReviewState(boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      double hoursSpent = 0.0;
      for (UserRole role : getUserRoles()) {
         hoursSpent += role.getHoursSpent() == null ? 0 : role.getHoursSpent();
      }
      AbstractWorkflowArtifact sma = getArtifact();
      sma.getStateMgr().setMetrics(hoursSpent, sma.getStateMgr().getPercentComplete(), true);
      if (persist) {
         getArtifact().persist(transaction);
      }
   }
}