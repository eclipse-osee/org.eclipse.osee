/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.rest.internal.config.operation.AtsConfigOperations;
import org.eclipse.osee.ats.rest.internal.config.operation.ConvertAtsAisAndTeamDefsOperation;
import org.eclipse.osee.ats.rest.internal.demo.AtsDbConfigDemoOp;
import org.eclipse.osee.ats.rest.internal.util.health.AtsHealthCheckOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.table.ExampleTableData;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpointApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final ExecutorAdmin executorAdmin;
   private List<ArtifactImage> images;

   public AtsConfigEndpointImpl(AtsApi atsApi, OrcsApi orcsApi, ExecutorAdmin executorAdmin) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.executorAdmin = executorAdmin;
   }

   @Override
   public String requestCacheReload() {
      executorAdmin.submit("REST requested ATS configuration cache reload", getConfigService);
      return "ATS configuration cache reload request submitted.";
   }

   private final Runnable getConfigService = new Runnable() {

      @Override
      public void run() {
         atsApi.getConfigService().getConfigurationsWithPend();
      }
   };

   @Override
   public String convertAisAndTeamDefs() {
      ConvertAtsAisAndTeamDefsOperation op = new ConvertAtsAisAndTeamDefsOperation(atsApi);
      XResultData rd = op.run();
      rd.sortResults();
      return rd.getHtml();
   }

   @Override
   public AtsConfigurations get() {
      return atsApi.getConfigService().getConfigurations();
   }

   @Override
   public List<ArtifactImage> getArtifactImages() {
      if (images == null) {
         images = new LinkedList<>();
         images.addAll(AtsArtifactImages.getImages());
         for (ArtifactTypeToken artifactType : AtsArtifactTypes.TeamWorkflow.getAllDescendantTypes()) {
            images.add(ArtifactImage.construct(artifactType, AtsArtifactImages.AGILE_TASK.getImageName(),
               AtsArtifactImages.AGILE_TASK.getBaseUrl()));
         }
      }
      return images;
   }

   @Override
   public AtsConfigurations getWithPend() {
      return atsApi.getConfigService().getConfigurationsWithPend();
   }

   @Override
   public ViewModel getNewSource() {
      return new ViewModel("templates/newConfigBranch.html");
   }

   @Override
   public XResultData createUpdateConfig() {
      XResultData resultData = new XResultData(false);
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      update.createUpdateConfig(resultData);
      if (resultData.isEmpty()) {
         resultData.log("Nothing to update");
      }
      return resultData;
   }

   @Override
   public List<AtsAttrValCol> generateAttrTypeViews() throws Exception {
      Map<String, AttributeTypeToken> idToToken = new HashMap<>();
      for (AttributeTypeToken attrType : orcsApi.tokenService().getAttributeTypes()) {
         idToToken.put(attrType.getName(), attrType);
      }

      List<String> sortedIds = new LinkedList<>();
      sortedIds.addAll(idToToken.keySet());
      Collections.sort(sortedIds);

      List<AtsAttrValCol> columns = new LinkedList<>();
      for (String id : sortedIds) {
         AttributeTypeToken attrType = idToToken.get(id);
         ColumnAlign columnAlign = ColumnAlign.Left;
         SortDataType sortDataType = SortDataType.String;
         int width = 60;

         if (attrType.isEnumerated()) {
            width = 40;
         } else if (attrType.isBoolean()) {
            width = 50;
         } else if (attrType.isInteger()) {
            width = 45;
            sortDataType = SortDataType.Integer;
            columnAlign = ColumnAlign.Center;
         } else if (attrType.isDouble()) {
            width = 40;
            sortDataType = SortDataType.Float;
            columnAlign = ColumnAlign.Center;
         } else if (attrType.isDate()) {
            width = 80;
            sortDataType = SortDataType.Date;
         }

         AtsAttrValCol valueColumn = new AtsAttrValCol();
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
         if (attrType.isBoolean()) {
            valueColumn.setBooleanNotSetShow("");
            valueColumn.setBooleanOnFalseShow("false");
            valueColumn.setBooleanOnTrueShow("true");
         }
         columns.add(valueColumn);
      }
      return columns;
   }

   private String generateId(String id, String name) {
      String cleanName = name.replaceAll("-", " ");
      cleanName = cleanName.replaceAll("\\(", " ");
      cleanName = cleanName.replaceAll("\\)", " ");
      cleanName = cleanName.replaceAll("/", " ");
      cleanName = cleanName.replaceAll(" +", " ");
      cleanName = cleanName.replaceAll("^ ", "");
      String replaceValue = Lib.toCamelCaseFromStringsWithSpaces(cleanName);
      String result = id.replaceFirst(Lib.escapeForRegex(name), replaceValue);
      return result;
   }

   @Override
   public XResultData alive() {
      XResultData results = new XResultData();
      results.logf("Alive");
      return results;
   }

   @Override
   public XResultData demoDbInit() {
      XResultData rd = new XResultData();
      try {
         AtsDbConfigDemoOp config = new AtsDbConfigDemoOp(atsApi);
         config.run();
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public XResultData atsDbInit() {
      XResultData rd = new XResultData();
      try {
         rd = atsApi.getConfigService().configAtsDatabase(atsApi);
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public ActionableItem getActionableItem(ArtifactId aiId) {
      return atsApi.getActionableItemService().getActionableItemById(aiId);
   }

   @Override
   public Version getVersion(ArtifactId verId) {
      return atsApi.getVersionService().getVersionById(verId);
   }

   @Override
   public TeamDefinition getTeamDefinition(ArtifactId teamDefId) {
      return atsApi.getTeamDefinitionService().getTeamDefinitionById(teamDefId);
   }

   @Override
   public List<ArtifactToken> getTeamLeads(ArtifactId teamDefId) {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andRelatedTo(
         AtsRelationTypes.TeamLead_Team, teamDefId).asArtifactTokens();
   }

   @Override
   public TransactionId demoInitilize(UserToken superUser) {
      TransactionId txId = orcsApi.getAdminOps().createDatastoreAndSystemBranches(superUser);
      orcsApi.getAdminOps().createDemoBranches();
      atsApi.getConfigService().configAtsDatabase(atsApi);
      return txId;
   }

   @Override
   public XResultData validate() {
      AtsHealthCheckOperation op = new AtsHealthCheckOperation(atsApi, atsApi.getJdbcService());
      return op.run();
   }

   @Override
   public XResultData getResultTableTest() {
      return ExampleTableData.getResultTable();
   }

   @Override
   @POST
   @Path("branch")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public BranchData createBranch(BranchData branchData) {
      AtsConfigOperations ops = new AtsConfigOperations(atsApi);
      return ops.createBranch(branchData);
   }

   @Override
   @POST
   @Path("configForDemoPl/{branch}")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData configForDemoPl(@PathParam("branch") BranchId branch) {
      try {
         orcsApi.getAdminOps().configForDemoPl(branch);
      } catch (Exception ex) {
         XResultData rd = new XResultData();
         rd.errorf("Error configForDemoPl %s", Lib.exceptionToString(ex));
         return rd;
      }
      return XResultData.EMPTY_RD;
   }

}