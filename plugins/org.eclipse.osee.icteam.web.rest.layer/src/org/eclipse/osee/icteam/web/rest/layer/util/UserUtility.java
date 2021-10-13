/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.util;

import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ajay Chandrahasan Util class for UsersResource class
 */
public class UserUtility {

   /**
    * @param orcsApi to query the database
    * @param userId userID to get user information
    * @return Artifact data related to user by userID
    * @throws OseeCoreException
    */
   public static ArtifactReadable getUserById(final OrcsApi orcsApi, final String userId) throws OseeCoreException {

      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.UserId, userId,
         QueryOption.EXACT_MATCH_OPTIONS).getResults().getExactlyOne();
   }

   /**
    * @param orcsApi to query the database
    * @param guid of user
    * @return User Artifact by Guid ID
    * @throws OseeCoreException
    */
   public static ArtifactReadable getUserByGUID(final OrcsApi orcsApi, final String guid) throws OseeCoreException {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andGuid(
         guid).getResults().getExactlyOne();
   }

   /**
    * @param orcsApi to query the database
    * @return List of all avaialable Users artifacts
    * @throws OseeCoreException
    */
   public static ResultSet<ArtifactReadable> getUserArtifacts(final OrcsApi orcsApi) throws OseeCoreException {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).getResults();
   }

   /**
    * Gets user name by User ID
    *
    * @param userId
    * @return User Name by userID or returns user ID back if UserID is UnAssigned
    */
   public static String getUserNameByUserId(final String userId) {
      if (userId.equals("UnAssigned")) {
         return userId;
      }
      if (!("".equals(userId.trim()))) {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         try {
            ArtifactReadable user = UserUtility.getUserById(orcsApi, userId);
            String userName = getUserName(user);
            if (!userName.isEmpty()) {
               return userName;
            }
         } catch (ItemDoesNotExist e) {
            return userId;
         }
      }
      return userId;
   }

   /**
    * @param user Artifact data of user
    * @return User ID from User Artifact if exists else returns empty
    */
   public static String getUserId(final ArtifactReadable user) {
      List<Object> userIdObject = user.getAttributeValues(CoreAttributeTypes.UserId);
      if (!userIdObject.isEmpty()) {
         if (userIdObject.get(0) instanceof String) {
            return (String) userIdObject.get(0);
         }
      }
      return "";
   }

   /**
    * @param user user Artifact data of user filtered by user ID
    * @return User Name if exists else returns empty
    */
   public static String getUserName(final ArtifactReadable user) {
      String userName = user.getName();
      if (userName.contains("(")) {
         userName = userName.split("\\(")[0].trim();
      }
      if ((userName != null) && !userName.isEmpty()) {
         return userName;
      }
      return "";
   }
}
