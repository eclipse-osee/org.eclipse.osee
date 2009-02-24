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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.dbinit.AddCommonBranch;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

/**
 * @author Donald G. Dunne
 */
public class AddCommonBranchForAtsDemo extends AddCommonBranch {

   @Override
   public void run(OseeConnection connection) throws OseeCoreException {
      super.run(connection);

      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      // Create Default Users
      for (SystemUser userEnum : SystemUser.values()) {
         UserManager.createUser(userEnum, transaction);
      }
      // Create Global Preferences artifact that lives on common branch
      GlobalPreferences.createGlobalPreferencesArtifact(transaction);

      // Create XViewer Customization artifact that lives on common branch
      XViewerCustomizationArtifact.getAtsCustArtifactOrCreate(true, transaction);
      transaction.execute();
   }

   @Override
   public List<String> getSkynetDbTypeExtensionIds() {
      return Arrays.asList("org.eclipse.osee.framework.skynet.core.OseeTypes_CommonBranch",
            "org.eclipse.osee.framework.skynet.core.OseeTypes_ProgramAndCommon", "org.eclipse.osee.ats.OseeTypes_ATS",
            "org.eclipse.osee.ats.config.demo.OseeTypes_DemoCommon");
   }
}
