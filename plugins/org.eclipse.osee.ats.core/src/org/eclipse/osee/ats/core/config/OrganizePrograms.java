/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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
            atsApi.getQueryService().getOrCreateArtifact(AtsArtifactToken.AtsTopFolder, folder, changes);
         if (programFolder == null || programFolder.isInvalid()) {
            changes.createArtifact(AtsArtifactToken.AtsTopFolder, AtsArtifactToken.ProgramFolder);
         }
         for (ArtifactToken programArt : atsApi.getQueryService().getArtifacts(artifactType)) {
            if (!atsApi.getRelationResolver().getChildren(programFolder).contains(programArt)) {
               changes.addChild(programFolder, programArt);
            }
         }
         changes.executeIfNeeded();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

}
