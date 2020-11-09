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

import java.io.File;
import java.util.Arrays;
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
import org.eclipse.osee.ats.core.config.OrganizePrograms;
import org.eclipse.osee.define.rest.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.define.rest.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.define.rest.importing.parsers.NativeDocumentExtractor;
import org.eclipse.osee.define.rest.importing.parsers.WholeWordDocumentExtractor;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Base configuration for ATS
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigBase {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsDbConfigBase(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {
      OseeInfo.setValue(atsApi.getJdbcService().getClient(), "osee.work.def.as.name", "true");

      XResultData results = createAtsFolders();
      if (results.isErrors()) {
         return results;
      }

      // load top team / ai into cache
      IAtsTeamDefinition topTeam =
         atsApi.getTeamDefinitionService().getTeamDefinitionById(AtsArtifactToken.TopTeamDefinition);
      IAtsActionableItem topAi =
         atsApi.getActionableItemService().getActionableItemById(AtsArtifactToken.TopActionableItem);

      IAtsChangeSet changes = atsApi.createChangeSet("Set Top Team Work Definition");
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(topTeam, AtsWorkDefinitionTokens.WorkDef_Team_Default,
         changes);
      changes.setSoleAttributeValue(topAi, AtsAttributeTypes.Actionable, false);
      changes.execute();

      atsApi.clearCaches();

      createUserGroups(atsApi);

      getOrCreateAtsConfig(atsApi);

      createUserCreationDisabledConfig();

      configureDemoPeerChecklist();

      return results;
   }

   private void configureDemoPeerChecklist() {
      String CONFIG_VALUE = //
         "Requirements Review Checklist;/c/checklists/requirements_checklist.xlsx\n" + //
            "Code Review Checklist;/c/checklists/codeeck if_checklist.txt\n" + //
            "Test Review Checklist;/c/checklists/test_checklist.doc\n" + //
            "Document Review Checklist;osee:570:Document_Checklist\n" + //
            "Process Review Checklist;osee:570:Process_Checklist";
      atsApi.setConfigValue("DemoPeerReviewChecklist", CONFIG_VALUE);
      IAtsChangeSet changes = atsApi.createChangeSet("Import Peer Checklist");
      ArtifactToken atsHeader = atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder);
      changes.createArtifact(atsHeader, AtsArtifactToken.AttachmentFolder);
      changes.execute();

      File file = OseeInf.getResourceAsFile("demoPeerChecklists/Document_Checklist.xlsx", AtsDbConfigBase.class);
      TransactionBuilder transaction = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         SystemUser.OseeSystem, "Import Peer Checklist");
      importChecklist(file, transaction);
      transaction.commit();

      transaction = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, SystemUser.OseeSystem,
         "Import Peer Checklist");
      File file2 = OseeInf.getResourceAsFile("demoPeerChecklists/Process_Checklist.xlsx", AtsDbConfigBase.class);
      importChecklist(file2, transaction);
      transaction.commit();

   }

   private void importChecklist(File file, TransactionBuilder transaction) {
      IArtifactExtractor extractor = getArtifactExtractor(CoreArtifactTypes.GeneralDocument);
      XResultData resultData = new XResultData();
      RoughArtifactCollector collector = new RoughArtifactCollector(
         new RoughArtifact(orcsApi, resultData, CoreArtifactTypes.GeneralDocument, "Code_Checklist"));

      IArtifactImportResolver resolver =
         ArtifactResolverFactory.createResolver(transaction, ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
            CoreArtifactTypes.GeneralDocument, Arrays.asList(CoreAttributeTypes.Name), true, false);
      ArtifactReadable attachmentFolder = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         AtsArtifactToken.AttachmentFolder).asArtifact();
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
         changes.createArtifact(AtsArtifactToken.HeadingFolder, AtsArtifactToken.AtsConfig);
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
         AtsArtifactToken.HeadingFolder, changes);
      for (ArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem, AtsArtifactToken.TopTeamDefinition,
         AtsArtifactToken.WorkDefinitionsFolder)) {
         atsApi.getQueryService().getOrCreateArtifact(headingArt, token, changes);
      }

      changes.execute();

      (new OrganizePrograms(atsApi)).run();

      return new XResultData();
   }

}