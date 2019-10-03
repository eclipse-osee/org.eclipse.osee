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
package org.eclipse.osee.ats.ide.demo.config;

import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Angel Avila
 */
public class DemoWebDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() {
      TestUtil.setDemoDb(true);

      BranchId atsBranch = AtsClientService.get().getAtsBranch();
      SkynetTransaction transaction = TransactionManager.createTransaction(atsBranch, "Create ATS Folders");
      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);
      headingArt.persist(transaction);

      Artifact oseeWebArt = ArtifactTypeManager.addArtifact(AtsArtifactToken.WebPrograms, atsBranch);
      headingArt.addChild(oseeWebArt);

      Artifact sawProgram =
         (Artifact) AtsClientService.get().getQueryService().getArtifact(DemoArtifactToken.SAW_PL_Program);
      oseeWebArt.addRelation(CoreRelationTypes.UniversalGrouping_Members, sawProgram);
      oseeWebArt.persist(transaction);

      transaction.execute();
   }

}
