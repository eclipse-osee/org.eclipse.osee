/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.core.internal.access.BootstrapUsers.getBoostrapUsers;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 */
public class CreateSystemBranches {

   private final OrcsApi orcsApi;
   private final TransactionFactory txFactory;
   private final QueryBuilder query;

   public CreateSystemBranches(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(COMMON);
   }

   public TransactionId create(UserToken superUser) {
      orcsApi.getKeyValueOps().putByKey(BASE, BASE.getName());

      SetupPublishing.setupConfiguration(orcsApi);

      populateSystemBranch();

      orcsApi.getBranchOps().createTopLevelBranch(COMMON);

      return populateCommonBranch(superUser);
   }

   private void populateSystemBranch() {
      TransactionBuilder tx = txFactory.createTransaction(CoreBranches.SYSTEM_ROOT, "Add System Root branch artifacts");
      tx.createArtifact(CoreArtifactTokens.DefaultHierarchyRoot);
      tx.createArtifact(CoreArtifactTokens.UniversalGroupRoot);
      tx.commit();
   }

   private TransactionId populateCommonBranch(UserToken superUser) {
      TransactionBuilder tx = txFactory.createTransaction(COMMON, "Add Common branch artifacts");

      orcsApi.tokenService().getArtifactTypeJoins().forEach(tx::addOrcsTypeJoin);
      orcsApi.tokenService().getAttributeTypeJoins().forEach(tx::addOrcsTypeJoin);
      orcsApi.tokenService().getRelationTypeJoins().forEach(tx::addOrcsTypeJoin);

      ArtifactReadable root = query.andIsHeirarchicalRootArtifact().getResults().getExactlyOne();

      ArtifactId oseeConfig = tx.createArtifact(root, CoreArtifactTokens.OseeConfiguration);

      ArtifactId userGroupsFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.UserGroups);
      ArtifactId everyOne = tx.createArtifact(userGroupsFolder, CoreUserGroups.Everyone);
      tx.setSoleAttributeValue(everyOne, CoreAttributeTypes.DefaultGroup, true);

      tx.createArtifact(userGroupsFolder, CoreUserGroups.OseeAdmin);
      tx.createArtifact(userGroupsFolder, CoreUserGroups.AccountAdmin);
      tx.createArtifact(userGroupsFolder, CoreUserGroups.OseeAccessAdmin);
      tx.createArtifact(userGroupsFolder, CoreUserGroups.Publishing);

      ArtifactToken prefArt = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).getArtifactOrSentinal();
      if (prefArt.isInvalid()) {
         prefArt = tx.createArtifact(CoreArtifactTokens.GlobalPreferences);
      }
      tx.setSoleAttributeValue(prefArt, CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);
      tx.setSoleAttributeValue(prefArt, CoreAttributeTypes.ProductLinePreferences, JSON_PL_PREFERENCES);

      tx.createArtifact(oseeConfig, CoreArtifactTokens.XViewerCustomization);

      tx.createArtifact(oseeConfig, CoreArtifactTokens.DocumentTemplates);
      SetupPublishing.setup(tx);
      tx.commit();

      List<IUserGroupArtifactToken> roles = superUser.getRoles();
      if (!roles.contains(CoreUserGroups.AccountAdmin)) {
         roles.add(CoreUserGroups.AccountAdmin);
      }
      if (!roles.contains(CoreUserGroups.OseeAdmin)) {
         roles.add(CoreUserGroups.OseeAdmin);
      }
      if (!roles.contains(CoreUserGroups.OseeAccessAdmin)) {
         roles.add(CoreUserGroups.OseeAccessAdmin);
      }
      UserToken userWithRoles = UserToken.create(superUser.getId(), superUser.getName(), superUser.getEmail(),
         superUser.getUserId(), true, superUser.getLoginIds(), roles);

      UserService userService = orcsApi.userService();
      userService.clearCaches();
      Set<UserToken> users = new HashSet<>(SystemUser.values());
      users.remove(userWithRoles); // Replace existing entry, if any
      Set<UserToken> bootsrapUsers = getBoostrapUsers();
      Conditions.assertFalse(bootsrapUsers.isEmpty(), "Bootstrap Users should NOT be empty.");
      users.addAll(bootsrapUsers);
      // Use token if possible, else add user passed in
      if (!users.contains(userWithRoles)) {
         users.add(userWithRoles);
      }
      OseeProperties.setIsInTest(true);
      userService.setUserForCurrentThread(UserId.valueOf(userWithRoles));
      TransactionId txId = userService.createUsers(users, "Create System Users");
      OseeProperties.setIsInTest(false);
      return txId;
   }

   private static final String JSON_ATTR_VALUE = "{ \"WCAFE\" : [" + //
      "{\"TypeId\" : 204509162766372, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 1, \"Max\" : 99}, {\"Min\" : 1001, \"Max\" : 1009}]}," + //
      "{\"TypeId\" : 204509162766372, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 1, \"Max\" : 49}]}," + //
      "{\"TypeId\" : 204509162766372, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1, \"Max\" : 99}, {\"Min\" : 1001, \"Max\" : 1009}]}," + //
      "{\"TypeId\" : 204509162766373, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 100, \"Max\" : 199}, {\"Min\" : 1100, \"Max\" : 1199}]}," + //
      "{\"TypeId\" : 204509162766373, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 50, \"Max\" : 199}]}," + //
      "{\"TypeId\" : 204509162766373, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 100, \"Max\" : 199}, {\"Min\" : 1100, \"Max\" : 1199}]}," + //
      "{\"TypeId\" : 204509162766374, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
      "{\"TypeId\" : 204509162766374, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
      "{\"TypeId\" : 204509162766374, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 200, \"Max\" : 1000}, {\"Min\" : 1200, \"Max\" : 2000}]}," + //
      "{\"TypeId\" : 204509162766370, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
      "{\"TypeId\" : 204509162766370, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
      "{\"TypeId\" : 204509162766370, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1, \"Max\" : 8191}]}," + //
      "{\"TypeId\" : 204509162766371, \"BranchId\" : 1, \"Range\" : [{\"Min\" : 400}]}," + //
      "{\"TypeId\" : 204509162766371, \"BranchId\" : 61, \"Range\" : [{\"Min\" : 400}]}," + //
      "{\"TypeId\" : 204509162766371, \"BranchId\" : 714, \"Range\" : [{\"Min\" : 1}]}]}";

   private static final String JSON_PL_PREFERENCES = "{ \"FileExtensionCommentStyle\" : [\n" + //
      "      { \"FileExtension\" : \"fileApplicability\", \"CommentPrefixRegex\" : \"\", \"CommentSuffixRegex\" : \"\", \"CommentPrefix\" : \"\", \"CommentSuffix\" : \"\"},\n" + //
      "      { \"FileExtension\" : \"txt\", \"CommentPrefixRegex\" : \"\", \"CommentSuffixRegex\" : \"\", \"CommentPrefix\" : \"\", \"CommentSuffix\" : \"\"},\n" + //
      "      { \"FileExtension\" : \"VMF\", \"CommentPrefixRegex\" : \"%\", \"CommentPrefix\" : \"% \" },\n" + //
      "      { \"FileExtension\" : \"mdgsource\", \"CommentPrefixRegex\" : \"\\\\+\\\\.\", \"CommentPrefix\" : \"+. \" },\n" + //
      "      { \"FileExtension\" : \"java\", \"CommentPrefixRegex\" : \"/\\\\*\", \"CommentSuffixRegex\" : \"\\\\*/\", \"CommentPrefix\" : \"/* \", \"CommentSuffix\" : \" */\"},\n" + //
      "      { \"FileExtension\" : \"cpp\", \"CommentPrefixRegex\" : \"//\", \"CommentPrefix\" : \"// \" },\n" + //
      "      { \"FileExtension\" : \"cmd\", \"CommentPrefixRegex\" : \"REM\", \"CommentPrefix\" : \"REM \" },\n" + //
      "      { \"FileExtension\" : \"xml\", \"CommentPrefixRegex\" : \"<!--\", \"CommentSuffixRegex\" : \"-->\", \"CommentPrefix\" : \"<!-- \", \"CommentSuffix\" : \" -->\"},\n" + //
      "      { \"FileExtension\" : \"lst\", \"CommentPrefixRegex\" : \"#\", \"CommentPrefix\" : \"# \" }\n" + //
      "      ]}";
}