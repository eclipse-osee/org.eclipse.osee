/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.config;

import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Angel Avila
 */
public class DemoWebDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run()  {
      TestUtil.setDemoDb(true);

      BranchId atsBranch = AtsClientService.get().getAtsBranch();
      SkynetTransaction transaction = TransactionManager.createTransaction(atsBranch, "Create ATS Folders");
      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);

      Artifact oseeWebArt = ArtifactTypeManager.addArtifact(AtsArtifactToken.WebPrograms, atsBranch);
      oseeWebArt.persist(transaction);

      Artifact demoProgramsArt = OseeSystemArtifacts.getOrCreateArtifact(DemoArtifactToken.DemoPrograms, atsBranch);
      oseeWebArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, demoProgramsArt);
      oseeWebArt.persist(transaction);

      Artifact sawTeam = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Bld_1);
      sawTeam.addRelation(CoreRelationTypes.SupportingInfo_SupportedBy, demoProgramsArt);
      sawTeam.persist(transaction);

      headingArt.addChild(oseeWebArt);
      headingArt.addChild(demoProgramsArt);
      headingArt.persist(transaction);

      transaction.execute();
   }

}
