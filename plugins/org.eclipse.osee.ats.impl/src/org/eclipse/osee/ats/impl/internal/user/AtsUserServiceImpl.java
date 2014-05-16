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
package org.eclipse.osee.ats.impl.internal.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.internal.util.AtsUtilServer;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
         ResultSet<ArtifactReadable> results =
            orcsApi.getQueryFactory(AtsUtilServer.getApplicationContext()).fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
               CoreArtifactTypes.User).and(CoreAttributeTypes.UserId,
               org.eclipse.osee.framework.core.enums.Operator.EQUAL, userId).getResults();
         if (!results.isEmpty()) {
            ArtifactReadable userArt = results.getExactlyOne();
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
            orcsApi.getQueryFactory(AtsUtilServer.getApplicationContext()).fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
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
   public boolean isAtsAdmin(IAtsUser user) {
      boolean admin =
         orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(
            AtsArtifactToken.AtsAdmin.getGuid()).andRelatedTo(CoreRelationTypes.Users_User, getUserArt(user)).getCount() == 1;
      return admin;
   }

   private ArtifactReadable getUserArt(IAtsUser user) {
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(user.getGuid()).getResults().getExactlyOne();
   }

   @Override
   public boolean isAssigneeMe(IAtsWorkItem workItem) throws OseeCoreException {
      return workItem.getStateMgr().getAssignees().contains(getCurrentUser());
   }

   public static ArtifactReadable getCurrentUserArt() throws OseeCoreException {
      // TODO Switch to real user
      return orcsApi.getQueryFactory(AtsUtilServer.getApplicationContext()).fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
         CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, org.eclipse.osee.framework.core.enums.Operator.EQUAL,
         SystemUser.OseeSystem.getUserId()).getResults().getExactlyOne();
   }

   @Override
   public List<IAtsUser> getUsers(Active active) {
      List<IAtsUser> users = new ArrayList<IAtsUser>();
      for (ArtifactReadable userArt : orcsApi.getQueryFactory(AtsUtilServer.getApplicationContext()).fromBranch(
         AtsUtilCore.getAtsBranch()).andIsOfType(CoreArtifactTypes.User).getResults()) {
         Boolean activeFlag = userArt.getSoleAttributeValue(AtsAttributeTypes.Active, true);
         if (active == Active.Both || ((active == Active.Active) && activeFlag) || ((active == Active.InActive) && !activeFlag)) {
            users.add(new AtsUser(userArt));
         }
      }
      return users;
   }

   @Override
   public List<IAtsUser> getUsersSortedByName(Active active) {
      List<IAtsUser> users = getUsers(active);
      Collections.sort(users, new AtsUserNameComparator(false));
      return users;
   }
}
