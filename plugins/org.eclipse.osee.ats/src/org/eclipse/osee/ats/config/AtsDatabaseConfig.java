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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.client.config.store.ActionableItemArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
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
         ArtifactQuery.getArtifactFromToken(AtsArtifactToken.TopTeamDefinition, AtsUtil.getAtsBranchToken());
      TeamDefinitionArtifactStore teamDefStore =
         new TeamDefinitionArtifactStore(topTeamDefArt, AtsConfigCache.instance);
      IAtsTeamDefinition teamDef = teamDefStore.getTeamDefinition();
      teamDef.setWorkflowDefinition(WorkDefinitionFactory.TeamWorkflowDefaultDefinitionId);
      teamDefStore.save("Set Top Team Work Definition");

      // load top ai into cache
      Artifact topAiArt =
         ArtifactQuery.getArtifactFromToken(AtsArtifactToken.TopActionableItem, AtsUtil.getAtsBranchToken());
      ActionableItemArtifactStore aiStore = new ActionableItemArtifactStore(topAiArt, AtsConfigCache.instance);
      IAtsActionableItem aia = aiStore.getActionableItem();
      aia.setActionable(false);
      aiStore.save("Set Top AI to Non Actionable");

      AtsWorkDefinitionSheetProviders.initializeDatabase(new XResultData(false));

      AtsGroup.AtsAdmin.getArtifact().persist(getClass().getSimpleName());
      AtsGroup.AtsTempAdmin.getArtifact().persist(getClass().getSimpleName());
   }

   public static void createAtsFolders() throws OseeCoreException {
      Branch atsBranch = AtsUtil.getAtsBranch();
      SkynetTransaction transaction = TransactionManager.createTransaction(atsBranch, "Create ATS Folders");

      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);
      if (!headingArt.hasParent()) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(atsBranch);
         rootArt.addChild(headingArt);
         headingArt.persist(transaction);
      }
      for (IArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem, AtsArtifactToken.TopTeamDefinition,
         AtsArtifactToken.ConfigFolder, //
         AtsArtifactToken.WorkDefinitionsFolder)) {
         Artifact art = OseeSystemArtifacts.getOrCreateArtifact(token, atsBranch);
         headingArt.addChild(art);
         art.persist(transaction);
      }
      transaction.execute();
   }
}