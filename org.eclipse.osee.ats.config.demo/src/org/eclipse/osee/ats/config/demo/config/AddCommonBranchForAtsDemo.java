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

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.skynet.dbinit.AddCommonBranch;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

/**
 * @author Donald G. Dunne
 */
public class AddCommonBranchForAtsDemo extends AddCommonBranch {

   public void run(Connection connection) throws Exception {
      super.run(connection);

      // Create Default Users
      for (UserEnum userEnum : UserEnum.values()) {
         SkynetAuthentication.getInstance().createUser(userEnum);
      }

      // Create Global Preferences artifact that lives on common branch
      GlobalPreferences.createGlobalPreferencesArtifact();

      // Create XViewer Customization artifact that lives on common branch
      XViewerCustomizationArtifact.getAtsCustArtifactOrCreate(true);

   }

   @Override
   public List<String> getSkynetDbTypeExtensionIds() {
      return Arrays.asList("org.eclipse.osee.framework.skynet.core.CommonBranch",
            "org.eclipse.osee.framework.skynet.core.ProgramAndCommon", "org.eclipse.osee.ats.ATS_Skynet_Types",
            "org.eclipse.osee.ats.config.demo.Demo_Common_Skynet_Types", "org.eclipse.osee.ats.ATS_Skynet_Types");
   }
}
