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
package org.eclipse.osee.ats.core.review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class UserRoleManager implements IAtsPeerReviewRoleManager {

   protected final static String ROLE_ITEM_TAG = "Role";
   private final Matcher roleMatcher =
      java.util.regex.Pattern.compile("<" + ROLE_ITEM_TAG + ">(.*?)</" + ROLE_ITEM_TAG + ">",
         Pattern.DOTALL | Pattern.MULTILINE).matcher("");
   private List<UserRole> roles;
   protected final IAtsUserService userService;
   protected final IAttributeResolver attrResolver;
   protected final IAtsPeerToPeerReview peerRev;
   protected final AtsApi atsApi;

   public UserRoleManager(IAtsPeerToPeerReview peerRev, AtsApi atsApi) {
      this.atsApi = atsApi;
      this.attrResolver = atsApi.getAttributeResolver();
      this.userService = atsApi.getUserService();
      this.peerRev = peerRev;
   }

   public void ensureLoaded() {
      if (roles == null) {
         roles = new ArrayList<>();
         if (attrResolver != null) {
            for (String xml : attrResolver.getAttributesToStringList(peerRev, AtsAttributeTypes.Role)) {
               roleMatcher.reset(xml);
               while (roleMatcher.find()) {
                  UserRole item = new UserRole(roleMatcher.group());
                  roles.add(item);
               }
            }
         }
      }
   }

   @Override
   public List<UserRole> getUserRoles() {
      ensureLoaded();
      return roles;
   }

   @Override
   public List<UserRole> getUserRoles(Role role) {
      List<UserRole> roles = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role) {
            roles.add(uRole);
         }
      }
      return roles;
   }

   @Override
   public List<IAtsUser> getRoleUsers(Role role) {
      List<IAtsUser> users = new ArrayList<>();
      for (UserRole uRole : getUserRoles()) {
         if (uRole.getRole() == role && !users.contains(userService.getUserById(uRole.getUserId()))) {
            users.add(userService.getUserById(uRole.getUserId()));
         }
      }
      return users;
   }

   @Override
   public List<IAtsUser> getRoleUsers(Collection<UserRole> roles) {
      List<IAtsUser> users = new ArrayList<>();
      for (UserRole uRole : roles) {
         if (!users.contains(userService.getUserById(uRole.getUserId()))) {
            users.add(userService.getUserById(uRole.getUserId()));
         }
      }
      return users;
   }

   @Override
   public void addOrUpdateUserRole(UserRole userRole) {
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
         if (!peerRev.getAssignees().contains(getUser(userRole, atsApi))) {
            peerRev.getStateMgr().addAssignee(getUser(userRole, atsApi));
         }
      }
   }

   @Override
   public void removeUserRole(UserRole userRole) {
      List<UserRole> roleItems = getUserRoles();
      roleItems.remove(userRole);
   }

   private List<UserRole> getStoredUserRoles() {
      // Add new ones: items in userRoles that are not in dbuserRoles
      List<UserRole> storedUserRoles = new ArrayList<>();
      for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(peerRev, AtsAttributeTypes.Role)) {
         UserRole storedRole = new UserRole((String) attr.getValue());
         storedUserRoles.add(storedRole);
      }
      return storedUserRoles;
   }

   @Override
   public void saveToArtifact(IAtsChangeSet changes) {
      try {
         List<UserRole> storedUserRoles = getStoredUserRoles();

         // Change existing ones
         for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(peerRev,
            AtsAttributeTypes.Role)) {
            UserRole storedRole = new UserRole((String) attr.getValue());
            for (UserRole pItem : getUserRoles()) {
               if (pItem.equals(storedRole)) {
                  changes.setAttribute(peerRev, attr.getId().intValue(), AXml.addTagData(ROLE_ITEM_TAG, pItem.toXml()));
               }
            }
         }
         List<UserRole> userRoles = getUserRoles();
         List<UserRole> updatedStoredUserRoles = getStoredUserRoles();
         // Remove deleted ones; items in dbuserRoles that are not in userRoles
         for (UserRole delUserRole : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(
            updatedStoredUserRoles, userRoles)) {
            for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(peerRev,
               AtsAttributeTypes.Role)) {
               UserRole storedRole = new UserRole((String) attr.getValue());
               if (storedRole.equals(delUserRole)) {
                  changes.deleteAttribute(peerRev, attr);
               }
            }
         }
         for (UserRole newRole : org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(userRoles,
            updatedStoredUserRoles)) {
            changes.addAttribute(peerRev, AtsAttributeTypes.Role, AXml.addTagData(ROLE_ITEM_TAG, newRole.toXml()));
         }
         rollupHoursSpentToReviewState(peerRev);
         validateUserRolesCompleted(storedUserRoles, userRoles, changes);
      } catch (OseeCoreException ex) {
         atsApi.getLogger().error(ex, "Can't create ats review role document");
      }
   }

   private void rollupHoursSpentToReviewState(IAtsPeerToPeerReview peerRev) {
      double hoursSpent = 0.0;
      for (UserRole role : getUserRoles()) {
         hoursSpent += role.getHoursSpent() == null ? 0 : role.getHoursSpent();
      }
      peerRev.getStateMgr().setMetrics(peerRev.getStateDefinition(), hoursSpent,
         peerRev.getStateMgr().getPercentComplete(peerRev.getStateMgr().getCurrentStateName()), true,
         atsApi.getUserService().getCurrentUser(), new Date());
   }

   private void validateUserRolesCompleted(List<UserRole> currentUserRoles, List<UserRole> newUserRoles, IAtsChangeSet changes) {
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
            changes.addWorkItemNotificationEvent(
               AtsNotificationEventFactory.getWorkItemNotificationEvent(atsApi.getUserService().getCurrentUser(),
                  (IAtsWorkItem) peerRev, AtsNotifyType.Peer_Reviewers_Completed));
         } catch (OseeCoreException ex) {
            atsApi.getLogger().error(ex, "Error adding ATS Notification Event");
         }
      }
   }

   public static IAtsUser getUser(UserRole item, AtsApi atsApi) {
      return atsApi.getUserService().getUserById(item.getUserId());
   }

}
