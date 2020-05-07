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
import java.util.Objects;
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

   private static String RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT =
      "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer";

   private static String RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT =
      "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer";

   private static String RENDERER_IDENTIFIER_TIS = "org.eclipse.osee.framework.ui.skynet.render.TisRenderer";

   private static String RENDERER_IDENTIFIER_WORD = "org.eclipse.osee.framework.ui.skynet.word";

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

   /**
    * Defines a Publishing Template Match Criterion for the creation of Publishing Template.
    */

   private static class PublishingTemplateMatchCriterion {

      private final String rendererIdentifier;
      private final String matchString;

      /**
       * Creates a new {@link PublishingTemplateMatchCriterion} with the specified parameters.
       *
       * @param rendererIdentifier the identifier as returned by {@link IRenderer#getIdentifier} of the renderer.
       * @param matchCriteria the remainder of the match criteria string. The space between the
       * <code>rendererIdentifier</code> and the remainder of the match criteria string will be added by this class.
       */

      public PublishingTemplateMatchCriterion(String rendererIdentifier, String matchString) {
         this.rendererIdentifier = rendererIdentifier;
         this.matchString = matchString;
      }

      /**
       * Adds a {@link CoreAttributeTypes#TemplateMatchCritera} value to the publishing template artifact.
       *
       * @param tx the {@link TransactionBuilder} used to modify the artifact.
       * @param templateArtifact the identifier of the publishing template artifact.
       */

      public void setTemplateMatchCriteria(TransactionBuilder tx, ArtifactId templateArtifact) {
         tx.createAttribute(templateArtifact, CoreAttributeTypes.TemplateMatchCriteria,
            this.rendererIdentifier + " " + this.matchString);
      }

   }

   /**
    * Defines the parameters for the creation of a Publishing Template.
    */

   private static class PublishingTemplate {

      private final ArtifactToken parentArtifactToken;
      private final String name;
      private final String rendererOptionsJson;
      private final String templateContentFileName;
      private final List<PublishingTemplateMatchCriterion> matchCriteria;

      /**
       * Creates a new {@link PublishingTemplate} with the specified parameters.
       *
       * @param parentArtifactToken the hierarchical parent of the publishing template artifact.
       * @param name the name of the publishing template artifact.
       * @param rendererOptionsJson the publishing template renderer options as a JSON string.
       * @param templateContentFileName the filename of a file containing the Word ML publishing template content.
       * @param matchCriteria a list of {@link PublishingTemplateMatchCriterion} for the publishing template.
       */

      public PublishingTemplate(ArtifactToken parentArtifactToken, String name, String rendererOptionsJson, String templateContentFileName, List<PublishingTemplateMatchCriterion> matchCriteria) {
         this.parentArtifactToken = parentArtifactToken;
         this.name = name;
         this.rendererOptionsJson = rendererOptionsJson;
         this.templateContentFileName = templateContentFileName;
         this.matchCriteria = matchCriteria;
      }

      /**
       * Creates a new publishing template artifact.
       *
       * @param tx the {@link TransactionBuilder} used to create an modify the publishing template artifact.
       */

      public void createPublishingTemplate(TransactionBuilder tx) {

         var publishingTemplateArtifact =
            tx.createArtifact(this.parentArtifactToken, CoreArtifactTypes.RendererTemplateWholeWord, this.name);

         if (Objects.nonNull(this.rendererOptionsJson)) {
            tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.RendererOptions,
               this.rendererOptionsJson);
         }

         if (Objects.nonNull(this.templateContentFileName)) {
            tx.setSoleAttributeValue(publishingTemplateArtifact, CoreAttributeTypes.WholeWordContent,
               OseeInf.getResourceContents(this.templateContentFileName, getClass()));
         }

         if (Objects.nonNull(this.matchCriteria)) {
            this.matchCriteria.forEach(
               (matchCriterion) -> matchCriterion.setTemplateMatchCriteria(tx, publishingTemplateArtifact));
         }
      }
   }

   /**
    * Definitions for Publishing Templates to be created on the Common branch during initialization.
    */

   //@formatter:off
   private static List<PublishingTemplate> publishingTempaltes =
      List.of
         (
            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                              /* Parent Artifact Identifier */
                      "WordEditTemplate",                                                /* Name                       */
                      EDIT_RENDERER_OPTIONS,                                             /* Renderer Options JSON      */
                      "templates/Word Edit Template.xml",                                /* Template Content File Name */
                      List.of                                                            /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "SPECIALIZED_EDIT"                                 /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_TIS,                           /* Renderer Identifier */
                                      "SPECIALIZED_EDIT"                                 /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                              /* Parent Artifact Identifier */
                      "WordMergeTemplate",                                               /* Name                       */
                      MERGE_RENDERER_OPTIONS,                                            /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL.xml",                                       /* Template Content File Name */
                      List.of                                                            /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_WORD,                          /* Renderer Identifier */
                                      "MERGE_EDIT"                                       /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_WORD,                          /* Renderer Identifier */
                                      "MERGE"                                            /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "MERGE"                                            /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "MERGE_EDIT"                                       /* Match String        */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "DIFF THREE_WAY_MERGE"                             /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                              /* Parent Artifact Identifier */
                      "PreviewAll",                                                      /* Name                       */
                      null,                                                              /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL.xml",                                       /* Template Content File Name */
                      List.of                                                            /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "PREVIEW PREVIEW_ARTIFACT"                         /* Match String        */
                                   ),

                                   new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "PREVIEW"                                          /* Match String        */
                                   ),

                                   new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,      /* Renderer Identifier */
                                      "DIFF"                                             /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                               /* Parent Artifact Identifier */
                      "PREVIEW_ALL_NO_ATTRIBUTES",                                        /* Name                       */
                      PREVIEW_ALL_NO_ATTR_RENDERER_OPTIONS,                               /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL_NO_ATTRIBUTES.xml",                          /* Template Content File Name */
                      List.of                                                             /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,       /* Renderer Identifier */
                                      "PREVIEW PREVIEW_ALL_NO_ATTRIBUTES"                 /* Match String        */
                                   ),

                                   new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,       /* Renderer Identifier */
                                      "DIFF_NO_ATTRIBUTES"                                /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                               /* Parent Artifact Identifier */
                      "PREVIEW_ALL_RECURSE",                                              /* Name                       */
                      RECURSIVE_RENDERER_OPTIONS,                                         /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL_RECURSE.xml",                                /* Template Content File Name */
                      List.of                                                             /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,       /* Renderer Identifier */
                                      "PREVIEW PREVIEW_WITH_RECURSE"                      /* Match String        */
                                   )
                         )
                   ),

            new PublishingTemplate
                   (
                      CoreArtifactTokens.DocumentTemplates,                               /* Parent Artifact Identifier */
                      "PREVIEW_ALL_RECURSE_NO_ATTRIBUTES",                                /* Name                       */
                      RECURSIVE_NO_ATTR_RENDERER_OPTIONS,                                 /* Renderer Options JSON      */
                      "templates/PREVIEW_ALL_RECURSE_NO_ATTRIBUTES.xml",                  /* Template Content File Name */
                      List.of                                                             /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_CLIENT_SIDE_MS_WORD_EDIT,       /* Renderer Identifier */
                                      "PREVIEW PREVIEW_WITH_RECURSE_NO_ATTRIBUTES"        /* Match String        */
                                   ),

                                   new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_IDENTIFIER_SERVER_SIDE_MS_WORD_EDIT,       /* Renderer Identifier */
                                      "PREVIEW_SERVER PREVIEW_WITH_RECURSE_NO_ATTRIBUTES" /* Match String        */
                                   )
                         )
                   )
         );
   //@formatter:on

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

      ArtifactToken prefArt = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).getArtifactOrSentinal();
      if (prefArt.isInvalid()) {
         prefArt = tx.createArtifact(CoreArtifactTokens.GlobalPreferences);
      }
      tx.setSoleAttributeValue(prefArt, CoreAttributeTypes.GeneralStringData, JSON_ATTR_VALUE);
      tx.setSoleAttributeValue(prefArt, CoreAttributeTypes.ProductLinePreferences, JSON_PL_PREFERENCES);

      tx.createArtifact(oseeConfig, CoreArtifactTokens.XViewerCustomization);

      ArtifactId documentTemplateFolder = tx.createArtifact(oseeConfig, CoreArtifactTokens.DocumentTemplates);
      createWordTemplates(tx);
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

   private void createWordTemplates(TransactionBuilder tx) {
      CreateSystemBranches.publishingTempaltes.forEach(
         (publishingTemplate) -> publishingTemplate.createPublishingTemplate(tx));
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