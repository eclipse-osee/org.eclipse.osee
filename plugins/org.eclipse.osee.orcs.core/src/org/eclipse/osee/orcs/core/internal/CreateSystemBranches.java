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
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.OseeInf;
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
   private static String EDIT_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String MERGE_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : false, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String PREVIEW_ALL_NO_ATTR_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String RECURSIVE_NO_ATTR_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : true, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String RECURSIVE_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : true, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"*\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";

   public CreateSystemBranches(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(COMMON);
   }

   public TransactionId create(UserToken superUser) {
      orcsApi.getKeyValueOps().putByKey(BASE, BASE.getName());

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

      ArtifactId globalPreferences = tx.createArtifact(oseeConfig, CoreArtifactTokens.GlobalPreferences);
      tx.setSoleAttributeValue(globalPreferences, CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);
      tx.setSoleAttributeValue(globalPreferences, CoreAttributeTypes.ProductLinePreferences, JSON_PL_PREFERENCES);

      tx.createArtifact(oseeConfig, CoreArtifactTokens.XViewerCustomization);

      ArtifactId documentTemplateFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.DocumentTemplates);
      createWordTemplates(tx, documentTemplateFolder);
      createDataRights(tx, documentTemplateFolder);
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

   private void createWordTemplates(TransactionBuilder tx, ArtifactId documentTemplateFolder) {
      ArtifactId templateArtWe =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplateWholeWord, "WordEditTemplate");

      tx.setSoleAttributeValue(templateArtWe, CoreAttributeTypes.RendererOptions, EDIT_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtWe, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/Word Edit Template.xml", getClass()));
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer SPECIALIZED_EDIT");
      tx.createAttribute(templateArtWe, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.TisRenderer SPECIALIZED_EDIT");

      ArtifactId templateArtMergeEdit =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplateWholeWord, "WordMergeTemplate");
      tx.setSoleAttributeValue(templateArtWe, CoreAttributeTypes.RendererOptions, MERGE_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtMergeEdit, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL.xml", getClass()));
      tx.createAttribute(templateArtMergeEdit, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.word MERGE_EDIT");
      tx.createAttribute(templateArtMergeEdit, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.word MERGE");
      tx.createAttribute(templateArtMergeEdit, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer MERGE");
      tx.createAttribute(templateArtMergeEdit, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer MERGE_EDIT");
      tx.createAttribute(templateArtMergeEdit, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer DIFF THREE_WAY_MERGE");

      ArtifactId templateArtPrev =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplateWholeWord, "PreviewAll");
      tx.setSoleAttributeValue(templateArtPrev, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL.xml", getClass()));
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_ARTIFACT");
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW");
      tx.createAttribute(templateArtPrev, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer DIFF");

      ArtifactId templateArtPrevNoAttr = tx.createArtifact(documentTemplateFolder,
         CoreArtifactTypes.RendererTemplateWholeWord, "PREVIEW_ALL_NO_ATTRIBUTES");
      tx.setSoleAttributeValue(templateArtPrevNoAttr, CoreAttributeTypes.RendererOptions,
         PREVIEW_ALL_NO_ATTR_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtPrevNoAttr, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL_NO_ATTRIBUTES.xml", getClass()));
      tx.createAttribute(templateArtPrevNoAttr, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_ALL_NO_ATTRIBUTES");
      tx.createAttribute(templateArtPrevNoAttr, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer DIFF_NO_ATTRIBUTES");

      // must match name used in client integration tests
      ArtifactId templateArtPar =
         tx.createArtifact(documentTemplateFolder, CoreArtifactTypes.RendererTemplateWholeWord, "PREVIEW_ALL_RECURSE");
      tx.setSoleAttributeValue(templateArtPar, CoreAttributeTypes.RendererOptions, RECURSIVE_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtPar, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL_RECURSE.xml", getClass()));
      tx.createAttribute(templateArtPar, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_WITH_RECURSE");

      ArtifactId templateArtParna = tx.createArtifact(documentTemplateFolder,
         CoreArtifactTypes.RendererTemplateWholeWord, "PREVIEW_ALL_RECURSE_NO_ATTRIBUTES");
      tx.setSoleAttributeValue(templateArtParna, CoreAttributeTypes.RendererOptions,
         RECURSIVE_NO_ATTR_RENDERER_OPTIONS);
      tx.setSoleAttributeValue(templateArtParna, CoreAttributeTypes.WholeWordContent,
         OseeInf.getResourceContents("templates/PREVIEW_ALL_RECURSE_NO_ATTRIBUTES.xml", getClass()));
      tx.createAttribute(templateArtParna, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer PREVIEW PREVIEW_WITH_RECURSE_NO_ATTRIBUTES");
      tx.createAttribute(templateArtParna, CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer PREVIEW_SERVER PREVIEW_WITH_RECURSE_NO_ATTRIBUTES");
   }

   private void createDataRights(TransactionBuilder tx, ArtifactId documentTemplateFolder) {
      ArtifactId dataRightsArt = tx.createArtifact(documentTemplateFolder, CoreArtifactTokens.DataRightsFooters);
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Unspecified.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("Default.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("GovernmentPurposeRights.xml", getClass()));
      tx.createAttribute(dataRightsArt, CoreAttributeTypes.GeneralStringData,
         OseeInf.getResourceContents("RestrictedRights.xml", getClass()));
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