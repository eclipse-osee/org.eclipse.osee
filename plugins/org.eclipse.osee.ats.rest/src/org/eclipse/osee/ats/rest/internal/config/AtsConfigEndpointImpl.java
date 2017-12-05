/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsConfigEndpoint;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpoint {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final Log logger;
   private final ExecutorAdmin executorAdmin;

   public AtsConfigEndpointImpl(AtsApi atsApi, OrcsApi orcsApi, Log logger, ExecutorAdmin executorAdmin) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.executorAdmin = executorAdmin;
   }

   @Override
   public String requestCacheReload() {
      executorAdmin.scheduleOnce("REST requested ATS configuration cache reload", atsApi::reloadConfigurationCache);
      return "ATS configuration cache reload request submitted.";
   }

   @Override
   public AtsConfigurations get() {
      return atsApi.getConfigurations();
   }

   @Override
   public List<ArtifactImage> getArtifactImages() {
      return AtsArtifactImages.getImages();
   }

   @Override
   public AtsConfigurations getFromDb() {
      return atsApi.reloadConfigurationCache();
   }

   @Override
   public ViewModel getNewSource() {
      return new ViewModel("templates/newConfigBranch.html");
   }

   @Override
   public AtsConfiguration createConfig(MultivaluedMap<String, String> form) {

      // get parameters
      BranchId fromBranchId = BranchId.valueOf(form.getFirst("fromBranchid"));
      String newBranchName = form.getFirst("newBranchName");
      Conditions.checkNotNullOrEmpty(newBranchName, "newBranchName");
      String userId = form.getFirst("userId");
      Conditions.checkNotNullOrEmpty(userId, "UserId");
      ArtifactId user = atsApi.getUserService().getUserById(userId).getStoreObject();
      if (user == null) {
         logger.error("User by id [%s] does not exist", userId);
      }
      org.eclipse.osee.orcs.data.BranchReadable fromBranch =
         orcsApi.getQueryFactory().branchQuery().andId(fromBranchId).getResults().getExactlyOne();

      // Create new baseline branch off Root
      Callable<BranchReadable> newBranchCallable =
         orcsApi.getBranchOps().createTopLevelBranch(IOseeBranch.create(newBranchName), user);
      BranchReadable newBranch;
      try {
         newBranch = newBranchCallable.call();
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR, "Error creating new branch");
      }

      // Introduce all ATS heading artifacts to new branch
      introduceAtsHeadingArtifacts(fromBranch, newBranch, user);

      // Create config artifact on Common
      AtsConfiguration config = createConfigArtifactOnCommon(newBranchName, user, newBranch);

      // Return new branch id
      return config;
   }

   private void introduceAtsHeadingArtifacts(org.eclipse.osee.orcs.data.BranchReadable fromBranch, BranchReadable newBranch, ArtifactId userArt) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(newBranch, userArt, "Add ATS Configuration");

      ArtifactId headingArt = introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.HeadingFolder, newBranch,
         CoreArtifactTokens.DefaultHierarchyRoot, null);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopActionableItem, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopTeamDefinition, newBranch, null, headingArt);
      ArtifactId configArt =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.ConfigFolder, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.ConfigsFolder, newBranch, null, configArt);
      ArtifactId workDefFolder =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDefinitionsFolder, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.Users, newBranch, null, configArt);

      // Introduce default work defs
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Goal, newBranch, null, workDefFolder);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Review_Decision, newBranch, null, workDefFolder);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Review_PeerToPeer, newBranch, null, workDefFolder);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Task_Default, newBranch, null, workDefFolder);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Team_Default, newBranch, null, workDefFolder);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_Team_Simple, newBranch, null, workDefFolder);

      tx.commit();
   }

   private ArtifactId introduceAndRelateTo(TransactionBuilder tx, org.eclipse.osee.orcs.data.BranchReadable fromBranch, ArtifactToken introToken, BranchReadable newBranch, ArtifactToken relateToToken, ArtifactId relateToArt) {
      ArtifactReadable introArt =
         orcsApi.getQueryFactory().fromBranch(fromBranch).andId(introToken).getResults().getAtMostOneOrNull();
      if (introArt == null) {
         introArt = orcsApi.getQueryFactory().fromBranch(fromBranch).andTypeEquals(
            introToken.getArtifactTypeId()).andNameEquals(introToken.getName()).getResults().getAtMostOneOrNull();
      }
      Conditions.checkNotNull(introArt, "No artifact found for token " + introToken);
      ArtifactId artifact = tx.introduceArtifact(fromBranch, introArt);
      if (relateToToken != null) {
         relateToArt =
            orcsApi.getQueryFactory().fromBranch(newBranch).andId(relateToToken).getResults().getAtMostOneOrNull();
         if (relateToArt == null) {
            relateToArt = orcsApi.getQueryFactory().fromBranch(newBranch).andTypeEquals(
               relateToToken.getArtifactTypeId()).andNameEquals(
                  relateToToken.getName()).getResults().getAtMostOneOrNull();
         }
      }
      tx.addChildren(relateToArt, artifact);
      return artifact;
   }

   private AtsConfiguration createConfigArtifactOnCommon(String branchName, ArtifactId userArt, BranchId branch) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt, "Add ATS Configuration");
      AtsConfiguration config = new AtsConfiguration();
      config.setName(branchName);
      config.setBranchId(branch.getId());
      config.setIsDefault(false);
      ArtifactId configArt = tx.createArtifact(AtsArtifactTypes.Configuration, branchName);
      config.setId(((ArtifactReadable) configArt).getId());
      tx.createAttribute(configArt, AtsAttributeTypes.AtsConfiguredBranch, branch.getIdString());
      XResultData rd = new XResultData();
      UpdateAtsConfiguration update = new UpdateAtsConfiguration((IAtsServer) atsApi);

      // Get or create Configs folder
      ArtifactId configsFolderArt = update.getOrCreateConfigsFolder(userArt, rd);
      if (rd.isErrors()) {
         throw new OseeStateException(rd.toString());
      }
      // Add configuration to configs folder
      tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Child, configArt);
      tx.commit();
      return config;
   }

   @Override
   public Response createUpdateConfig() {
      XResultData resultData = new XResultData(false);
      UpdateAtsConfiguration update = new UpdateAtsConfiguration((IAtsServer) atsApi);
      update.createUpdateConfig(resultData);
      if (resultData.isEmpty()) {
         resultData.log("Nothing to update");
      }
      return Response.ok(resultData.toString()).build();
   }

   @Override
   public Response storeWorkDef(JaxAtsWorkDef jaxWorkDef) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         atsApi.getArtifact(AtsCoreUsers.SYSTEM_USER), "Store Work Definition " + jaxWorkDef.getName());
      ArtifactReadable workDefArt = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.WorkDefinition).andNameEquals(jaxWorkDef.getName()).getResults().getAtMostOneOrNull();
      if (workDefArt == null) {
         workDefArt = (ArtifactReadable) tx.createArtifact(AtsArtifactTypes.WorkDefinition, jaxWorkDef.getName());
      }
      tx.setSoleAttributeValue(workDefArt, AtsAttributeTypes.DslSheet, jaxWorkDef.getWorkDefDsl());
      if (workDefArt.getParent() == null) {
         ArtifactReadable workDefFolder = (ArtifactReadable) atsApi.getArtifact(AtsArtifactToken.WorkDefinitionsFolder);
         tx.addChildren(workDefFolder, workDefArt);
      }
      tx.commit();
      ((IAtsServer) atsApi).getWorkDefinitionService().clearCaches();
      return Response.ok().build();
   }

   @Override
   public List<AtsAttributeValueColumn> generateAttrTypeViews() throws Exception {
      Map<String, AttributeTypeToken> idToToken = new HashMap<>();
      IAtsServer atsServer = (IAtsServer) atsApi;
      for (AttributeTypeToken attrType : atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getAll()) {
         idToToken.put(attrType.getName(), attrType);
      }

      List<String> sortedIds = new LinkedList<>();
      sortedIds.addAll(idToToken.keySet());
      Collections.sort(sortedIds);

      List<AtsAttributeValueColumn> columns = new LinkedList<>();
      for (String id : sortedIds) {
         AttributeTypeToken attrType = idToToken.get(id);
         ColumnAlign columnAlign = ColumnAlign.Left;
         SortDataType sortDataType = SortDataType.String;
         int width = 60;

         boolean isBoolean = atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isBooleanType(attrType);
         if (atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isEnumerated(attrType)) {
            width = 40;
         } else if (isBoolean) {
            width = 50;
         } else if (atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isIntegerType(attrType)) {
            width = 45;
            sortDataType = SortDataType.Integer;
            columnAlign = ColumnAlign.Center;
         } else if (atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isFloatingType(attrType)) {
            width = 40;
            sortDataType = SortDataType.Float;
            columnAlign = ColumnAlign.Center;
         } else if (atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isDateType(attrType)) {
            width = 80;
            sortDataType = SortDataType.Date;
         }

         AtsAttributeValueColumn valueColumn = new AtsAttributeValueColumn();
         valueColumn.setAttrTypeId(attrType.getId());
         valueColumn.setAttrTypeName(attrType.getName());
         valueColumn.setWidth(width);
         valueColumn.setAlign(columnAlign);
         valueColumn.setVisible(true);
         valueColumn.setSortDataType(sortDataType.name());
         valueColumn.setColumnMultiEdit(true);
         valueColumn.setDescription(attrType.getDescription());
         valueColumn.setNamespace("org.eclipse.osee.ats.WorldXViewer");
         String name = attrType.getName().replaceAll("^.*\\.", "");
         valueColumn.setName(name);
         valueColumn.setId(generateId(attrType.getName(), name));
         if (isBoolean) {
            valueColumn.setBooleanNotSetShow("");
            valueColumn.setBooleanOnFalseShow("false");
            valueColumn.setBooleanOnTrueShow("true");
         }
         columns.add(valueColumn);
      }
      return columns;
   }

   private String generateId(String id, String name) {
      System.err.println(String.format("\nid [%s] name [%s]", id, name));
      String cleanName = name.replaceAll("-", " ");
      cleanName = cleanName.replaceAll("\\(", " ");
      cleanName = cleanName.replaceAll("\\)", " ");
      cleanName = cleanName.replaceAll("/", " ");
      cleanName = cleanName.replaceAll(" +", " ");
      cleanName = cleanName.replaceAll("^ ", "");
      System.err.println(String.format("cleanName [%s]", cleanName));
      String replaceValue = Lib.toCamelCaseFromStringsWithSpaces(cleanName);
      System.err.println(String.format("replaceValue [%s]", replaceValue));
      String result = id.replaceFirst(Lib.escapeForRegex(name), replaceValue);
      System.err.println(String.format("result [%s]", result));
      return result;
   }

   @Override
   public XResultData alive() {
      XResultData results = new XResultData();
      results.logf("Alive");
      return results;
   }
}
