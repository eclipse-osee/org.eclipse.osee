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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Donald G. Dunne
 */
public class GlobalPreferences extends Artifact {

   private static GlobalPreferences instance;
   public static String ARTIFACT_NAME = "Global Preferences";

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public GlobalPreferences(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   public static GlobalPreferences get() throws SQLException {
      if (instance == null) {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(ARTIFACT_NAME, ARTIFACT_NAME,
                     BranchPersistenceManager.getInstance().getCommonBranch());
         instance = srch.getSingletonArtifactOrException(GlobalPreferences.class);
      }
      return instance;
   }

   public static void createGlobalPreferencesArtifact() throws SQLException {
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(GlobalPreferences.ARTIFACT_NAME,
                  ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME,
                  BranchPersistenceManager.getInstance().getCommonBranch());
      if (srch.getArtifacts(Artifact.class).size() == 0) {
         Artifact art =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     GlobalPreferences.ARTIFACT_NAME, BranchPersistenceManager.getInstance().getCommonBranch()).makeNewArtifact();
         art.setDescriptiveName(GlobalPreferences.ARTIFACT_NAME);
         art.persistAttributes();
      }
   }

}
