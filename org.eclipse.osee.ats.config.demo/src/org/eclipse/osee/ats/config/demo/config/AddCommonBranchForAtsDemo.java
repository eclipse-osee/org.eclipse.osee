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

import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.init.AddCommonBranch;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.GlobalXViewerSettings;

/**
 * @author Donald G. Dunne
 */
public class AddCommonBranchForAtsDemo extends AddCommonBranch {

   @Override
   public void run() throws OseeCoreException {
      super.run();

      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
      // Create Default Users
      for (SystemUser userEnum : SystemUser.values()) {
         UserManager.createUser(userEnum, transaction);
      }
      // Create Global Preferences artifact that lives on common branch
      OseeSystemArtifacts.createGlobalPreferenceArtifact().persistAttributesAndRelations(transaction);

      // Create XViewer Customization artifact that lives on common branch
      GlobalXViewerSettings.createAtsCustomArtifact().persistAttributesAndRelations(transaction);

      // cause Osee Admin group to be created
      SystemGroup.OseeAdmin.getArtifact().persistAttributesAndRelations(transaction);

      transaction.execute();
   }
}
