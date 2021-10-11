/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workflow.AtsAttachment;
import org.eclipse.osee.ats.api.workflow.AtsAttachments;
import org.eclipse.osee.ats.core.config.OrganizePrograms;
import org.eclipse.osee.define.rest.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.define.rest.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.define.rest.importing.parsers.NativeDocumentExtractor;
import org.eclipse.osee.define.rest.importing.parsers.WholeWordDocumentExtractor;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Base configuration for ATS
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigBase {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;
   private final OrcsBranch branchOps;

   public AtsDbConfigBase(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      branchOps = orcsApi.getBranchOps();
   }

   String getMultipleArtEntriesonCommon(AtsApi atsApi) {
      return "SELECT DISTINCT art1.art_id FROM osee_artifact art1, osee_artifact art2, \n" + //
         "osee_txs txs WHERE art1.ART_ID = art2.ART_ID AND \n" + //
         "art1.art_type_id = art2.art_type_id AND art1.GAMMA_ID <> art2.GAMMA_ID AND \n" + //
         "art2.GAMMA_ID = txs.GAMMA_ID and txs.BRANCH_ID = 570 ORDER BY art1.art_id";
   }

   public XResultData run() {
      OseeInfo.setValue(atsApi.getJdbcService().getClient(), "osee.work.def.as.name", "true");

      XResultData results = createAtsFolders();
      if (results.isErrors()) {
         return results;
      }

      // Load top team / ai into cache
      IAtsTeamDefinition topTeam =
         atsApi.getTeamDefinitionService().getTeamDefinitionById(AtsArtifactToken.TopTeamDefinition);
      IAtsActionableItem topAi =
         atsApi.getActionableItemService().getActionableItemById(AtsArtifactToken.TopActionableItem);

      IAtsChangeSet changes = atsApi.createChangeSet("Set Top Team Work Definition");
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(topTeam, AtsWorkDefinitionTokens.WorkDef_Team_Default,
         changes);
      changes.setSoleAttributeValue(topAi, AtsAttributeTypes.Actionable, false);
      changes.execute();

      changes.reset("Create ATS CM Artifact");
      ArtifactToken art = changes.createArtifact(AtsArtifactToken.AtsTopFolder, AtsArtifactToken.AtsCmBranch);
      changes.setSoleAttributeValue(art, CoreAttributeTypes.Description,
         "Used to denote Baseline branch as ATS CM branch");
      changes.execute();

      List<ArtifactId> artIds = atsApi.getQueryService().getArtifactIdsFromQuery(getMultipleArtEntriesonCommon(atsApi));

      Conditions.assertTrue(artIds.isEmpty(), "Duplicate artifact ids found [%s]", artIds);

      atsApi.clearCaches();

      createUserGroups(atsApi);

      getOrCreateAtsConfig(atsApi);

      createUserCreationDisabledConfig();

      createAndconfigureProcessesBranchAndDemoPeerChecklist();

      configureWalkthroughChecklist();

      createPeerReviewUserGroup();

      atsApi.getWorkDefinitionService().updateAllValidStateNames();

      return results;
   }

   private void createPeerReviewUserGroup() {

      // Create "Peer Review Created" User Group.
      IAtsChangeSet changes = atsApi.createChangeSet("Create Peer Review User Group", COMMON);
      ArtifactToken userGroupArt = atsApi.getQueryService().getArtifact(CoreArtifactTokens.UserGroups, COMMON);
      ArtifactToken artifact = changes.createArtifact(userGroupArt, AtsUserGroups.peerReviewCreationNotify);
      changes.addAttribute(artifact, CoreAttributeTypes.Email, "testemail@boeing.com");
      changes.execute();
   }

   private void createAndconfigureProcessesBranchAndDemoPeerChecklist() {
      AtsAttachments checklists = new AtsAttachments();
      checklists.addAttachment(new AtsAttachment("Document_Checklist", "osee", DemoBranches.Processes));
      checklists.addAttachment(new AtsAttachment("Process_Checklist", "osee", DemoBranches.Processes));
      String jsonToStore = atsApi.jaxRsApi().toJson(checklists);
      atsApi.setConfigValue("PeerReviewChecklist", jsonToStore);

      // Create Processes Branch
      Branch branch =
         branchOps.createBaselineBranch(DemoBranches.Processes, CoreBranches.SYSTEM_ROOT, ArtifactId.SENTINEL);
      branchOps.setBranchPermission(CoreUserGroups.Everyone, branch, PermissionEnum.READ);

      // Create Top Folder on Processes Branch
      IAtsChangeSet changes = atsApi.createChangeSet("Create PR Attachment Folder", branch);
      ArtifactToken branchRoot = atsApi.getQueryService().getArtifact(CoreArtifactTokens.DefaultHierarchyRoot, branch);
      changes.createArtifact(branchRoot, AtsArtifactToken.PeerAttachmentFolder);

      changes.execute();

      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(DemoBranches.Processes, "Import Peer Checklist");
      File file = OseeInf.getResourceAsFile("demoPeerChecklists/Document_Checklist.xlsx", AtsDbConfigBase.class);
      importChecklist(file, transaction, AtsArtifactToken.PeerAttachmentFolder);

      File file2 = OseeInf.getResourceAsFile("demoPeerChecklists/Process_Checklist.xlsx", AtsDbConfigBase.class);
      importChecklist(file2, transaction, AtsArtifactToken.PeerAttachmentFolder);
      transaction.commit();
   }

   private void configureWalkthroughChecklist() {
      AtsAttachments checklists = new AtsAttachments();
      checklists.addAttachment(new AtsAttachment("W_Document_Checklist", "osee", DemoBranches.Processes));
      checklists.addAttachment(new AtsAttachment("W_Process_Checklist", "osee", DemoBranches.Processes));
      String jsonToStore = atsApi.jaxRsApi().toJson(checklists);
      atsApi.setConfigValue("XAttachmentExampleWidget", jsonToStore);

      // Create Top Folder on Processes Branch
      IAtsChangeSet changes = atsApi.createChangeSet("Create WT Attachment Folder", DemoBranches.Processes);
      ArtifactToken branchRoot =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.DefaultHierarchyRoot, DemoBranches.Processes);
      changes.createArtifact(branchRoot, AtsArtifactToken.WalkthroughAttachmentFolder);

      changes.execute();

      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(DemoBranches.Processes, "Import Walkthrough Checklist");
      File file =
         OseeInf.getResourceAsFile("demoWalkthroughChecklists/W_Document_Checklist.xlsx", AtsDbConfigBase.class);
      importChecklist(file, transaction, AtsArtifactToken.WalkthroughAttachmentFolder);

      File file2 =
         OseeInf.getResourceAsFile("demoWalkthroughChecklists/W_Process_Checklist.xlsx", AtsDbConfigBase.class);
      importChecklist(file2, transaction, AtsArtifactToken.WalkthroughAttachmentFolder);
      transaction.commit();
   }

   private void importChecklist(File file, TransactionBuilder transaction, ArtifactToken token) {
      IArtifactExtractor extractor = getArtifactExtractor(CoreArtifactTypes.GeneralDocument);
      XResultData resultData = new XResultData();
      RoughArtifactCollector collector = new RoughArtifactCollector(
         new RoughArtifact(orcsApi, resultData, CoreArtifactTypes.GeneralDocument, "Code_Checklist"));

      IArtifactImportResolver resolver =
         ArtifactResolverFactory.createResolver(transaction, ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
            CoreArtifactTypes.GeneralDocument, Arrays.asList(CoreAttributeTypes.Name), true, false);
      ArtifactReadable attachmentFolder =
         orcsApi.getQueryFactory().fromBranch(DemoBranches.Processes).andId(token).asArtifact();
      SourceToRoughArtifactOperation sourceToRoughArtifactOperation =
         new SourceToRoughArtifactOperation(orcsApi, resultData, extractor, file, collector);
      sourceToRoughArtifactOperation.importFiles();
      RoughToRealArtifactOperation roughToRealArtifactOperation = new RoughToRealArtifactOperation(orcsApi, resultData,
         transaction, attachmentFolder, collector, resolver, false, extractor);
      roughToRealArtifactOperation.doWork();
   }

   private IArtifactExtractor getArtifactExtractor(ArtifactTypeToken artifactType) {
      IArtifactExtractor extractor = null;
      if (artifactType.inheritsFrom(CoreArtifactTypes.GeneralDocument)) {
         extractor = new NativeDocumentExtractor();
      } else {
         extractor = new WholeWordDocumentExtractor();
      }
      return extractor;
   }

   public static void createUserGroups(AtsApi atsApi) {
      if (atsApi.getQueryService().getArtifact(AtsUserGroups.AtsAdmin) == null) {
         IAtsChangeSet changes = atsApi.createChangeSet("Create Admin groups");

         ArtifactToken userGroup = atsApi.getQueryService().getArtifact(CoreArtifactTokens.UserGroups);

         changes.createArtifact(userGroup, AtsUserGroups.AtsAdmin);
         changes.createArtifact(userGroup, AtsUserGroups.AtsTempAdmin);
         changes.execute();
      }
   }

   public static ArtifactId getOrCreateAtsConfig(AtsApi atsApi) {
      ArtifactToken atsConfigArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      if (atsConfigArt == null) {
         IAtsChangeSet changes = atsApi.createChangeSet("Create AtsConfig");
         changes.createArtifact(AtsArtifactToken.AtsTopFolder, AtsArtifactToken.AtsConfig);
         changes.execute();
      }
      return atsConfigArt;
   }

   private void createUserCreationDisabledConfig() {
      atsApi.setConfigValue(AtsUtil.USER_CREATION_DISABLED,
         AtsArtifactTypes.Action.toStringWithId() + ";" + AtsArtifactTypes.TeamWorkflow.toStringWithId());
   }

   public XResultData createAtsFolders() {

      IAtsChangeSet changes = atsApi.createChangeSet("Create ATS Folders");

      ArtifactToken headingArt = atsApi.getQueryService().getOrCreateArtifact(CoreArtifactTokens.OseeConfiguration,
         AtsArtifactToken.AtsTopFolder, changes);
      for (ArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem,
         AtsArtifactToken.TopTeamDefinition)) {
         atsApi.getQueryService().getOrCreateArtifact(headingArt, token, changes);
      }

      changes.execute();

      XResultData results = (new OrganizePrograms(atsApi)).run();

      return results;
   }

}