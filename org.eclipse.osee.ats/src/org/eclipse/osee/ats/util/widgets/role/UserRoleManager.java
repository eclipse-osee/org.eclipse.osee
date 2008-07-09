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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import org.eclipse.osee.ats.artifact.IReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class UserRoleManager {

   private final ReviewSMArtifact artifact;
   private boolean enabled = true;
   private static String ATS_DEFECT_TAG = "AtsRole";
   private static String DEFECT_ITEM_TAG = "Role";
   private static String REVIEW_DEFECT_ATTRIBUTE_NAME = "ats.Role";

   public UserRoleManager(ReviewSMArtifact artifact) {
      this.artifact = artifact;
   }

   public String getHtml() throws OseeCoreException, SQLException {
      if (getUserRoles().size() == 0) return "";
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Defects"));
      sb.append(getTable());
      return sb.toString();
   }

   public Set<UserRole> getUserRoles() throws OseeCoreException, SQLException {
      Set<UserRole> uRoles = new HashSet<UserRole>();
      String xml = artifact.getSoleAttributeValue(REVIEW_DEFECT_ATTRIBUTE_NAME, "");
      Matcher m =
            java.util.regex.Pattern.compile("<" + DEFECT_ITEM_TAG + ">(.*?)</" + DEFECT_ITEM_TAG + ">").matcher(xml);
      while (m.find()) {
         UserRole item = new UserRole(m.group());
         uRoles.add(item);
      }
      return uRoles;
   }

   public Set<UserRole> getUserRoles(Role role) throws OseeCoreException, SQLException {
      Set<UserRole> roles = new HashSet<UserRole>();
      for (UserRole uRole : getUserRoles())
         if (uRole.getRole() == role) roles.add(uRole);
      return roles;
   }

   public Set<User> getRoleUsers(Role role) throws OseeCoreException, SQLException {
      Set<User> users = new HashSet<User>();
      for (UserRole uRole : getUserRoles())
         if (uRole.getRole() == role) users.add(uRole.getUser());
      return users;
   }

   private void saveRoleItems(Set<UserRole> defectItems, boolean persist) {
      try {
         StringBuffer sb = new StringBuffer("<" + ATS_DEFECT_TAG + ">");
         for (UserRole item : defectItems)
            sb.append(AXml.addTagData(DEFECT_ITEM_TAG, item.toXml()));
         sb.append("</" + ATS_DEFECT_TAG + ">");
         artifact.setSoleAttributeValue(REVIEW_DEFECT_ATTRIBUTE_NAME, sb.toString());
         updateAssignees();
         if (persist) artifact.persistAttributes();
         rollupHoursSpentToReviewState(persist);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, "Can't create ats review defect document", ex, true);
      }
   }

   public void addOrUpdateUserRole(UserRole userRole, boolean persist) throws OseeCoreException, SQLException {
      Set<UserRole> roleItems = getUserRoles();
      boolean found = false;
      for (UserRole uRole : roleItems) {
         if (userRole.equals(uRole)) {
            uRole.update(userRole);
            found = true;
         }
      }
      if (!found) roleItems.add(userRole);
      saveRoleItems(roleItems, persist);
   }

   private void updateAssignees() throws OseeCoreException, SQLException {
      // Set assigness based on roles that are not set as completed
      Set<User> assignees = new HashSet<User>();
      for (UserRole uRole : getUserRoles()) {
         if (!uRole.isCompleted() && uRole.getUser() != null) assignees.add(uRole.getUser());
      }
      // If roles are all completed, then still need to select a user to assign to SMA
      if (assignees.size() == 0) {
         if (getUserRoles(Role.Author).size() > 0)
            for (UserRole role : getUserRoles(Role.Author))
               assignees.add(role.getUser());
         else if (getUserRoles(Role.Moderator).size() > 0)
            for (UserRole role : getUserRoles(Role.Moderator))
               assignees.add(role.getUser());
         else
            assignees.add(SkynetAuthentication.getUser());
      }
      // Set assigness based on roles
      artifact.getSmaMgr().getStateMgr().setAssignees(assignees);
   }

   public void removeUserRole(UserRole userRole, boolean persist) throws OseeCoreException, SQLException {
      Set<UserRole> roleItems = getUserRoles();
      roleItems.remove(userRole);
      saveRoleItems(roleItems, persist);
   }

   public void clearLog(boolean persist) {
      saveRoleItems(new HashSet<UserRole>(), persist);
   }

   public String getTable() throws OseeCoreException, SQLException {
      StringBuilder builder = new StringBuilder();
      builder.append("<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Role</TH>" + "<TH>User</TH><TH>Hours</TH><TH>Major</TH><TH>Minor</TH><TH>Issues</TH>");
      for (UserRole item : getUserRoles()) {
         User user = item.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (name == null || name.equals("")) {
               name = user.getName();
            }
         }
         builder.append("<TR>");
         builder.append("<TD>" + item.getRole().name() + "</TD>");
         builder.append("<TD>" + item.getUser().getDescriptiveName() + "</TD>");
         builder.append("<TD>" + item.getHoursSpentStr() + "</TD>");
         builder.append("<TD>" + getNumMajor(item.getUser()) + "</TD>");
         builder.append("<TD>" + getNumMinor(item.getUser()) + "</TD>");
         builder.append("<TD>" + getNumIssues(item.getUser()) + "</TD>");
         builder.append("</TR>");
      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   public int getNumMajor(User user) throws OseeCoreException, SQLException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) artifact).getDefectManager().getDefectItems())
         if (dItem.getSeverity() == Severity.Major && dItem.getUser() == user) x++;
      return x;
   }

   public int getNumMinor(User user) throws OseeCoreException, SQLException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) artifact).getDefectManager().getDefectItems())
         if (dItem.getSeverity() == Severity.Minor && dItem.getUser() == user) x++;
      return x;
   }

   public int getNumIssues(User user) throws OseeCoreException, SQLException {
      int x = 0;
      for (DefectItem dItem : ((IReviewArtifact) artifact).getDefectManager().getDefectItems())
         if (dItem.getSeverity() == Severity.Issue && dItem.getUser() == user) x++;
      return x;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void rollupHoursSpentToReviewState(boolean persist) throws OseeCoreException, SQLException {
      double hoursSpent = 0.0;
      for (UserRole role : getUserRoles())
         hoursSpent += role.getHoursSpent() == null ? 0 : role.getHoursSpent();
      SMAManager smaMgr = new SMAManager((StateMachineArtifact) artifact);
      smaMgr.getStateMgr().updateMetrics(hoursSpent, smaMgr.getStateMgr().getPercentComplete(), true);
      if (persist) artifact.persistAttributes();
   }

}