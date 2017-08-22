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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.JaxActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.config.JaxVersion;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpointApi {

   private final OrcsApi orcsApi;
   private final IAtsServices services;
   private final Log logger;
   private AtsConfigurations atsConfigurations;
   private final Collection<Long> teamDefIds = new LinkedList<>();
   private final Collection<Long> aiIds = new LinkedList<>();
   private final long DEFAULT_CONFIG_LOADING_TIME = 1200000; // 20 minutes

   public AtsConfigEndpointImpl(IAtsServices services, OrcsApi orcsApi, Log logger) {
      this.services = services;
      this.orcsApi = orcsApi;
      this.logger = logger;
      startAtsConfigurationsReloader();
   }

   private void startAtsConfigurationsReloader() {
      Thread thread = new Thread("ATS Configuration Re-Loader") {
         @Override
         public void run() {
            while (true) {
               if (orcsApi.getAdminOps().isDataStoreInitialized()) {
                  AtsConfigurations configs = getAtsConfigurationsFromDb();
                  atsConfigurations = configs;
               } else {
                  atsConfigurations = new AtsConfigurations();
               }
               try {
                  long reloadTime = AtsUtilCore.SERVER_CONFIG_RELOAD_MS_DEFAULT;
                  String reloadTimeStr = services.getConfigValue(AtsUtilCore.SERVER_CONFIG_RELOAD_MS_KEY);
                  if (Strings.isNumeric(reloadTimeStr)) {
                     reloadTime = Long.valueOf(reloadTimeStr);
                  }
                  Thread.sleep(reloadTime);
               } catch (InterruptedException ex) {
                  // do nothing
               }
            }
         }
      };
      thread.start();
   }

   private AtsConfigurations getAtsConfigurationsFromDb() {
      teamDefIds.clear();
      aiIds.clear();
      ResultSet<ArtifactReadable> artifacts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         AtsArtifactTypes.Configuration).getResults();
      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      for (ArtifactReadable art : artifacts) {
         AtsConfiguration config = new AtsConfiguration();
         configs.getConfigs().add(config);
         config.setName(art.getName());
         config.setUuid(art.getId());
         config.setBranchUuid(Long.valueOf(art.getSoleAttributeValue(AtsAttributeTypes.AtsConfiguredBranch, "0L")));
         config.setIsDefault(art.getSoleAttributeValue(AtsAttributeTypes.Default, false));
      }
      UpdateAtsConfiguration update = new UpdateAtsConfiguration((IAtsServer) services);
      AtsViews views = update.getConfigViews();
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns());
      // load valid state names
      configs.setValidStateNames(update.getValidStateNames());
      // load users
      for (IAtsUser user : services.getUserService().getUsersFromDb()) {
         configs.getUsers().add((AtsUser) user);
      }
      // load admins
      ArtifactReadable atsAdminArt = orcsApi.getQueryFactory().fromBranch(services.getAtsBranch()).andId(
         AtsArtifactToken.AtsAdmin).getResults().getAtMostOneOrNull();
      if (atsAdminArt != null) {
         for (ArtifactReadable member : atsAdminArt.getRelated(CoreRelationTypes.Users_User)) {
            configs.getAtsAdmins().add(member);
         }
      }

      Map<Long, ArtifactReadable> idToArtifact = new HashMap<>();

      List<ArtifactReadable> configArts =
         orcsApi.getQueryFactory().fromBranch(services.getAtsBranch()).andIsOfType(AtsArtifactTypes.TeamDefinition,
            AtsArtifactTypes.Version, AtsArtifactTypes.ActionableItem).getResults().getList();

      // load ats config objects
      for (ArtifactReadable configArtId : configArts) {
         if (services.getStoreService().isOfType(configArtId, AtsArtifactTypes.TeamDefinition)) {
            JaxTeamDefinition teamDef = createJaxTeamDefinition(configArtId);
            configs.addTeamDef(teamDef);
         } else if (services.getStoreService().isOfType(configArtId, AtsArtifactTypes.ActionableItem)) {
            JaxActionableItem ai = createJaxActionableItem(configArtId);
            configs.addAi(ai);
         } else if (services.getStoreService().isOfType(configArtId, AtsArtifactTypes.Version)) {
            JaxVersion version = createJaxVersion(configArtId);
            configs.addVersion(version);
         }
         idToArtifact.put(configArtId.getId(), configArtId);
      }

      // load team def tree

      addTeamDefinitionChildrenWIthRecurse(AtsArtifactToken.TopTeamDefinition.getId(), idToArtifact, configs);
      configs.setTopTeamDefinition(AtsArtifactToken.TopTeamDefinition);

      // load actionable items tree
      addActionableItemChildrenWIthRecurse(AtsArtifactToken.TopActionableItem.getId(), idToArtifact, configs);
      configs.setTopActionableItem(AtsArtifactToken.TopActionableItem);

      // load work definitions
      for (ArtifactToken workDefArt : orcsApi.getQueryFactory().fromBranch(services.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.WorkDefinition).getResults()) {
         String workDefStr =
            services.getAttributeResolver().getSoleAttributeValueAsString(workDefArt, AtsAttributeTypes.DslSheet, "");
         configs.getWorkDefinitionsData().add(new WorkDefData(workDefArt.getId(), workDefArt.getName(), workDefStr));
      }
      return configs;
   }

   private JaxActionableItem addActionableItemChildrenWIthRecurse(Long aiId, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      ArtifactReadable aiArt = idToArtifact.get(aiId);
      if (aiArt != null && aiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
         JaxActionableItem jaxAi = configs.getIdToAi().get(aiId);
         for (Long childId : aiArt.getChildrentIds()) {
            if (isActionableItemId(childId)) {
               JaxActionableItem child = addActionableItemChildrenWIthRecurse(childId, idToArtifact, configs);
               if (child != null) {
                  child.setParentId(aiId);
                  jaxAi.addChild(child);
               }
            }
         }
         return jaxAi;
      }
      return null;
   }

   private JaxTeamDefinition addTeamDefinitionChildrenWIthRecurse(Long teamDefId, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      ArtifactReadable teamDef = idToArtifact.get(teamDefId);
      if (teamDef != null && teamDef.isOfType(AtsArtifactTypes.TeamDefinition)) {
         JaxTeamDefinition jaxTeamDef = configs.getIdToTeamDef().get(teamDefId);
         for (Long childId : teamDef.getChildrentIds()) {
            if (isTeamDefinitionId(childId)) {
               JaxTeamDefinition child = addTeamDefinitionChildrenWIthRecurse(childId, idToArtifact, configs);
               if (child != null) {
                  child.setParentId(teamDefId);
                  jaxTeamDef.addChild(child);
               }
            }
         }
         // add team to version ids
         for (Long versionId : services.getRelationResolver().getRelatedIds(teamDef,
            AtsRelationTypes.TeamDefinitionToVersion_Version)) {
            jaxTeamDef.addVersion(versionId);
            JaxVersion version = configs.getIdToVersion().get(versionId);
            version.setTeamDefId(teamDefId);
         }
         // add team to ai ids
         for (Long aiId : services.getRelationResolver().getRelatedIds(teamDef,
            AtsRelationTypes.TeamActionableItem_ActionableItem)) {
            JaxActionableItem jai = configs.getIdToAi().get(aiId);
            if (jai != null) {
               jaxTeamDef.addAi(aiId);
               jai.setTeamDefId(teamDefId);
            }
         }
         return jaxTeamDef;
      }
      return null;
   }

   private boolean isTeamDefinitionId(Long childId) {
      if (teamDefIds.isEmpty()) {
         for (ArtifactId art : services.getQueryService().createQuery(AtsArtifactTypes.TeamDefinition).getIds()) {
            teamDefIds.add(art.getId());
         }
      }
      return teamDefIds.contains(childId);
   }

   private boolean isActionableItemId(Long childId) {
      if (aiIds.isEmpty()) {
         for (ArtifactId art : services.getQueryService().createQuery(AtsArtifactTypes.ActionableItem).getIds()) {
            aiIds.add(art.getId());
         }
      }
      return aiIds.contains(childId);
   }

   private JaxVersion createJaxVersion(ArtifactReadable verArt) {
      JaxVersion jaxVersion = new JaxVersion();
      jaxVersion.setName(verArt.getName());
      jaxVersion.setUuid(verArt.getId());
      jaxVersion.setGuid(verArt.getGuid());
      jaxVersion.setActive(verArt.getSoleAttributeValue(AtsAttributeTypes.Active, true));
      return jaxVersion;
   }

   private JaxActionableItem createJaxActionableItem(ArtifactReadable aiArt) {
      JaxActionableItem jaxAi = new JaxActionableItem();
      jaxAi.setName(aiArt.getName());
      jaxAi.setUuid(aiArt.getId());
      jaxAi.setGuid(aiArt.getGuid());
      jaxAi.setDescription(aiArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      jaxAi.setActive(aiArt.getSoleAttributeValue(AtsAttributeTypes.Active, true));
      return jaxAi;
   }

   private JaxTeamDefinition createJaxTeamDefinition(ArtifactReadable teamDefArt) {
      JaxTeamDefinition jaxTeamDef = new JaxTeamDefinition();
      jaxTeamDef.setName(teamDefArt.getName());
      jaxTeamDef.setUuid(teamDefArt.getId());
      jaxTeamDef.setGuid(teamDefArt.getGuid());
      jaxTeamDef.setActive(teamDefArt.getSoleAttributeValue(AtsAttributeTypes.Active, true));
      for (ArtifactToken ai : services.getRelationResolver().getRelated(teamDefArt,
         AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         jaxTeamDef.getAis().add(ai.getId());
      }
      return jaxTeamDef;
   }

   @Override
   @GET
   @Path("clearcache")
   @Produces(MediaType.APPLICATION_JSON)
   public String clearCaches() {
      atsConfigurations = null;
      Thread thread = new Thread() {

         @Override
         public void run() {
            super.run();
            get();
         }
      };
      thread.start();
      return "Complete";
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public AtsConfigurations get() {
      if (atsConfigurations == null) {
         atsConfigurations = getAtsConfigurationsFromDb();
      }
      return atsConfigurations;
   }

   @GET
   @Path("fromdb")
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public AtsConfigurations getFromDb() {
      return getAtsConfigurationsFromDb();
   }

   @GET
   @Path("ui/NewAtsBranchConfig")
   @Override
   public ViewModel getNewSource() throws Exception {
      return new ViewModel("templates/newConfigBranch.html");
   }

   @POST
   @Path("branch")
   @Override
   public AtsConfiguration createConfig(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) {

      // get parameters
      String query = uriInfo.getPath();
      System.out.println("query [" + query + "]");
      BranchId fromBranchUuid = BranchId.valueOf(form.getFirst("fromBranchUuid"));
      String newBranchName = form.getFirst("newBranchName");
      Conditions.checkNotNullOrEmpty(newBranchName, "newBranchName");
      String userId = form.getFirst("userId");
      Conditions.checkNotNullOrEmpty(userId, "UserId");
      ArtifactId user = services.getUserService().getUserById(userId).getStoreObject();
      if (user == null) {
         logger.error("User by id [%s] does not exist", userId);
      }
      org.eclipse.osee.orcs.data.BranchReadable fromBranch =
         orcsApi.getQueryFactory().branchQuery().andId(fromBranchUuid).getResults().getExactlyOne();

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

      // Return new branch uuid
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
         introArt =
            orcsApi.getQueryFactory().fromBranch(fromBranch).andTypeEquals(introToken.getArtifactType()).andNameEquals(
               introToken.getName()).getResults().getAtMostOneOrNull();
      }
      Conditions.checkNotNull(introArt, "No artifact found for token " + introToken);
      ArtifactId artifact = tx.introduceArtifact(fromBranch, introArt);
      if (relateToToken != null) {
         relateToArt =
            orcsApi.getQueryFactory().fromBranch(newBranch).andId(relateToToken).getResults().getAtMostOneOrNull();
         if (relateToArt == null) {
            relateToArt = orcsApi.getQueryFactory().fromBranch(newBranch).andTypeEquals(
               relateToToken.getArtifactType()).andNameEquals(
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
      config.setBranchUuid(branch.getId());
      config.setIsDefault(false);
      ArtifactId configArt = tx.createArtifact(AtsArtifactTypes.Configuration, branchName);
      config.setUuid(((ArtifactReadable) configArt).getId());
      tx.createAttribute(configArt, AtsAttributeTypes.AtsConfiguredBranch, branch.getIdString());
      XResultData rd = new XResultData();
      UpdateAtsConfiguration update = new UpdateAtsConfiguration((IAtsServer) services);

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

   @POST
   @Override
   public Response createUpdateConfig() {
      XResultData resultData = new XResultData(false);
      UpdateAtsConfiguration update = new UpdateAtsConfiguration((IAtsServer) services);
      update.createUpdateConfig(resultData);
      if (resultData.isEmpty()) {
         resultData.log("Nothing to update");
      }
      return Response.ok(resultData.toString()).build();
   }

   @PUT
   @Path("workDef")
   @Override
   public Response storeWorkDef(JaxAtsWorkDef jaxWorkDef) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         services.getArtifact(AtsCoreUsers.SYSTEM_USER), "Store Work Definition " + jaxWorkDef.getName());
      ArtifactReadable workDefArt = orcsApi.getQueryFactory().fromBranch(services.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.WorkDefinition).andNameEquals(jaxWorkDef.getName()).getResults().getAtMostOneOrNull();
      if (workDefArt == null) {
         workDefArt = (ArtifactReadable) tx.createArtifact(AtsArtifactTypes.WorkDefinition, jaxWorkDef.getName());
      }
      tx.setSoleAttributeValue(workDefArt, AtsAttributeTypes.DslSheet, jaxWorkDef.getWorkDefDsl());
      if (workDefArt.getParent() == null) {
         ArtifactReadable workDefFolder =
            (ArtifactReadable) services.getArtifact(AtsArtifactToken.WorkDefinitionsFolder);
         tx.addChildren(workDefFolder, workDefArt);
      }
      tx.commit();
      ((IAtsServer) services).getWorkDefinitionService().clearCaches();
      return Response.ok().build();
   }

   @Override
   public List<AtsAttributeValueColumn> generateAttrTypeViews() throws Exception {
      Map<String, AttributeTypeToken> idToToken = new HashMap<>();
      IAtsServer atsServer = (IAtsServer) services;
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
   @GET
   @Path("alive")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData alive() {
      XResultData results = new XResultData();
      results.logf("Alive");
      return results;
   }

}
