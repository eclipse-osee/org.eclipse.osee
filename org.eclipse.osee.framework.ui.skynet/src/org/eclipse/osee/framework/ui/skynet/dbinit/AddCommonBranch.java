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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

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

      // Create Default Users
      for (UserEnum userEnum : UserEnum.values()) {
         SkynetAuthentication.getInstance().createUser(userEnum);
      }

      // Create Global Preferences artifact that lives on common branch
      GlobalPreferences.createGlobalPreferencesArtifact();

      // Create XViewer Customization artifact that lives on common branch
      XViewerCustomizationArtifact.getAtsCustArtifactOrCreate(true);

   }

   public List<String> getSkynetDbTypeExtensionIds() {
      List<String> skynetTypeImport = new ArrayList<String>();
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.CommonBranch");
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.ProgramAndCommon");
      return skynetTypeImport;
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
