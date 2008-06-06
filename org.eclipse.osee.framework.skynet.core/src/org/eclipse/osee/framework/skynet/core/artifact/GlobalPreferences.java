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
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;

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
   public GlobalPreferences(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static GlobalPreferences get() throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      if (instance == null) {
         instance =
               (GlobalPreferences) ArtifactQuery.getArtifactFromTypeAndName(ARTIFACT_NAME, ARTIFACT_NAME,
                     BranchPersistenceManager.getCommonBranch());
      }
      return instance;
   }

   public static void createGlobalPreferencesArtifact() throws SQLException {
      Artifact art =
            ArtifactTypeManager.addArtifact(GlobalPreferences.ARTIFACT_NAME,
                  BranchPersistenceManager.getCommonBranch(), GlobalPreferences.ARTIFACT_NAME);
      art.persistAttributes();
   }
}