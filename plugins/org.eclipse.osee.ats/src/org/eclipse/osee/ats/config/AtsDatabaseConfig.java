/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config;

import java.util.Arrays;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {
      createAtsFolders();

      // load top team into cache
      Artifact topTeamDefArt =
         ArtifactQuery.getArtifactFromToken(AtsArtifactToken.TopTeamDefinition, AtsUtilCore.getAtsBranch());
      IAtsTeamDefinition teamDef = AtsClientService.get().getConfigObject(topTeamDefArt);
      teamDef.setWorkflowDefinition(IAtsWorkDefinitionAdmin.TeamWorkflowDefaultDefinitionId);
      AtsChangeSet changes = new AtsChangeSet("Set Top Team Work Definition");
      AtsClientService.get().storeConfigObject(teamDef, changes);
      changes.execute();

      // load top ai into cache
      Artifact topAiArt =
         ArtifactQuery.getArtifactFromToken(AtsArtifactToken.TopActionableItem, AtsUtilCore.getAtsBranch());
      IAtsActionableItem aia = AtsClientService.get().getConfigObject(topAiArt);
      aia.setActionable(false);
      changes.reset("Set Top AI to Non Actionable");
      AtsClientService.get().storeConfigObject(aia, changes);
      changes.execute();

      AtsWorkDefinitionSheetProviders.initializeDatabase(new XResultData(false));

      AtsGroup.AtsAdmin.getArtifact().persist(getClass().getSimpleName());
      AtsGroup.AtsTempAdmin.getArtifact().persist(getClass().getSimpleName());
   }

   public static void createAtsFolders() throws OseeCoreException {
      IOseeBranch atsBranch = AtsUtilCore.getAtsBranch();
      SkynetTransaction transaction = TransactionManager.createTransaction(atsBranch, "Create ATS Folders");

      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);
      if (!headingArt.hasParent()) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(atsBranch);
         rootArt.addChild(headingArt);
         headingArt.persist(transaction);
      }
      for (IArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem, AtsArtifactToken.TopTeamDefinition,
         AtsArtifactToken.WorkDefinitionsFolder)) {
         Artifact art = OseeSystemArtifacts.getOrCreateArtifact(token, atsBranch);
         headingArt.addChild(art);
         art.persist(transaction);
      }

      Artifact configFolderArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.ConfigFolder, atsBranch);
      headingArt.addChild(configFolderArt);
      configFolderArt.persist(transaction);

      for (IArtifactToken token : Arrays.asList(AtsArtifactToken.Users, AtsArtifactToken.ConfigsFolder)) {
         Artifact art = OseeSystemArtifacts.getOrCreateArtifact(token, atsBranch);
         configFolderArt.addChild(art);
         art.persist(transaction);
      }

      Artifact configArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.AtsConfig, atsBranch);
      setConfigAttributes(configArt);
      configFolderArt.addChild(configArt);
      configArt.persist(transaction);

      transaction.execute();
   }

   private static void setConfigAttributes(Artifact configArt) throws OseeCoreException {
      PluginUtil util = new PluginUtil(Activator.PLUGIN_ID);
      try {
         String json = Lib.fileToString(util.getPluginFile("support/views.json"));
         configArt.addAttribute(CoreAttributeTypes.GeneralStringData, "views=" + json);
      } catch (Exception ex) {
         throw new OseeWrappedException("Error loading column views.json file", ex);
      }
   }

   public static void organizePrograms(IArtifactType programType, IArtifactToken programFolderToken) {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Organize Programs");
      Artifact programFolder = OseeSystemArtifacts.getOrCreateArtifact(programFolderToken, AtsUtilCore.getAtsBranch());
      programFolder.persist(transaction);
      for (Artifact programArt : ArtifactQuery.getArtifactListFromType(programType, AtsUtilCore.getAtsBranch())) {
         if (!programFolder.getChildren().contains(programArt)) {
            programFolder.addChild(programArt);
         }
      }
      transaction.execute();
   }
}