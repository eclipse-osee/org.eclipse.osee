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
package org.eclipse.osee.ats.config.demo.config;

import org.eclipse.osee.ats.config.demo.DemoCISBuilds;
import org.eclipse.osee.ats.config.demo.DemoSubsystems;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Initialization class that will load configuration information for a sample DB.
 * 
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {

      TestUtil.setDemoDb(true);

      // Create SAW_Bld_1 branch
      BranchManager.createTopLevelBranch(DemoSawBuilds.SAW_Bld_1);
      populateProgramBranch(DemoSawBuilds.SAW_Bld_1);

      // Create build one branch for CIS
      BranchManager.createTopLevelBranch(DemoCISBuilds.CIS_Bld_1);
      populateProgramBranch(DemoCISBuilds.CIS_Bld_1);

   }

   private void populateProgramBranch(IOseeBranch programBranch) throws OseeCoreException {
      Artifact sawProduct =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, subsystem));
      }

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {
         Requirements.SYSTEM_REQUIREMENTS,
         Requirements.SUBSYSTEM_REQUIREMENTS,
         Requirements.SOFTWARE_REQUIREMENTS,
         Requirements.HARDWARE_REQUIREMENTS,
         "Verification Tests",
         "Validation Tests",
         "Integration Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, programBranch, name));
      }

      sawProduct.persist();
      programRoot.persist();

   }

}
