/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.agile;

import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AgileService implements IAgileService {

   private final Log logger;
   private final IAtsServer atsServer;

   public AgileService(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public IAgileTeam getAgileTeam(Object artifact) {
      return new AgileTeam(logger, atsServer, (ArtifactReadable) artifact);
   }

   @Override
   public IAgileTeam createAgileTeam(String name, String guid) {
      ArtifactReadable userArt = atsServer.getArtifact(atsServer.getUserService().getCurrentUser());
      TransactionBuilder transaction =
         atsServer.getOrcsApi().getTransactionFactory(null).createTransaction(AtsUtilCore.getAtsBranch(), userArt,
            "Create new Agile Team");
      ArtifactReadable agileArt = (ArtifactReadable) transaction.createArtifact(AtsArtifactTypes.AgileTeam, name, guid);

      ArtifactReadable topAgileFolder = getOrCreateTopAgileFolder(transaction, userArt);
      transaction.addChildren(topAgileFolder, agileArt);

      transaction.commit();
      return getAgileTeam(agileArt);
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getOrCreateTopAgileFolder(TransactionBuilder tx, ArtifactReadable userArt) {
      ArtifactId agileFolder =
         atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(
            AtsArtifactToken.TopAgileFolder).getResults().getAtMostOneOrNull();
      if (agileFolder == null) {
         agileFolder = tx.createArtifact(AtsArtifactToken.TopAgileFolder);
         ArtifactReadable defaultHierarchyRoot =
            atsServer.getOrcsApi().getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(
               CoreArtifactTokens.DefaultHierarchyRoot).getResults().getExactlyOne();
         tx.addChildren(defaultHierarchyRoot, agileFolder);
      }
      return (ArtifactReadable) agileFolder;
   }

}
