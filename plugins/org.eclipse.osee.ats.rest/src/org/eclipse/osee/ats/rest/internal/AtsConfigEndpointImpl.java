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
package org.eclipse.osee.ats.rest.internal;

import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
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
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import com.google.gson.Gson;

/**
 * @author Donald G. Dunne
 */
public final class AtsConfigEndpointImpl implements AtsConfigEndpointApi {

   private static final String VIEWS_KEY = "views";
   private final OrcsApi orcsApi;
   private final IAtsServer atsServer;
   private final Log logger;
   private final Gson gson = new Gson();

   public AtsConfigEndpointImpl(IAtsServer atsServer, OrcsApi orcsApi, Log logger, IResourceRegistry registry) {
      this.atsServer = atsServer;
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   @Override
   public AtsConfigurations get() {
      ResultSet<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andTypeEquals(AtsArtifactTypes.Configuration).getResults();
      AtsConfigurations configs = new AtsConfigurations();
      for (ArtifactReadable art : artifacts) {
         AtsConfiguration config = new AtsConfiguration();
         configs.getConfigs().add(config);
         config.setName(art.getName());
         config.setUuid(art.getLocalId());
         config.setBranchUuid(Long.valueOf(art.getSoleAttributeValue(AtsAttributeTypes.AtsConfiguredBranch, "0L")));
         config.setIsDefault(art.getSoleAttributeValue(AtsAttributeTypes.Default, false));
      }
      String viewsStr = atsServer.getConfigValue(VIEWS_KEY);
      if (Strings.isValid(viewsStr)) {
         AtsViews views = gson.fromJson(viewsStr, AtsViews.class);
         configs.setViews(views);
      }
      return configs;
   }

   @Override
   public ViewModel getNewSource() throws Exception {
      return new ViewModel("templates/newConfigBranch.html");
   }

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
      IAtsUser user = atsServer.getUserService().getUserById(userId);
      if (user == null) {
         logger.error("User by id [%s] does not exist", userId);
      }
      ArtifactReadable userArt = atsServer.getArtifact(user);
      org.eclipse.osee.orcs.data.BranchReadable fromBranch =
         orcsApi.getQueryFactory(null).branchQuery().andUuids(fromBranchUuid).getResults().getExactlyOne();

      // Create new baseline branch off Root
      Callable<BranchReadable> newBranchCallable =
         orcsApi.getBranchOps(null).createTopLevelBranch(TokenFactory.createBranch(newBranchName), userArt);
      BranchReadable newBranch;
      try {
         newBranch = newBranchCallable.call();
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR, "Error creating new branch");
      }
      long newBranchUuid = newBranch.getUuid();

      // Introduce all ATS heading artifacts to new branch
      introduceAtsHeadingArtifacts(fromBranch, newBranch, userArt);

      // Create config artifact on Common
      AtsConfiguration config = createConfigArtifactOnCommon(newBranchName, userArt, newBranchUuid);

      // Return new branch uuid
      return config;
   }

   private void introduceAtsHeadingArtifacts(org.eclipse.osee.orcs.data.BranchReadable fromBranch, BranchReadable newBranch, ArtifactReadable userArt) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory(null).createTransaction(newBranch, userArt, "Add ATS Configuration");

      ArtifactId headingArt =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.HeadingFolder, newBranch,
            CoreArtifactTokens.DefaultHierarchyRoot, null);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopActionableItem, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.TopTeamDefinition, newBranch, null, headingArt);
      ArtifactId configArt =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.ConfigFolder, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.ConfigsFolder, newBranch, null, configArt);
      ArtifactId workDefFolder =
         introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDefinitionsFolder, newBranch, null, headingArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.Users, newBranch, null, configArt);
      introduceAndRelateTo(tx, fromBranch, AtsArtifactToken.WorkDef_State_Names, newBranch, null, workDefFolder);

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
         orcsApi.getQueryFactory(null).fromBranch(fromBranch).andIds(introToken).getResults().getAtMostOneOrNull();
      if (introArt == null) {
         introArt =
            orcsApi.getQueryFactory(null).fromBranch(fromBranch).andTypeEquals(introToken.getArtifactType()).andNameEquals(
               introToken.getName()).getResults().getAtMostOneOrNull();
      }
      Conditions.checkNotNull(introArt, "No artifact found for token " + introToken);
      ArtifactId artifact = tx.introduceArtifact(introArt);
      if (relateToToken != null) {
         relateToArt =
            orcsApi.getQueryFactory(null).fromBranch(newBranch).andIds(relateToToken).getResults().getAtMostOneOrNull();
         if (relateToArt == null) {
            relateToArt =
               orcsApi.getQueryFactory(null).fromBranch(newBranch).andTypeEquals(relateToToken.getArtifactType()).andNameEquals(
                  relateToToken.getName()).getResults().getAtMostOneOrNull();
         }
      }
      tx.addChildren(relateToArt, artifact);
      return artifact;
   }

   private AtsConfiguration createConfigArtifactOnCommon(String branchName, ArtifactReadable userArt, long newBranchUuid) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory(null).createTransaction(CoreBranches.COMMON, userArt, "Add ATS Configuration");
      AtsConfiguration config = new AtsConfiguration();
      config.setName(branchName);
      config.setBranchUuid(newBranchUuid);
      config.setIsDefault(false);
      ArtifactId configArt = tx.createArtifact(AtsArtifactTypes.Configuration, branchName);
      config.setUuid(((ArtifactReadable) configArt).getLocalId());
      tx.createAttribute(configArt, AtsAttributeTypes.AtsConfiguredBranch, String.valueOf(newBranchUuid));
      // Get or create Configs folder
      ArtifactId configsFolderArt = getOrCreateConfigsFolder(tx, userArt);
      // Add configuration to configs folder
      tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Child, configArt);
      tx.commit();
      return config;
   }

   @SuppressWarnings("unchecked")
   private ArtifactId getOrCreateConfigsFolder(TransactionBuilder tx, ArtifactReadable userArt) {
      ArtifactId configsFolderArt =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(AtsArtifactToken.ConfigsFolder).getResults().getAtMostOneOrNull();
      if (configsFolderArt == null) {
         configsFolderArt = tx.createArtifact(AtsArtifactToken.ConfigsFolder);
         ArtifactReadable configFolderArt =
            orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(AtsArtifactToken.ConfigFolder).getResults().getExactlyOne();
         tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, configFolderArt);
      }
      return configsFolderArt;
   }
}
