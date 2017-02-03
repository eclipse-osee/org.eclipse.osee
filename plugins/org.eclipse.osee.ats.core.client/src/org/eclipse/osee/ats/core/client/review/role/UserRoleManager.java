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
package org.eclipse.osee.ats.core.client.review.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.core.client.validator.ArtifactValueProvider;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public class UserRoleManager {

   private final static String ROLE_ITEM_TAG = "Role";
   private static final AttributeTypeToken ATS_ROLE_STORAGE_TYPE = AtsAttributeTypes.Role;

   private final Matcher roleMatcher =
      java.util.regex.Pattern.compile("<" + ROLE_ITEM_TAG + ">(.*?)</" + ROLE_ITEM_TAG + ">",
         Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private final IValueProvider valueProvider;
   private List<UserRole> roles;

   public UserRoleManager(Artifact artifact) {
      this(new ArtifactValueProvider(artifact, ATS_ROLE_STORAGE_TYPE));
   }

   public UserRoleManager(IValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

   public static String getHtml(PeerToPeerReviewArtifact peerArt) throws OseeCoreException {
      if (getUserRoles(peerArt).isEmpty()) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Roles"));
      sb.append(getTable(peerArt));
      return sb.toString();
   }

   public static List<UserRole> getUserRoles(Artifact artifact) throws OseeCoreException {
      return new UserRoleManager(artifact).getUserRoles();
   }

   public void ensureLoaded() throws OseeCoreException {
      if (roles == null) {
         roles = new ArrayList<>();
         for (String xml : valueProvider.getValues()) {
            roleMatcher.reset(xml);
            while (roleMatcher.find()) {
               UserRole item = new UserRole(roleMatcher.group());
               roles.add(item);
            }
         }
      }
   }

   public List<UserRole> getUserRoles() throws OseeCoreException {
      ensureLoaded();
      return roles;
   }

   public List<UserRole> getUserRoles(Role role) throws OseeCoreException {
      List<UserRole> roles = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role) {
            roles.add(uRole);
         }
      }
      return roles;
   }

   public List<IAtsUser> getRoleUsers(Role role) throws OseeCoreException {
      List<IAtsUser> users = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role && !users.contains(getUser(uRole))) {
            users.add(getUser(uRole));
         }
      }
      return users;
   }

   @SuppressWarnings("deprecation")
   private List<UserRole> getStoredUserRoles(Artifact artifact) throws OseeCoreException {
      // Add new ones: items in userRoles that are not in dbuserRoles
      List<UserRole> storedUserRoles = new ArrayList<>();
      for (Attribute<?> attr : artifact.getAttributes(ATS_ROLE_STORAGE_TYPE)) {
         UserRole storedRole = new UserRole((String) attr.getValue());
         storedUserRoles.add(storedRole);
      }
      return storedUserRoles;
   }

   public void saveToArtifact(IAtsChangeSet changes) throws OseeCoreException {
      if (valueProvider instanceof ArtifactValueProvider) {
         saveToArtifact(((ArtifactValueProvider) valueProvider).getArtifact(), changes);
      } else {
         throw new OseeArgumentException(
            "Can't saveToArtifact unless provider is ArtifactValueProvider, use saveToArtifact(Artifact artifact, SkynetTransaction transaction)");
      }
   }

   @SuppressWarnings("deprecation")
   public void saveToArtifact(Artifact artifact, IAtsChangeSet changes) {
      try {
         List<UserRole> storedUserRoles = getStoredUserRoles(artifact);

         // Change existing ones
         for (Attribute<?> attr : artifact.getAttributes(ATS_ROLE_STORAGE_TYPE)) {
            UserRole storedRole = new UserRole((String) attr.getValue());
            for (UserRole pItem : getUserRoles()) {
               if (pItem.equals(storedRole)) {
                  attr.setFromString(AXml.addTagData(ROLE_ITEM_TAG, pItem.toXml()));
               }
            }
         }
         List<UserRole> userRoles = getUserRoles();
         List<UserRole> updatedStoredUserRoles = getStoredUserRoles(artifact);
         // Remove deleted ones; items in dbuserRoles that are not in userRoles
         for (UserRole delUserRole : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
            updatedStoredUserRoles, userRoles)) {
            for (Attribute<?> attr : artifact.getAttributes(ATS_ROLE_STORAGE_TYPE)) {
               UserRole storedRole = new UserRole((String) attr.getValue());
               if (storedRole.equals(delUserRole)) {
                  attr.delete();
               }
            }
         }
         for (UserRole newRole : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(userRoles,
            updatedStoredUserRoles)) {
            artifact.addAttributeFromString(ATS_ROLE_STORAGE_TYPE, AXml.addTagData(ROLE_ITEM_TAG, newRole.toXml()));
         }
         rollupHoursSpentToReviewState(artifact);
         validateUserRolesCompleted(artifact, storedUserRoles, userRoles, changes);
         changes.add(artifact);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't create ats review role document", ex);
      }
   }

   private void validateUserRolesCompleted(Artifact artifact, List<UserRole> currentUserRoles, List<UserRole> newUserRoles, IAtsChangeSet changes) {
      //all reviewers are complete; send notification to author/moderator
      int numCurrentCompleted = 0, numNewCompleted = 0;
      for (UserRole role : newUserRoles) {
         if (role.getRole() == Role.Reviewer) {
            if (!role.isCompleted()) {
               return;
            } else {
               numNewCompleted++;
            }
         }
      }
      for (UserRole role : currentUserRoles) {
         if (role.getRole() == Role.Reviewer) {
            if (role.isCompleted()) {
               numCurrentCompleted++;
            }
         }
      }
      if (numNewCompleted != numCurrentCompleted) {
         try {
            changes.getNotifications().addWorkItemNotificationEvent(
               AtsNotificationEventFactory.getWorkItemNotificationEvent(
                  AtsClientService.get().getUserService().getCurrentUser(), (IAtsWorkItem) artifact,
                  AtsNotifyType.Peer_Reviewers_Completed));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
         }
      }
   }

   public void addOrUpdateUserRole(UserRole userRole, PeerToPeerReviewArtifact peerArt) throws OseeCoreException {
      List<UserRole> roleItems = getUserRoles();
      boolean found = false;
      for (UserRole uRole : roleItems) {
         if (userRole.equals(uRole)) {
            uRole.update(userRole);
            found = true;
         }
      }
      if (!found) {
         roleItems.add(userRole);
         if (!peerArt.getAssignees().contains(getUser(userRole))) {
            peerArt.getStateMgr().addAssignee(getUser(userRole));
         }
      }
   }

   public void removeUserRole(UserRole userRole) throws OseeCoreException {
      List<UserRole> roleItems = getUserRoles();
      roleItems.remove(userRole);
   }

   public static String getTable(PeerToPeerReviewArtifact peerArt) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Role</TH>" + "<TH>User</TH><TH>Hours</TH><TH>Major</TH><TH>Minor</TH><TH>Issues</TH>");
      for (UserRole item : getUserRoles(peerArt)) {
         IAtsUser user = getUser(item);
         String name = "";
         if (user != null) {
            name = user.getName();
            if (!Strings.isValid(name)) {
               name = "invalid name";
            }
         }
         builder.append("<TR>");
         builder.append("<TD>" + item.getRole().name() + "</TD>");
         builder.append("<TD>" + name + "</TD>");
         builder.append("<TD>" + AtsUtilCore.doubleToI18nString(item.getHoursSpent()) + "</TD>");

         ReviewDefectManager defectMgr = new ReviewDefectManager(peerArt);
         builder.append("<TD>" + defectMgr.getNumMajor(user) + "</TD>");
         builder.append("<TD>" + defectMgr.getNumMinor(user) + "</TD>");
         builder.append("<TD>" + defectMgr.getNumIssues(user) + "</TD>");
         builder.append("</TR>");
      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   private void rollupHoursSpentToReviewState(Artifact artifact) throws OseeCoreException {
      double hoursSpent = 0.0;
      for (UserRole role : getUserRoles()) {
         hoursSpent += role.getHoursSpent() == null ? 0 : role.getHoursSpent();
      }
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
      awa.getStateMgr().setMetrics(awa.getStateDefinition(), hoursSpent,
         awa.getStateMgr().getPercentComplete(awa.getCurrentStateName()), true,
         AtsClientService.get().getUserService().getCurrentUser(), new Date());
   }

   public static IAtsUser getUser(UserRole item) {
      return AtsClientService.get().getUserService().getUserById(item.getUserId());
   }
}
