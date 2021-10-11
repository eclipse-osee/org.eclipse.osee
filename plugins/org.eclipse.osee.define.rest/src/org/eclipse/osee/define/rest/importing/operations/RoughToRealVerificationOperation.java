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

package org.eclipse.osee.define.rest.importing.operations;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public class RoughToRealVerificationOperation {
   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final XResultData results;
   private final RoughArtifactCollector rawData;
   private final IArtifactImportResolver artifactResolver;
   private final Map<RoughArtifact, ArtifactReadable> roughToRealArtifacts;
   private final ArtifactReadable destinationArtifact;

   public RoughToRealVerificationOperation(OrcsApi orcsApi, BranchId branchId, XResultData results, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData, IArtifactImportResolver artifactResolver, boolean deleteUnmatchedArtifacts, IArtifactExtractor extractor) {
      this.results = results;
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.rawData = rawData;
      this.artifactResolver = artifactResolver;
      this.destinationArtifact = destinationArtifact;
      this.roughToRealArtifacts = new HashMap<>();
      roughToRealArtifacts.put(rawData.getParentRoughArtifact(), destinationArtifact);
   }

   public void doWork() {
      for (RoughArtifact roughArtifact : rawData.getRoughArtifacts()) {
         ArtifactId art = artifactResolver.resolve(roughArtifact, branchId, destinationArtifact, destinationArtifact);
         if (art == null) {
            results.errorf("Artifact %s with Doors ID %s not found", roughArtifact.getName(),
               roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName()));
         }
      }
   }
}
