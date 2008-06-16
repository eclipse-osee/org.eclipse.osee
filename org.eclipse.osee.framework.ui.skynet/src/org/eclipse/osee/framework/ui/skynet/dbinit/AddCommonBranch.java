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

package org.eclipse.osee.framework.ui.skynet.dbinit;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;

/**
 * This class creates the common branch and imports the appropriate skynet types. Class should be extended for plugins
 * that require extra skynet types to be added to common.
 * 
 * @author Donald G. Dunne
 */
public class AddCommonBranch implements IDbInitializationTask {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   public void run(Connection connection) throws Exception {

      // Create branch, import skynet types and initialize
      BranchPersistenceManager.getInstance().createRootBranch(null, Branch.COMMON_BRANCH_CONFIG_ID,
            Branch.COMMON_BRANCH_CONFIG_ID, getSkynetDbTypeExtensionIds(), true);
   }

   public List<String> getSkynetDbTypeExtensionIds() {
      return Arrays.asList("org.eclipse.osee.framework.skynet.core.CommonBranch",
            "org.eclipse.osee.framework.skynet.core.ProgramAndCommon", "lba.ats.config.tools.SkyNet_LbaAtsTools",
            "org.eclipse.osee.ats.ATS_Skynet_Types", "lba.ats.config.blk3.mp.SkyNet_LBA_BLK3_MP",
            "lba.ats.config.v11reu.processor.SkyNet_LBA_V11Reu_Processor",
            "lba.ats.config.v13.processor.SkyNet_LBA_V13_Processor",
            "lba.ats.config.v11reu.processor.SkyNet_LBA_V11Reu_Processor",
            "lba.ats.config.deliverable.SkyNet_LbaAtsDeliverable");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#canRun()
    */
   public boolean canRun() {
      return true;
   }
}
