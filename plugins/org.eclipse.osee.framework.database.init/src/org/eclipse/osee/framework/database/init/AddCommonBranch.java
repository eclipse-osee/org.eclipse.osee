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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.GlobalXViewerSettings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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

   @Override
   public void run() throws OseeCoreException {
      if (initializeRootArtifacts) {
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RootArtifact, CoreBranches.SYSTEM_ROOT,
            OseeSystemArtifacts.DEFAULT_HIERARCHY_ROOT_NAME).persist(getClass().getSimpleName());
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.UniversalGroup, CoreBranches.SYSTEM_ROOT,
            OseeSystemArtifacts.ROOT_ARTIFACT_TYPE_NAME).persist(getClass().getSimpleName());

         BranchManager.createTopLevelBranch(CoreBranches.COMMON);

         OseeTypesSetup types = new OseeTypesSetup();
         Map<String, URL> typeMap = types.getOseeTypeExtensions();

         SkynetTransaction transaction1 =
            new SkynetTransaction(BranchManager.getCommonBranch(), "Add Types to Common Branch");

         for (Entry<String, URL> entry : typeMap.entrySet()) {
            Artifact artifact =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.OseeTypeDefinition, BranchManager.getCommonBranch(),
                  entry.getKey(), GUID.create(), HumanReadableId.generate());
            artifact.setSoleAttributeValue(CoreAttributeTypes.Active, true);
            InputStream inputStream = null;
            try {
               inputStream = new BufferedInputStream(entry.getValue().openStream());
               artifact.setSoleAttributeFromStream(CoreAttributeTypes.UriGeneralStringData, inputStream);
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(inputStream);
            }
            transaction1.addArtifact(artifact);
         }
         transaction1.execute();

         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "Add Common Branch");

         //create everyone group
         Artifact everyonGroup = SystemGroup.Everyone.getArtifact();
         everyonGroup.setSoleAttributeValue(CoreAttributeTypes.DefaultGroup, true);
         everyonGroup.persist(transaction);

         // Create Default Users
         for (IUserToken userToken : SystemUser.values()) {
            UserManager.createUser(userToken, transaction);
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
}
