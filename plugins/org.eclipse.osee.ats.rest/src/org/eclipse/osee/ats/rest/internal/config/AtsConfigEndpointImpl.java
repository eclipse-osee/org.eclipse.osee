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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
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
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.JaxAtsUser;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpointApi {

   private final OrcsApi orcsApi;
   private final IAtsServer atsServer;
   private final Log logger;

   public AtsConfigEndpointImpl(IAtsServer atsServer, OrcsApi orcsApi, Log logger) {
      this.atsServer = atsServer;
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   private final Supplier<AtsConfigurations> configurationsCache =
      Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 5, TimeUnit.MINUTES);

   private Supplier<AtsConfigurations> getConfigurationsSupplier() {
      return new Supplier<AtsConfigurations>() {
         @SuppressWarnings("unchecked")
         @Override
         public AtsConfigurations get() {
            ResultSet<ArtifactReadable> artifacts =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
                  AtsArtifactTypes.Configuration).getResults();
            // load ats branch configurations
            AtsConfigurations configs = new AtsConfigurations();
            for (ArtifactReadable art : artifacts) {
               AtsConfiguration config = new AtsConfiguration();
               configs.getConfigs().add(config);
               config.setName(art.getName());
               config.setUuid(art.getId());
               config.setBranchUuid(
                  Long.valueOf(art.getSoleAttributeValue(AtsAttributeTypes.AtsConfiguredBranch, "0L")));
               config.setIsDefault(art.getSoleAttributeValue(AtsAttributeTypes.Default, false));
            }
            UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsServer);
            AtsViews views = update.getConfigViews();
            // load views
            configs.setViews(views);
            // load color column config
            configs.setColorColumns(update.getColorColumns());
            // load valid state names
            configs.setValidStateNames(update.getValidStateNames());
            // load users
            for (IAtsUser user : atsServer.getUserService().getUsers()) {
               configs.getUsers().add((JaxAtsUser) user);
            }
            // load admins
            ArtifactReadable atsAdminArt = orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIds(
               AtsArtifactToken.AtsAdmin).getResults().getAtMostOneOrNull();
            if (atsAdminArt != null) {
               for (ArtifactReadable member : atsAdminArt.getRelated(CoreRelationTypes.Users_User)) {
                  configs.getAtsAdmins().add(member.getId());
               }
            }
            // load ats config object ids
            for (HasLocalId<Integer> configArtId : orcsApi.getQueryFactory().fromBranch(
               AtsUtilCore.getAtsBranch()).andIsOfType(AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.Version,
                  AtsArtifactTypes.ActionableItem).getResultsAsLocalIds()) {
               configs.getAtsConfigIds().add(Long.valueOf(configArtId.getLocalId()));
            }
            // load work definitions
            for (ArtifactId workDefArt : orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
               AtsArtifactTypes.WorkDefinition).getResults()) {
               String workDefStr = atsServer.getAttributeResolver().getSoleAttributeValueAsString(workDefArt,
                  AtsAttributeTypes.DslSheet, "");
               configs.getWorkDefIdToWorkDef().put(workDefArt.getName(), workDefStr);
            }
            return configs;
         }

      };
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public AtsConfigurations get() {
      return configurationsCache.get();
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
      long fromBranchUuid = Long.valueOf(form.getFirst("fromBranchUuid"));
      String newBranchName = form.getFirst("newBranchName");
      Conditions.checkNotNullOrEmpty(newBranchName, "newBranchName");
      String userId = form.getFirst("userId");
      Conditions.checkNotNullOrEmpty(userId, "UserId");
      ArtifactId user = atsServer.getUserService().getUserById(userId).getStoreObject();
      if (user == null) {
         logger.error("User by id [%s] does not exist", userId);
      }
      org.eclipse.osee.orcs.data.BranchReadable fromBranch =
         orcsApi.getQueryFactory().branchQuery().andUuids(fromBranchUuid).getResults().getExactlyOne();

      // Create new baseline branch off Root
      Callable<BranchReadable> newBranchCallable =
         orcsApi.getBranchOps().createTopLevelBranch(TokenFactory.createBranch(newBranchName), user);
      BranchReadable newBranch;
      try {
         newBranch = newBranchCallable.call();
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR, "Error creating new branch");
      }
      long newBranchUuid = newBranch.getUuid();

      // Introduce all ATS heading artifacts to new branch
      introduceAtsHeadingArtifacts(fromBranch, newBranch, user);

      // Create config artifact on Common
      AtsConfiguration config = createConfigArtifactOnCommon(newBranchName, user, newBranchUuid);

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

   @SuppressWarnings("unchecked")
   private ArtifactId introduceAndRelateTo(TransactionBuilder tx, org.eclipse.osee.orcs.data.BranchReadable fromBranch, IArtifactToken introToken, BranchReadable newBranch, IArtifactToken relateToToken, ArtifactId relateToArt) {
      ArtifactReadable introArt =
         orcsApi.getQueryFactory().fromBranch(fromBranch).andIds(introToken).getResults().getAtMostOneOrNull();
      if (introArt == null) {
         introArt =
            orcsApi.getQueryFactory().fromBranch(fromBranch).andTypeEquals(introToken.getArtifactType()).andNameEquals(
               introToken.getName()).getResults().getAtMostOneOrNull();
      }
      Conditions.checkNotNull(introArt, "No artifact found for token " + introToken);
      ArtifactId artifact = tx.introduceArtifact(fromBranch, introArt);
      if (relateToToken != null) {
         relateToArt =
            orcsApi.getQueryFactory().fromBranch(newBranch).andIds(relateToToken).getResults().getAtMostOneOrNull();
         if (relateToArt == null) {
            relateToArt = orcsApi.getQueryFactory().fromBranch(newBranch).andTypeEquals(
               relateToToken.getArtifactType()).andNameEquals(
                  relateToToken.getName()).getResults().getAtMostOneOrNull();
         }
      }
      tx.addChildren(relateToArt, artifact);
      return artifact;
   }

   private AtsConfiguration createConfigArtifactOnCommon(String branchName, ArtifactId userArt, long newBranchUuid) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt, "Add ATS Configuration");
      AtsConfiguration config = new AtsConfiguration();
      config.setName(branchName);
      config.setBranchUuid(newBranchUuid);
      config.setIsDefault(false);
      ArtifactId configArt = tx.createArtifact(AtsArtifactTypes.Configuration, branchName);
      config.setUuid(((ArtifactReadable) configArt).getId());
      tx.createAttribute(configArt, AtsAttributeTypes.AtsConfiguredBranch, String.valueOf(newBranchUuid));
      XResultData rd = new XResultData();
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsServer);

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
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsServer);
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
         atsServer.getArtifact(AtsCoreUsers.SYSTEM_USER.getId()), "Store Work Definition " + jaxWorkDef.getName());
      ArtifactReadable workDefArt = orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.WorkDefinition).andNameEquals(jaxWorkDef.getName()).getResults().getAtMostOneOrNull();
      if (workDefArt == null) {
         workDefArt = (ArtifactReadable) tx.createArtifact(AtsArtifactTypes.WorkDefinition, jaxWorkDef.getName());
      }
      tx.setSoleAttributeValue(workDefArt, AtsAttributeTypes.DslSheet, jaxWorkDef.getWorkDefDsl());
      if (workDefArt.getParent() == null) {
         ArtifactReadable workDefFolder = atsServer.getArtifact(AtsArtifactToken.WorkDefinitionsFolder);
         tx.addChildren(workDefFolder, workDefArt);
      }
      tx.commit();
      atsServer.getWorkDefAdmin().clearCaches();
      return Response.ok().build();
   }

}
