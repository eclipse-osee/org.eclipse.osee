/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Collection;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeWithInheritenceSearchItem extends WorldUISearchItem {
   private final ArtifactTypeToken artifactType;

   public ArtifactTypeWithInheritenceSearchItem(String name, ArtifactTypeToken artifactType) {
      super(name, AtsImage.FLASHLIGHT);
      this.artifactType = artifactType;
   }

   private ArtifactTypeWithInheritenceSearchItem(ArtifactTypeWithInheritenceSearchItem artifactTypesSearchItem) {
      super(artifactTypesSearchItem, AtsImage.FLASHLIGHT);
      this.artifactType = artifactTypesSearchItem.artifactType;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, AtsApiService.get().getAtsBranch(),
         EXCLUDE_DELETED);
   }

   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypeWithInheritenceSearchItem(this);
   }
}