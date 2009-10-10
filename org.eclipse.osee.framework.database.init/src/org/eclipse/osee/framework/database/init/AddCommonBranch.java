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

package org.eclipse.osee.framework.database.init;

import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.GlobalXViewerSettings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * This class creates the common branch and imports the appropriate skynet types. Class should be extended for plugins
 * that require extra skynet types to be added to common.
 * 
 * @author Donald G. Dunne
 */
public abstract class AddCommonBranch implements IDbInitializationTask {
   private final boolean initializeRootArtifacts;

   public AddCommonBranch() {
      this(true);
   }

   public AddCommonBranch(boolean initializeRootArtifacts) {
      this.initializeRootArtifacts = initializeRootArtifacts;
   }

   public void run() throws OseeCoreException {
      if (initializeRootArtifacts) {
         Branch systemBranch = BranchManager.getSystemRootBranch();

         ArtifactTypeManager.addArtifact(OseeSystemArtifacts.ROOT_ARTIFACT_TYPE_NAME, systemBranch,
               OseeSystemArtifacts.DEFAULT_HIERARCHY_ROOT_NAME).persist();
         ArtifactTypeManager.addArtifact(UniversalGroup.ARTIFACT_TYPE_NAME, systemBranch,
               OseeSystemArtifacts.ROOT_ARTIFACT_TYPE_NAME).persist();

         BranchManager.createTopLevelBranch(CoreBranches.COMMON.getName(), CoreBranches.COMMON.getName(),
               CoreBranches.COMMON.getGuid());

         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());

         // Create Default Users
         for (SystemUser userEnum : SystemUser.values()) {
            UserManager.createUser(userEnum, transaction);
         }
         // Create Global Preferences artifact that lives on common branch
         OseeSystemArtifacts.createGlobalPreferenceArtifact().persist(transaction);

         // Create XViewer Customization artifact that lives on common branch
         GlobalXViewerSettings.createCustomArtifact().persist(transaction);

         // Create OseeAdmin group
         SystemGroup.OseeAdmin.getArtifact().persist(transaction);

         transaction.execute();
      }
   }

   public boolean canRun() {
      return true;
   }
}
