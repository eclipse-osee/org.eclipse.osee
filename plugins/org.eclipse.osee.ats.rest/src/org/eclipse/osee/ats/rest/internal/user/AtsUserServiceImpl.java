/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.user;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.rest.internal.util.AtsUtilRest;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Non-artifact base user service
 * 
 * @author Donald G Dunne
 */
public class AtsUserServiceImpl implements IAtsUserService {

   private static OrcsApi orcsApi;

   public static void setOrcsApi(OrcsApi orcsApi) {
      AtsUserServiceImpl.orcsApi = orcsApi;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(orcsApi, "OrcsApi");
      System.out.println("ATS - AtsUserService started");
   }

   @Override
   public IAtsUser getCurrentUser() throws OseeCoreException {
      return getUserById(SystemUser.OseeSystem.getUserId());
   }

   @Override
   public IAtsUser getUserById(String userId) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (Strings.isValid(userId)) {
         ArtifactReadable userArt =
            orcsApi.getQueryFactory(AtsUtilRest.getApplicationContext()).fromBranch(CoreBranches.COMMON).andIsOfType(
               CoreArtifactTypes.User).and(CoreAttributeTypes.UserId,
               org.eclipse.osee.framework.core.enums.Operator.EQUAL, userId).getResults().getExactlyOne();
         if (userArt != null) {
            atsUser = new AtsUser(userArt);
         }
      }
      return atsUser;
   }

   @Override
   public Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (String userId : userIds) {
         IAtsUser user = getUserById(userId);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public IAtsUser getUserByName(String name) throws OseeCoreException {
      IAtsUser atsUser = null;
      if (Strings.isValid(name)) {
         ArtifactReadable userArt =
            orcsApi.getQueryFactory(AtsUtilRest.getApplicationContext()).fromBranch(CoreBranches.COMMON).andIsOfType(
               CoreArtifactTypes.User).and(CoreAttributeTypes.Name,
               org.eclipse.osee.framework.core.enums.Operator.EQUAL, name).getResults().getExactlyOne();
         if (userArt != null) {
            atsUser = new AtsUser(userArt);
         }
      }
      return atsUser;
   }

   @Override
   public boolean isUserIdValid(String userId) throws OseeCoreException {
      return getUserById(userId) != null;
   }

   @Override
   public boolean isUserNameValid(String name) throws OseeCoreException {
      return getUserByName(name) != null;
   }

   @Override
   public String getUserIdByName(String name) throws OseeCoreException {
      String userId = null;
      IAtsUser userByName = getUserByName(name);
      if (userByName != null) {
         userId = userByName.getUserId();
      }
      return userId;
   }

   @Override
   public boolean isAtsAdmin() {
      boolean admin = false;
      try {
         ArtifactReadable atsAdminGroup =
            orcsApi.getQueryFactory(AtsUtilRest.getApplicationContext()).fromBranch(CoreBranches.COMMON).andIsOfType(
               CoreArtifactTypes.UserGroup).and(CoreAttributeTypes.Name,
               org.eclipse.osee.framework.core.enums.Operator.EQUAL, "AtsAdmin").getResults().getExactlyOne();
         if (atsAdminGroup != null) {
            ArtifactReadable currentUserArt = getCurrentUserArt();
            for (ArtifactReadable adminArt : atsAdminGroup.getRelated(CoreRelationTypes.Users_User)) {
               if (adminArt.equals(currentUserArt)) {
                  admin = true;
                  break;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsUserServiceImpl.class, Level.SEVERE, ex);
      }
      return admin;
   }

   @Override
   public boolean isAssigneeMe(IAtsWorkItem workItem) throws OseeCoreException {
      return workItem.getStateMgr().getAssignees().contains(getCurrentUser());
   }

   public static ArtifactReadable getCurrentUserArt() throws OseeCoreException {
      // TODO Switch to real user
      return orcsApi.getQueryFactory(AtsUtilRest.getApplicationContext()).fromBranch(CoreBranches.COMMON).andIsOfType(
         CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, org.eclipse.osee.framework.core.enums.Operator.EQUAL,
         SystemUser.OseeSystem.getUserId()).getResults().getExactlyOne();
   }
}
