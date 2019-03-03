/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class OrganizePrograms {

   private final AtsApi atsApi;
   private final ArtifactTypeToken artifactType;
   private final ArtifactToken folder;

   public OrganizePrograms(AtsApi atsApi) {
      this(atsApi, AtsArtifactTypes.Program, AtsArtifactToken.ProgramFolder);
   }

   public OrganizePrograms(AtsApi atsApi, ArtifactTypeToken artifactType, ArtifactToken folder) {
      this.atsApi = atsApi;
      this.artifactType = artifactType;
      this.folder = folder;
   }

   public XResultData run() {
      XResultData results = new XResultData();
      try {
         IAtsChangeSet changes = atsApi.createChangeSet("Organize Programs");
         ArtifactToken programFolder =
            atsApi.getQueryService().getOrCreateArtifact(CoreArtifactTokens.DefaultHierarchyRoot, folder, changes);
         if (programFolder == null || programFolder.isInvalid()) {
            changes.createArtifact(AtsArtifactToken.ProgramFolder);
         }
         for (ArtifactToken programArt : atsApi.getQueryService().getArtifacts(artifactType)) {
            if (!atsApi.getRelationResolver().getChildren(programFolder).contains(programArt)) {
               changes.addChild(programFolder, programArt);
            }
         }
         changes.execute();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

}
